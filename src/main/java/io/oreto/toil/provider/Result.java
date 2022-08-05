package io.oreto.toil.provider;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.oreto.toil.Toil;
import io.oreto.toil.dsl.Expressible;
import io.oreto.toil.dsl.Table;
import io.oreto.toil.dsl.query.Mappable;
import io.oreto.toil.dsl.query.Select;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.*;

import io.oreto.toil.dsl.query.Record;

public class Result<T> {
   protected final List<T> records;
   protected final List<SQLException> exceptions;

   @SuppressWarnings("unchecked")
   Result(Select select, ResultSet resultSet, Mappable<T> mappable) {
      this.exceptions = new ArrayList<>();
      this.records = new ArrayList<>();
      if (this.getClass().equals(RecordResult.class)) {
         try {
            this.records.addAll ((List<T>) Record.of(resultSet));
         } catch (SQLException sqlException) {
            exceptions.add(sqlException);
         }
      } else {
        mapRecord(select, resultSet, mappable);
      }
   }

   private void mapRecord(Select select, ResultSet resultSet, Mappable<T> mappable) {
      final Map<Integer, Class<?>> iClassMap = buildClassIndex(select, mappable.getTableClassMap());
      final Map<Class<?>, Map<Object, Object>> store = new WeakHashMap<>();

      final Map<Class<?>, Map<String, Object>> classValues = new WeakHashMap<>();
      final Map<Class<?>, Object> classObjects = new WeakHashMap<>();
      final Set<Class<?>> newObjects = new HashSet<>();
      final ObjectMapper objectMapper = Toil.JsonProvider.get();

      try {
         while (resultSet.next()) {
            // reset row state for each record
            classValues.clear(); classObjects.clear(); newObjects.clear();

            buildClassValues(resultSet, iClassMap, classValues);
            convertClassValues(classValues, objectMapper, store, mappable.getRootType(), newObjects, classObjects);

            // Make any associations defined on the row
            mappable.getAssociations().forEach(((association, biConsumer) -> {
               if (newObjects.contains(association.getChild())) {
                  biConsumer.accept(classObjects.get(association.getParent()), classObjects.get(association.getChild()));
               }
            }));
         }
      } catch (SQLException sqlException) {
        exceptions.add(sqlException);
      }
   }

   private Map<Integer, Class<?>> buildClassIndex(Select select, Map<Table, Class<?>> tableClassMap) {
      Map<Integer, Class<?>> iClassMap = new WeakHashMap<>();
      List<Expressible<?>> expressibles = select.getExpressibles();
      int size = expressibles.size();
      for (int i = 0; i < size; i++) {
         Table table = expressibles.get(i).getTable();
         if (table == null)
            table = select.getFrom().get(0);
         iClassMap.put(i, tableClassMap.get(table));
      }
      return iClassMap;
   }

   private void buildClassValues(ResultSet resultSet
           , Map<Integer, Class<?>> iClassMap, Map<Class<?>
           , Map<String, Object>> classValues) throws SQLException {
      ResultSetMetaData metaData = resultSet.getMetaData();
      for (int i = 1; i <= metaData.getColumnCount(); i++) {
         String name = metaData.getColumnName(i);
         Object val = resultSet.getObject(i);
         Class<?> aClass = iClassMap.get(i - 1);
         Map<String, Object> vals = classValues.get(aClass);
         if (vals == null) {
            classValues.put(aClass, new WeakHashMap<String, Object>(){{ put(name, val); }});
         } else
            vals.put(name, val);
      }
   }

   @SuppressWarnings("unchecked")
   private void convertClassValues(Map<Class<?>, Map<String, Object>> classValues
           , ObjectMapper objectMapper
           , Map<Class<?>, Map<Object, Object>> store
           , Class<T> rootType
           , Set<Class<?>> newObjects
           , Map<Class<?>, Object> classObjects) {
      for (Class<?> aClass : classValues.keySet()) {
         Object o = objectMapper.convertValue(classValues.get(aClass), aClass);
         Map<Object, Object> classStore = store.get(aClass);
         if (Objects.nonNull(classStore) && classStore.containsKey(o)) {
            // object already exists in store
            o = classStore.get(o);
         } else {
            // new object
            if (rootType.equals(aClass)) {
               this.records.add((T) o);
            }
            if (classStore == null) {
               classStore = new WeakHashMap<>();
               classStore.put(o, o);
               store.put(aClass, classStore);
            } else {
               classStore.put(o, o);
            }
            newObjects.add(aClass);
         }
         classObjects.put(aClass, o);
      }
   }

   public List<T> getRecords() {
      return records;
   }

   public Optional<T> getOneRecord() {
      return isEmpty() ? Optional.empty() : Optional.of(records.get(0));
   }

   public T getFirstRecord() {
      return isEmpty() ? null : records.get(0);
   }

   public T getLastRecord() {
      return isEmpty() ? null : records.get(size() - 1);
   }

   public int size() {
      return records.size();
   }

   public boolean isSingle(){
      return size() == 1;
   }

   public boolean isPlural() {
      return !isSingle();
   }

   public boolean isEmpty() {
      return records.isEmpty();
   }
}
