package io.oreto.toil.dsl.query;

import io.oreto.toil.dsl.filter.Condition;
import io.oreto.toil.dsl.filter.Where;
import io.oreto.toil.provider.DbProvider;
import io.oreto.toil.provider.Result;

import java.sql.SQLException;
import java.util.List;

public class SelectWhere extends Where implements Fetchable {
    private final Select select;

    SelectWhere(Select select, Condition... conditions) {
        super(conditions);
        this.select = select;
    }

    @Override
    public DbProvider getProvider() {
        return select.getProvider();
    }


    @Override
    public Result<java.lang.Record> fetch() throws SQLException {
        return getProvider().fetch(select);
    }

    @Override
    public <T> Result<T> fetch(Mappable<T> mappable) throws SQLException {
        return getProvider().fetch(select, mappable);
    }

    @Override
    public List<Orderable<?>> getOrderables() {
        return select.getOrderables();
    }

    @Override
    public final Fetchable offset(Integer offset) {
        return select.offset(offset);
    }

    @Override
    public final Fetchable limit(Integer limit) {
        return select.limit(limit);
    }

    public final Fetchable noLimit() {
        return select.noLimit();
    }

    @Override
    public Integer getOffset() {
        return select.getOffset();
    }

    @Override
    public Integer getLimit() {
        return select.getLimit();
    }

    public SelectWhere orderBy(Orderable... orderables) {
        select.orderBy(orderables);
        return this;
    }

    @Override
    public SelectWhere and(Condition... conditions) {
        super.and(conditions);
        return this;
    }

    @Override
    public SelectWhere or(Condition... conditions) {
        super.or(conditions);
        return this;
    }

    @Override
    public SelectWhere andOr(Condition... conditions) {
        super.andOr(conditions);
        return this;
    }

    @Override
    public SelectWhere orAnd(Condition... conditions) {
        super.orAnd(conditions);
        return this;
    }
}
