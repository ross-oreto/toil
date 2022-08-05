package io.oreto.toil.dsl.function;


import io.oreto.toil.dsl.Constant;
import io.oreto.toil.dsl.Expressible;

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
    public String express() {
        return expressFunction(parameter);
    }
}
