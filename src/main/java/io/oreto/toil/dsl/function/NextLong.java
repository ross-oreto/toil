package io.oreto.toil.dsl.function;

import io.oreto.toil.dsl.SQL;
import io.oreto.toil.dsl.sequence.Sequence;
import io.oreto.toil.provider.DbProvider;

public class NextLong extends Func<Long> {
    private final Sequence<Long> sequence;

    public NextLong(Sequence<Long> sequence) {
        super(Names.NEXTVAL.name());
        this.sequence = sequence;
    }

    @Override
    public SQL express(DbProvider dbProvider) {
        return dbProvider.nextVal(sequence);
    }
}
