package io.oreto.toil.dsl.column;


import io.oreto.toil.dsl.Expressible;
import io.oreto.toil.dsl.Table;
import io.oreto.toil.dsl.query.Alias;

public class ColumnImpl<T> implements Column<T> {

    private final String name;
    private final Class<T> type;
    private final boolean nullable;
    private final Table table;

    public ColumnImpl(String name, Class<T> type, boolean nullable, Table table) {
        this.name = name;
        this.type = type;
        this.nullable = nullable;
        this.table = table;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Class<T> getType() {
        return type;
    }

    @Override
    public boolean isNullable() {
        return nullable;
    }

    @Override
    public Table getTable() {
        return table;
    }

    @Override
    public Expressible<T> as(String alias) {
        return Alias.create(this, alias);
    }

    @Override
    public String express() {
        return String.format("%s.%s", table.getTableName(), name);
    }

    @Override
    public Direction getDirection() {
        return null;
    }
}
