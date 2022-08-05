package io.oreto.toil.dsl.column;

import io.oreto.toil.dsl.Table;
import io.oreto.toil.dsl.query.Orderable;

public interface Column<T> extends ColumnInfo, Orderable<T> {
    @Override
    Table getTable();
}
