package io.oreto.toil.dsl.function;


import io.oreto.toil.dsl.Expressible;
import io.oreto.toil.dsl.SQL;
import io.oreto.toil.provider.DbProvider;

public class Count extends Func<CharSequence> {
    private final Expressible<?> parameter;

    public Count(Expressible<?> parameter) {
        super(Names.COUNT.name());
        this.parameter = parameter;
    }

    public Count() {
        this(null);
    }

    @Override
    public SQL express(DbProvider dbProvider) {
        return dbProvider.count(parameter);
    }
}
