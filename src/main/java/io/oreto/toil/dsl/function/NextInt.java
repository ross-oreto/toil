package io.oreto.toil.dsl.function;

import io.oreto.toil.dsl.expression.SQL;
import io.oreto.toil.dsl.sequence.Sequence;
import io.oreto.toil.provider.DbProvider;

public class NextInt extends Func<Integer> {
    private final Sequence<Integer> sequence;

    public NextInt(Sequence<Integer> sequence) {
        super(Names.NEXTVAL.name());
        this.sequence = sequence;
    }

    @Override
    public SQL express(DbProvider dbProvider) {
        return dbProvider.nextVal(sequence);
    }
}
