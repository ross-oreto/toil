package io.oreto.toil.dsl.column;

import io.oreto.toil.dsl.Table;

public class LongImpl extends ColumnImpl<Long> implements Num<Long> {
    public LongImpl(String name, boolean nullable, Table table) {
        super(name, Long.class, nullable, table);
    }
}
