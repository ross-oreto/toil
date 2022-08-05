package io.oreto.toil.provider;

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
}
