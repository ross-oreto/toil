package io.oreto.toil.dsl;

import io.oreto.toil.provider.DbProvider;

public class Constant<T> implements Expressible<T> {
    public static <T> Constant<T> of(T value) {
        return new Constant<>(value);
    }
    private final T value;
    private Table table;

    protected Constant(T value) {
        this.value = value;
    }

    public T getValue() {
        return value;
    }

    @Override
    public SQL express(DbProvider dbProvider) {
        return SQL.of(CharSequence.class.isAssignableFrom(value.getClass())
                ? String.format("'%s'", value)
                : value.toString());
    }

    @Override
    public Table getTable() {
        return table;
    }

    Constant<T> associate(Table table) {
       this.table = table;
       return this;
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
