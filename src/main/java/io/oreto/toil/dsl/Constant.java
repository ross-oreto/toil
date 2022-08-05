package io.oreto.toil.dsl;

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
    public String express() {
        return CharSequence.class.isAssignableFrom(value.getClass())
                ? String.format("'%s'", value)
                : value.toString();
    }

    @Override
    public Table getTable() {
        return table;
    }

    Constant<T> associate(Table table) {
       this.table = table;
       return this;
    }
}
