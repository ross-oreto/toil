package io.oreto.toil.dsl.function;


import io.oreto.toil.dsl.expression.Expressible;
import io.oreto.toil.dsl.query.Orderable;

public abstract class Func<R> implements Orderable<R> {
    public static Count count() {
        return new Count();
    }
    public static Count count(Expressible<?> expressible) {
        return new Count(expressible);
    }

    public static Lower lower(CharSequence string) {
        return new Lower(string);
    }

    public static Upper upper(CharSequence string) {
        return new Upper(string);
    }

    public enum Names {
        LOWER, UPPER, CONCAT, NEXTVAL, COUNT
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
