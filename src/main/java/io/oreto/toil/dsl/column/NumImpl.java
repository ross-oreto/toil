package io.oreto.toil.dsl.column;

import io.oreto.toil.dsl.Table;

public class NumImpl<T extends Number> extends ColumnImpl<T> implements Num<T> {
    public NumImpl(String name, Class<T> type, boolean nullable, Table table) {
        super(name, type, nullable, table);
    }
}
