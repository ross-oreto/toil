package io.oreto.toil.dsl.expression;

import io.oreto.toil.dsl.filter.Condition;
import io.oreto.toil.dsl.filter.Operator;
import io.oreto.toil.dsl.query.Alias;
import io.oreto.toil.dsl.query.Orderable;
import io.oreto.toil.dsl.table.Table;
import io.oreto.toil.provider.DbProvider;

import java.io.Serializable;

// Where the expression can be a constant, function,
// any combination of column names, constants, and functions connected by an operator or operators, or a sub-query.
public interface Expressible<T> {
    SQL express(DbProvider dbProvider);

    default Condition eq(Serializable value) {
        return new Condition(this, Operator.EQ, Param.of(value));
    }

    default Condition eq(T value) {
        return new Condition(this, Operator.EQ, Param.of(value));
    }
    
    default Condition eq(Expressible<T> value) {
        return new Condition(this, Operator.EQ, value);
    }

    default Condition ne(T value) {
        return new Condition(this, Operator.NE, Param.of(value));
    }

    default Condition gt(T value) {
        return new Condition(this, Operator.GT, Param.of(value));
    }

    default Condition gte(T value) {
        return new Condition(this, Operator.GTE, Param.of(value));
    }

    default Condition lt(T value) {
        return new Condition(this, Operator.LT, Param.of(value));
    }

    default Condition lte(T value) {
        return new Condition(this, Operator.LTE, Param.of(value));
    }

    default Condition isNull() {
        return new Condition(this, Operator.IS_NULL);
    }

    default Condition isNotNull() {
        return new Condition(this, Operator.IS_NOT_NULL);
    }

    default Orderable<T> asc() {
        Expressible<T> tExpressible = this;
        return new Orderable<T>() {
            @Override public Direction getDirection() { return Direction.asc; }
            @Override public SQL express(DbProvider dbProvider) { return tExpressible.express(dbProvider); }
        };
    }

    default Orderable<T> desc() {
        Expressible<T> tExpressible = this;
        return new Orderable<T>() {
            @Override public Direction getDirection() { return Direction.desc; }
            @Override public SQL express(DbProvider dbProvider) { return tExpressible.express(dbProvider); }
        };
    }

    default Expressible<T> as(String alias) {
        return Alias.create(this, alias);
    }

    default boolean isAliased() {
        return false;
    }

    default Alias<T> getAlias() {
        return null;
    }

    default Table getTable() { return null; }
}
