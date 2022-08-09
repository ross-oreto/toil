package io.oreto.toil.dsl.query;

import io.oreto.toil.dsl.Expressible;
import io.oreto.toil.dsl.SQL;
import io.oreto.toil.provider.DbProvider;

public class Alias<T> implements Expressible<T> {
    public static <T> Alias<T> create(Expressible<T> expressible, String alias) {
        return new Alias<>(expressible, alias);
    }

    private final Expressible<T> expressible;
    private final String aliasName;

    protected Alias(Expressible<T> expressible, String alias) {
        this.expressible = expressible;
        this.aliasName = alias;
    }

    public SQL create(DbProvider dbProvider) {
        SQL sql = expressible.express(dbProvider);
        return SQL.of(String.format("%s AS %s", sql, getAliasName()), sql.getParameterArray());
    }

    public String getAliasName() {
        return aliasName;
    }

    @Override
    public SQL express(DbProvider dbProvider) {
        return SQL.of(aliasName);
    }

    @Override
    public String toString() {
        return aliasName;
    }

    @Override
    public boolean isAliased() {
        return true;
    }

    @Override
    public Alias<T> getAlias() {
        return this;
    }
}
