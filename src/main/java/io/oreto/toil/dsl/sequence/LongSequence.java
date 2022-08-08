package io.oreto.toil.dsl.sequence;

public interface LongSequence extends Sequence<Long> {
    default Class<Long> getType() {
        return Long.class;
    }
}
