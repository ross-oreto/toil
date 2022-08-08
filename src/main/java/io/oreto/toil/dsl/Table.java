package io.oreto.toil.dsl;

import io.oreto.toil.dsl.column.Column;

public interface Table {
    String getTableName();
    String getSchema();
    default String qualify() {
        return getSchema() == null ? getTableName() : String.format("%s.%s", getSchema(), getTableName());
    }
    Column<?>[] getColumns();
    Column<?>[] getPrimaryKey();

}
