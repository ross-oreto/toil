package io.oreto.toil.provider;

import io.oreto.toil.dsl.expression.SQL;
import io.oreto.toil.dsl.query.Select;
import io.oreto.toil.dsl.sequence.Sequence;

import java.io.IOException;
import java.util.Objects;
import java.util.Properties;

public class SqlProvider extends DbProvider {
    private boolean trustCerts;

    public SqlProvider(String file) throws IOException {
        super(file);
    }

    public SqlProvider(Properties properties) {
        super(properties);
    }

    @Override
    protected void configure(Properties properties) {
        this.trustCerts = properties.getProperty("trustCerts", "true").equals("true");
    }

    @Override
    public String buildUrl() {
        return String.format("%s://%s:%s;databaseName=%s;trustServerCertificate=%b", driver(), host, port, db, trustCerts);
    }

    @Override
    public String driver() {
        return "jdbc:sqlserver";
    }

    @Override
    public SQL page(Select select) {
        Integer offset = select.getOffset();
        Integer limit = select.getLimit();
        String order = select.isOrdered() ? "" : "order by (select null) ";

        if (Objects.nonNull(limit)) {
            return SQL.of(order + "offset ? rows fetch next ? rows only", offset == null ? 0 : offset, limit);
        } else if (Objects.nonNull(offset)) {
            return SQL.of(order + "offset ? rows", offset);
        } else
            return SQL.of("");
    }

    @Override
    public <T extends Number> SQL toSQL(Sequence<T> sequence, boolean current) {
        String which = current ? "CURR" : "NEXT";
        return SQL.of(String.format("select %s  VALUE FOR %s", which, sequence.qualify()));
    }
}
