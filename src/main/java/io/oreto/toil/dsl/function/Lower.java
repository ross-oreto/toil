package io.oreto.toil.dsl.function;


import io.oreto.toil.dsl.expression.Constant;
import io.oreto.toil.dsl.expression.Expressible;
import io.oreto.toil.dsl.expression.SQL;
import io.oreto.toil.provider.DbProvider;

public class Lower extends Func<CharSequence> {
    private final Expressible<CharSequence> parameter;

    public Lower(CharSequence parameter) {
        this(Constant.of(parameter));
    }

    public Lower(Expressible<CharSequence> parameter) {
        super(Names.LOWER.name());
        this.parameter = parameter;
    }

    @Override
    public SQL express(DbProvider dbProvider) {
        return dbProvider.lower(parameter);
    }
}
