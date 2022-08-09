package io.oreto.toil.dsl.filter;


import io.oreto.toil.dsl.expression.Expressible;
import io.oreto.toil.dsl.expression.SQL;
import io.oreto.toil.provider.DbProvider;

public enum Operator implements Expressible<Operator> {
    EQ("=")
    , NE("!=")
    , GT(">")
    , GTE(">=")
    , LT("<")
    , LTE("<=")
    , IN("IN")
    , NOT_IN("NOT IN")
    , EXISTS("EXISTS")
    , LIKE("LIKE")
    , NOT_LIKE("NOT LIKE")
    , BETWEEN("BETWEEN")
    , NOT_BETWEEN("NOT BETWEEN")
    , IS_NULL("IS NULL")
    , IS_NOT_NULL("IS NOT NULL")
    , ANY("ANY")
    , ALL("ALL")
    , AND("AND")
    , OR("OR");

    Operator(String s) {
        this.s = s;
    }

    private final String s;

    @Override
    public String toString() {
        return s;
    }

    public static boolean isValid(String operator) {
        for (Operator op : values())
            if (op.toString().equalsIgnoreCase(operator))
                return true;
        return false;
    }

    @Override
    public SQL express(DbProvider dbProvider) {
        return SQL.of(s);
    }
}
