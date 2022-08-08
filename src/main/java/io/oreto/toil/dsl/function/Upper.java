package io.oreto.toil.dsl.function;


import io.oreto.toil.dsl.Constant;
import io.oreto.toil.dsl.Expressible;
import io.oreto.toil.dsl.SQL;
import io.oreto.toil.provider.DbProvider;

public class Upper extends Func<CharSequence> {
    private final Expressible<CharSequence> parameter;

    public Upper(CharSequence parameter) {
        this(Constant.of(parameter));
    }

    public Upper(Expressible<CharSequence> parameter) {
        super(Names.UPPER.name());
        this.parameter = parameter;
    }

    @Override
    public SQL express(DbProvider dbProvider) {
        return dbProvider.upper(parameter);
    }
}
