package io.oreto.toil;


import io.oreto.toil.dsl.DataSource;
import io.oreto.toil.dsl.Expressible;
import io.oreto.toil.dsl.query.Select;
import io.oreto.toil.provider.DbProvider;

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

    @Override
    public void close() throws Exception {
        provider.close();
    }
}
