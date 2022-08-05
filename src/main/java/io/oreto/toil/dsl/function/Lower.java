package io.oreto.toil.dsl.function;


import io.oreto.toil.dsl.Constant;
import io.oreto.toil.dsl.Expressible;

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
    public String express() {
        return expressFunction(parameter);
    }
}
