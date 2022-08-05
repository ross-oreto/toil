package io.oreto.toil.provider;

import io.oreto.toil.dsl.column.ColumnInfo;
import io.oreto.toil.dsl.query.Select;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;
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
    public String page(Select select) {
        Integer offset = select.getOffset();
        Integer limit = select.getLimit();
        String order = select.isOrdered() ? "" : "order by (select null) ";

        if (Objects.nonNull(limit)) {
            return String.format("%soffset %d rows fetch next %d rows only", order, offset == null ? 0 : offset, limit);
        } else if (Objects.nonNull(offset)) {
            return String.format("%soffset %d rows", order, offset);
        } else
            return "";
    }
}
