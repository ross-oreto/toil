package io.oreto.toil.test.db;

import io.oreto.toil.dsl.function.NextLong;
import io.oreto.toil.dsl.sequence.LongSequence;

public class DbSequence implements LongSequence {
    public static final DbSequence DB_SEQUENCE = new DbSequence();

    public final NextLong nextval = new NextLong(this);

    @Override
    public String getSequenceName() {
        return "db_sequence";
    }

    @Override
    public String getSchema() {
        return null;
    }

    @Override
    public String toString() {
        return qualify();
    }
}
