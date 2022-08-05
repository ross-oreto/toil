package io.oreto.toil.dsl.query;

import io.oreto.toil.dsl.Expressible;

public interface Orderable<T> extends Expressible<T> {
    enum Direction {
        asc, desc
    }

    Direction getDirection();
}
