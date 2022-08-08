package io.oreto.toil.dsl.function;


import io.oreto.toil.dsl.query.Orderable;

public abstract class Func<R> implements Orderable<R> {
    public enum Names {
        LOWER, UPPER, CONCAT, NEXTVAL
    }
    private final String name;


    protected Func(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public Direction getDirection() {
        return null;
    }
}
