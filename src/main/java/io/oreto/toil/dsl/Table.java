package io.oreto.toil.dsl;

import io.oreto.toil.dsl.column.Column;

public interface Table {
    String getTableName();
    String getSchema();
    Column<?>[] getColumns();
}
