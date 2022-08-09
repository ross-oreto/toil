package io.oreto.toil.dsl.column;

import io.oreto.toil.dsl.table.Table;

public class IntImpl extends ColumnImpl<Integer> implements Num<Integer> {
    public IntImpl(String name, boolean nullable, Table table) {
        super(name, Integer.class, nullable, table);
    }
}
