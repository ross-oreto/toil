package io.oreto.toil.dsl.column;


import io.oreto.toil.dsl.expression.Expressible;
import io.oreto.toil.dsl.expression.SQL;
import io.oreto.toil.dsl.query.Alias;
import io.oreto.toil.dsl.table.Table;
import io.oreto.toil.provider.DbProvider;

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
    public SQL express(DbProvider dbProvider) {
        return SQL.of(String.format("%s.%s", table.qualify(), name));
    }

    @Override
    public Direction getDirection() {
        return null;
    }
}
