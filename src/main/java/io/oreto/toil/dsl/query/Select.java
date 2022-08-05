package io.oreto.toil.dsl.query;

import io.oreto.toil.dsl.*;
import io.oreto.toil.dsl.filter.Condition;
import io.oreto.toil.provider.DbProvider;
import io.oreto.toil.provider.Result;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class Select implements Fetchable {
    private final DbProvider provider;
    private final List<Expressible<?>> expressibles;
    private final List<Table> from;
    private SelectWhere where;
    private Integer offset;
    private Integer limit;

    private final List<Orderable<?>> orderables;

    public Select(DbProvider provider, Expressible<?>... expressibles) {
        this.provider = provider;
        this.expressibles = new ArrayList<>(Arrays.asList(expressibles));
        this.from = new ArrayList<>();
        this.orderables = new ArrayList<>();
        this.limit = 500;
    }

    public Select from(Table... tables) {
        this.from.addAll(Arrays.asList(tables));
        if (expressibles.isEmpty()) {
           from.forEach(table -> expressibles.addAll(Arrays.asList(table.getColumns())));
        }
        return this;
    }

    @Override
    public Result<java.lang.Record> fetch() throws SQLException {
        return getProvider().fetch(this);
    }

    @Override
    public <T> Result<T> fetch(Mappable<T> mappable) throws SQLException {
        return getProvider().fetch(this, mappable);
    }

    public List<Expressible<?>> getExpressibles() {
        return expressibles;
    }

    public List<Table> getFrom() {
        return from;
    }

    public SelectWhere getWhere() {
        return where;
    }

    public boolean isFiltered() {
        return Objects.nonNull(where);
    }

    @Override
    public List<Orderable<?>> getOrderables() {
        return orderables;
    }

    @Override
    public Select offset(Integer offset) {
        this.offset = offset;
        return this;
    }

    public Select limit(Integer limit) {
        this.limit = limit;
        return this;
    }

    public Select noLimit() {
        this.limit = null;
        return this;
    }

    @Override
    public Integer getOffset() {
        return offset;
    }

    @Override
    public Integer getLimit() {
        return limit;
    }

    public SelectWhere where(Condition... conditions) {
        this.where = new SelectWhere(this, conditions);
        return where;
    }

    public Select orderBy(Orderable<?>... orderables) {
        this.orderables.addAll(Arrays.asList(orderables));
        return this;
    }

    public SQL toSQL() {
        return getProvider().toSQL(this);
    }

    @Override
    public DbProvider getProvider() {
        return provider;
    }
}
