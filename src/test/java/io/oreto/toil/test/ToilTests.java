package io.oreto.toil.test;

import com.fasterxml.jackson.databind.JsonNode;
import io.oreto.toil.DB;
import io.oreto.toil.Toil;
import io.oreto.toil.dsl.function.Func;
import io.oreto.toil.dsl.query.Mapper;
import io.oreto.toil.provider.HsqldbProvider;
import io.oreto.toil.provider.Result;
import io.oreto.toil.provider.RowResult;
import io.oreto.toil.test.db.Address;
import io.oreto.toil.test.db.AddressRepo;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;
import java.util.Optional;

import static io.oreto.toil.test.db.AddressTable.ADDRESS;
import static io.oreto.toil.test.db.DbSequence.DB_SEQUENCE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestMethodOrder(OrderAnnotation.class)
public class ToilTests {
    private static DB db;
    private static AddressRepo addressRepo;
    @BeforeAll
    static void setup() throws SQLException {
        db = DB.using(new HsqldbProvider(new Toil.Config()
                .host("mem")
                .db("testdb")
                .user("test")
                .pass("test")
                .getProperties()));
        Connection connection = db.getProvider().connection();
        InputStream inputStream = ClassLoader.getSystemClassLoader().getResourceAsStream("schema.sql");
        assert inputStream != null;
        for (String line : new BufferedReader(new InputStreamReader(inputStream)).lines().toList()) {
            try(Statement statement = connection.createStatement()) {
                statement.execute(line);
            }
        }

        addressRepo = new AddressRepo(db);
    }

    @AfterAll
    static void cleanup() throws Exception {
        db.close();
    }

//    @Test
//    void builderTest() throws SQLException {
//        new TableClassBuilder().run(db.getProvider(), "PERSON", null);
//    }

    @Test @Order(1)
    void insertAddressTest() throws SQLException {
        RowResult rowResult = db.insert(ADDRESS)
                .value(ADDRESS.ID, DB_SEQUENCE.nextval)
                .value(ADDRESS.LINE, "The Shire")
                .returning(ADDRESS.ID)
                .fetch();
        long id = rowResult.singleValue();
        assertEquals(1L, id);

        Address address = addressRepo.create(new Address().withLine("Hogwarts"));
        assertEquals(address.getId(), db.currval(DB_SEQUENCE));

        rowResult = db.insert(ADDRESS)
                .values(DB_SEQUENCE.nextval, "Winterfell")
                .returning(ADDRESS.ID)
                .fetch();
        assertEquals(3L, (Long) rowResult.singleValue());
    }

    @Test @Order(10)
    void queryAddressTest() throws SQLException {
        long count = addressRepo.count();
        assertEquals(count, (Long) db.select(Func.count(ADDRESS.ID)).from(ADDRESS).fetch().singleValue());

        Result<Address> records =
                db.select(ADDRESS.ID, ADDRESS.LINE)
                        .from(ADDRESS)
                        .fetch(ADDRESS.mapper);
        assertEquals(count, records.size());

        Result<JsonNode> json =
                db.select(ADDRESS.ID, ADDRESS.LINE)
                        .from(ADDRESS)
                        .fetch(Mapper.of(JsonNode.class, ADDRESS));

        assertEquals(count, json.size());

        Optional<Address> record = addressRepo.get(1L);
        assertTrue(record.isPresent());
        record = addressRepo.get(Map.of(ADDRESS.ID, 1L));
        assertTrue(record.isPresent());
    }
}
