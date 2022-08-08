package io.oreto.toil.provider;

import io.oreto.toil.dsl.SQL;
import io.oreto.toil.dsl.sequence.Sequence;

import java.io.IOException;
import java.util.Properties;

public class HsqldbProvider extends DbProvider {
    public HsqldbProvider(String file) throws IOException {
        super(file);
    }

    public HsqldbProvider(Properties properties) {
        super(properties);
    }

    @Override
    public String buildUrl() {
        return String.format("%s:%s:%s", driver(), host, db);
    }

    @Override
    public String driver() {
        return "jdbc:hsqldb";
    }

    @Override
    public SQL nextVal(Sequence<?> sequence) {
        return SQL.of(String.format("NEXT VALUE FOR %s", sequence.qualify()));
    }

    public <T extends Number> SQL toSQL(Sequence<T> sequence, boolean current) {
        if (current) {
            return SQL.of(String.format("SELECT (CAST(next_value AS INTEGER) - 1) FROM information_schema.sequences where sequence_name = '%s'"
                    , sequence.qualify().toUpperCase()));
        } else {
            return SQL.of(String.format("call NEXT VALUE FOR %s", sequence.qualify()));
        }
    }
}
