package io.oreto.toil.dsl.query;

import io.oreto.toil.dsl.Expressible;

public class Alias<T> implements Expressible<T> {
    public static <T> Alias<T> create(Expressible<T> expressible, String alias) {
        return new Alias<>(expressible, alias);
    }

    public final Expressible<T> expressible;
    private final String aliasName;

    protected Alias(Expressible<T> expressible, String alias) {
        this.expressible = expressible;
        this.aliasName = alias;
    }

    public String create() {
        return String.format("%s AS %s", expressible.express(), getAliasName());
    }

    public String getAliasName() {
        return aliasName;
    }

    @Override
    public String express() {
        return aliasName;
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
