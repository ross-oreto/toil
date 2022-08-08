package io.oreto.toil.dsl;


import java.util.Collection;
import java.util.List;

public class SQL {
    public static SQL of(String sql, Object... parameters) {
        return new SQL(sql, parameters);
    }

    protected SQL(String sql, Object... parameters) {
        this.sql = sql;
        this.parameters = List.of(parameters);
    }

    private final String sql;
    private final Collection<Object> parameters;

    public String getSql() {
        return sql;
    }

    public Collection<Object> getParameters() {
        return parameters;
    }

    public boolean hasParameters() {
        return !parameters.isEmpty();
    }

    @Override
    public String toString() {
        return sql;
    }
}
