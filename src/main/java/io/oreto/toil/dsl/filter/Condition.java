package io.oreto.toil.dsl.filter;

import io.oreto.toil.dsl.Expressible;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class Condition {
    Expressible<?> expression1;
    Operator operator;
    Expressible<?> expression2;

    private final List<Object> params;

    public Condition(Expressible<?> expression1) {
        this.params = new ArrayList<>();
        this.expression1 = expression1;
        if (expression1 instanceof Param) {
            params.add(((Param<?>) expression1).getValue());
        }
    }

    public Condition(Expressible<?> expression1, Operator operator) {
        this(expression1);
        this.operator = operator;
    }

    public Condition(Expressible<?> expression1, Operator operator, Expressible<?> expression2) {
        this(expression1, operator);
        this.expression2 = expression2;
        if (expression2 instanceof Param) {
            params.add(((Param<?>) expression2).getValue());
        }
    }

    @Override
    public String toString() {
        return Arrays.stream(new Expressible[] { expression1, operator, expression2 })
                .filter(Objects::nonNull).map(Expressible::express)
                .collect(Collectors.joining(" "));
    }

    public List<Object> getParams() {
        return params;
    }
}
