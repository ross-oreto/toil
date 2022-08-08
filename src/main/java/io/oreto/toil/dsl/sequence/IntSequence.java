package io.oreto.toil.dsl.sequence;

public interface IntSequence extends Sequence<Integer> {
    default Class<Integer> getType() {
        return Integer.class;
    }
}
