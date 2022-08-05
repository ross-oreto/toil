package io.oreto.toil.dsl;


public class SQL {
    public static SQL of(String sql, Object... parameters) {
        return new SQL(sql, parameters);
    }

    protected SQL(String sql, Object... parameters) {
        this.sql = sql;
        this.parameters = parameters;
    }

    private final String sql;
    private final Object[] parameters;

    public String getSql() {
        return sql;
    }

    public Object[] getParameters() {
        return parameters;
    }
}
