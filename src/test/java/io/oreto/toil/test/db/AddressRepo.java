package io.oreto.toil.test.db;

import io.oreto.toil.DB;
import io.oreto.toil.Repository;
import io.oreto.toil.dsl.query.Mapper;

import java.sql.SQLException;
import java.util.Optional;

import static io.oreto.toil.test.db.AddressTable.ADDRESS;

public class AddressRepo extends Repository<Address> {
    public AddressRepo(DB db) {
        super(db, ADDRESS, Mapper.of(Address.class, ADDRESS));
    }

    @Override
    public Address create(Address address) throws SQLException {
        return db.insert(ADDRESS)
                .value(ADDRESS.ID,  DbSequence.DB_SEQUENCE.nextval)
                .value(ADDRESS.LINE, address.getLine())
                .returning()
                .fetch(mapper)
                .getFirstRecord();
    }

    public final Optional<Address> get(long id) throws SQLException {
        return db.select()
                .from(ADDRESS)
                .where(ADDRESS.ID.eq(id))
                .fetch(mapper)
                .getOneRecord();
    }
}
