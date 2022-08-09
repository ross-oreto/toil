package io.oreto.toil.dsl.table;

import io.oreto.toil.dsl.column.Column;

import java.io.Serializable;
import java.util.Collection;

public interface Table {
    String getTableName();
    String getSchema();
    default String qualify() {
        return getSchema() == null ? getTableName() : String.format("%s.%s", getSchema(), getTableName());
    }
    Column<?>[] getColumns();
    Collection<Column<? extends Serializable>> getPrimaryKey();
}
