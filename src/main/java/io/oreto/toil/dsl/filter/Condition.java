package io.oreto.toil.dsl.filter;

import io.oreto.toil.dsl.Expressible;
import io.oreto.toil.dsl.SQL;
import io.oreto.toil.provider.DbProvider;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class Condition {
    Expressible<?> expression1;
    Operator operator;
    Expressible<?> expression2;

    public Condition(Expressible<?> expression1) {
        this.expression1 = expression1;
    }

    public Condition(Expressible<?> expression1, Operator operator) {
        this(expression1);
        this.operator = operator;
    }

    public Condition(Expressible<?> expression1, Operator operator, Expressible<?> expression2) {
        this(expression1, operator);
        this.expression2 = expression2;
    }

    public SQL toSQL(DbProvider dbProvider) {
        List<Object> parameters = new ArrayList<>();
        return SQL.of(Arrays.stream(new Expressible[] { expression1, operator, expression2 })
                .filter(Objects::nonNull).map(expressible -> {
                    SQL sql = expressible.express(dbProvider);
                    if (sql.hasParameters())
                        parameters.addAll(sql.getParameters());
                    return sql.getSql();
                })
                .collect(Collectors.joining(" ")), parameters.toArray());
    }
}
