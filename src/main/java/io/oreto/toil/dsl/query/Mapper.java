package io.oreto.toil.dsl.query;

import io.oreto.toil.dsl.table.Table;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

public class Mapper<T> implements Mappable<T> {
    public static class Association<P, C> {
        private final Class<P> parent;
        private final Class<C> child;

        private Association(Class<P> parent, Class<C> child) {
            this.parent = parent;
            this.child = child;
        }

        public Class<P> getParent() {
            return parent;
        }
        public Class<C> getChild() {
            return child;
        }
    }

    public static <T> Mapper<T> of(Class<T> tClass, Table... tables) {
        return new Mapper<>(tClass, tables);
    }

    private final Class<T> rootClass;

    private final Map<Table, Class<?>> tableClassMap;

    private final Map<Association<?, ?>, BiConsumer<Object, Object>> associations;

    protected Mapper(Class<T> tClass, Table... tables) {
        this.rootClass = tClass;
        this.tableClassMap = new HashMap<>();
        for (Table table : tables)
            this.tableClassMap.put(table, tClass);
        this.associations = new HashMap<>();
    }

    public Class<T> getRootType() {
        return rootClass;
    }

    public Map<Table, Class<?>> getTableClassMap() {
        return tableClassMap;
    }

    public Map<Association<?, ?>, BiConsumer<Object, Object>> getAssociations() {
        return associations;
    }

    public <C> Mapper<T> map(Class<C> tClass, Table... tables) {
        for (Table table : tables)
            this.tableClassMap.put(table, tClass);
        return this;
    }

    @SuppressWarnings("unchecked")
    public <P, C> Mapper<T> associate(Class<P> parent, Class<C> child, BiConsumer<P, C> associate) {
        this.associations.put(new Association<>(parent, child), (BiConsumer<Object, Object>) associate);
        return this;
    }
}
