package io.oreto.toil.dsl.query;

import io.oreto.toil.dsl.table.Table;

import java.util.Map;
import java.util.function.BiConsumer;

public interface Mappable<T> {
    Class<T> getRootType();
    Map<Table, Class<?>> getTableClassMap();

    Map<Mapper.Association<?, ?>, BiConsumer<Object, Object>> getAssociations();

}
