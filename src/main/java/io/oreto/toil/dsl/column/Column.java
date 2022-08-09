package io.oreto.toil.dsl.column;

import io.oreto.toil.dsl.query.Orderable;
import io.oreto.toil.dsl.table.Table;

public interface Column<T> extends ColumnInfo, Orderable<T> {
    @Override
    Table getTable();
}
