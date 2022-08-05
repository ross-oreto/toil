package io.oreto.toil.dsl.function;


import io.oreto.toil.dsl.*;
import io.oreto.toil.dsl.query.Orderable;

import java.util.Arrays;
import java.util.stream.Collectors;

public abstract class Func<R> implements Orderable<R> {
    public enum Names {
        LOWER, UPPER, CONCAT
    }
    private final String name;


    protected Func(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    protected String expressFunction(Expressible<?>... parameters) {
        return String.format("%s(%s)"
                , name, Arrays.stream(parameters).map(Expressible::express).collect(Collectors.joining(", ")));
    }

    @Override
    public Direction getDirection() {
        return null;
    }
}
