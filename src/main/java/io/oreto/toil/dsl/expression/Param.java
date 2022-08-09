package io.oreto.toil.dsl.expression;

import io.oreto.toil.provider.DbProvider;

public class Param<T> implements Expressible<T> {
    public static <T> Param<T> of(T value) {
        return new Param<>(value);
    }
    private final T value;

    protected Param(T value) {
        this.value = value;
    }

    public T getValue() {
        return value;
    }

    @Override
    public SQL express(DbProvider dbProvider) {
        return SQL.of("?", value);
    }
}
