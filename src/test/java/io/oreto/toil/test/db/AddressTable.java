package io.oreto.toil.test.db;

import io.oreto.toil.DB;
import io.oreto.toil.dsl.Table;
import io.oreto.toil.dsl.column.*;
import io.oreto.toil.dsl.query.Mapper;

import java.sql.SQLException;
import java.util.Optional;

public class AddressTable implements Table {
    public static final AddressTable ADDRESS = new AddressTable();

    public final Num<Long> ID = new NumImpl<>("ID", Long.class, false, this);
    public final VarChar LINE = new VarCharImpl("LINE", true, this);

    public final Mapper<Address> mapper = Mapper.of(Address.class, this);

    public Address create(DB db, Address address) throws SQLException {
        return db.insert(ADDRESS)
                .value(ID, DbSequence.DB_SEQUENCE.nextval)
                .value(LINE, address.getLine())
                .returning(getColumns())
                .fetch(mapper)
                .getFirstRecord();
    }

    public final Optional<Address> get(DB db, long id) throws SQLException {
        return db.select()
                .from(ADDRESS)
                .where(ID.eq(id))
                .fetch(mapper)
                .getOneRecord();
    }

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
    public Column<?>[] getPrimaryKey() {
        return new Column[] {
                ID
        };
    }
}
