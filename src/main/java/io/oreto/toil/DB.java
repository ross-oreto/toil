package io.oreto.toil;


import io.oreto.toil.dsl.DataSource;
import io.oreto.toil.dsl.Expressible;
import io.oreto.toil.dsl.Table;
import io.oreto.toil.dsl.column.Column;
import io.oreto.toil.dsl.insert.Insert;
import io.oreto.toil.dsl.query.Select;
import io.oreto.toil.dsl.sequence.Sequence;
import io.oreto.toil.provider.DbProvider;

import java.sql.SQLException;

public class DB implements DataSource, AutoCloseable {
    private final DbProvider provider;

    public static DB using(DbProvider provider) {
        return new DB(provider);
    }

    protected DB(DbProvider provider) {
        this.provider = provider;
    }


    @Override
    public DbProvider getProvider() {
        return provider;
    }

    public Select select(Expressible<?>... selectables) {
        return new Select(getProvider(), selectables);
    }

    public Insert insert(Table table, Column<?>... columns) {
        return new Insert(getProvider(), table, columns);
    }

    public <T extends Number> T nextval(Sequence<T> sequence) throws SQLException {
        return getProvider().nextval(sequence);
    }

    public <T extends Number> T currval(Sequence<T> sequence) throws SQLException {
        return getProvider().currval(sequence);
    }

    @Override
    public void close() throws Exception {
        provider.close();
    }
}
