package io.oreto.toil.test.db;

import io.oreto.toil.dsl.column.*;
import io.oreto.toil.dsl.query.Mapper;
import io.oreto.toil.dsl.table.Table;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

public class AddressTable implements Table {
    public static final AddressTable ADDRESS = new AddressTable();

    public final Num<Long> ID = new NumImpl<>("ID", Long.class, false, this);
    public final VarChar LINE = new VarCharImpl("LINE", true, this);

    public final Mapper<Address> mapper = Mapper.of(Address.class, this);

    @Override
    public String getTableName() {
        return "ADDRESS";
    }

    @Override
    public String getSchema() {
        return null;
    }

    @Override
    public Column<?>[] getColumns() {
        return new Column[] {
                ID
                , LINE
        };
    }

    @Override
    public Collection<Column<? extends Serializable>> getPrimaryKey() {
        return List.of(
                ID
        );
    }
}
