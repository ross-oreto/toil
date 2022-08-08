package io.oreto.toil.dsl.sequence;

import io.oreto.toil.dsl.Expressible;
import io.oreto.toil.dsl.SQL;
import io.oreto.toil.provider.DbProvider;

public interface Sequence<T extends Number> extends Expressible<T> {
    String getSequenceName();

    String getSchema();

    Class<T> getType();

    default String qualify() {
        return getSchema() == null ? getSequenceName() : String.format("%s.%s", getSchema(), getSequenceName());
    }

    default SQL express(DbProvider dbProvider) {
        return SQL.of(qualify());
    }
}
