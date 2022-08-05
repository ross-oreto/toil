package io.oreto.toil.dsl;


import io.oreto.toil.provider.DbProvider;

public interface DataSource {
    DbProvider getProvider();
}
