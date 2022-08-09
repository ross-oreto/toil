package io.oreto.toil.dsl.insert;

import io.oreto.toil.dsl.Expressible;
import io.oreto.toil.dsl.Table;
import io.oreto.toil.dsl.column.Column;
import io.oreto.toil.dsl.filter.Param;
import io.oreto.toil.dsl.function.Func;
import io.oreto.toil.dsl.query.Mappable;
import io.oreto.toil.dsl.query.Select;
import io.oreto.toil.provider.DbProvider;
import io.oreto.toil.provider.Result;
import io.oreto.toil.provider.RowResult;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Insert {
    private final DbProvider provider;

    private final Table table;

    private final List<Column<?>> columns;

    private final List<Expressible<?>> values;

    private Select selectValues;

    private List<Column<?>> returning;

    public Insert(DbProvider provider, Table table, Column<?>... columns) {
        this.provider = provider;
        this.table = table;
        this.columns = new ArrayList<>(List.of(columns));
        this.values = new ArrayList<>();
    }

    public <T> Insert value(Column<T> column, T value) {
        this.columns.add(column);
        this.values.add(Param.of(value));
        return this;
    }

    public <T> Insert value(Column<T> column, Func<T> value) {
        this.columns.add(column);
        this.values.add(value);
        return this;
    }

    public Insert values(Object... values) {
        for (Object value : values) {
            if (value == null)
                this.values.add(Param.of(null));
            else if (Expressible.class.isAssignableFrom(value.getClass()))
                this.values.add((Expressible<?>) value);
            else
                this.values.add(Param.of(value));
        }
        return this;
    }

    public Insert values(Select selectValues) {
        this.selectValues = selectValues;
        return this;
    }

    public Insert returning(Column<?>... columns) {
        this.returning = List.of(columns.length == 0 ? table.getColumns() : columns);
        return this;
    }

    public int execute() throws SQLException {
        return provider.execute(this);
    }

    public RowResult fetch() throws SQLException {
        return provider.fetch(this);
    }

    public <T> Result<T> fetch(Mappable<T> mappable) throws SQLException {
        return provider.fetch(this, mappable);
    }

    public Table getTable() {
        return table;
    }

    public List<Column<?>> getColumns() {
        return columns;
    }

    public List<Expressible<?>> getValues() {
        return values;
    }

    public Select getSelectValues() {
        return selectValues;
    }

    public List<Column<?>> getReturning() {
        return returning;
    }
}
