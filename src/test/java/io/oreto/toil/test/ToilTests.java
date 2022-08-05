package io.oreto.toil.test;

import io.oreto.toil.DB;
import io.oreto.toil.Toil;
import io.oreto.toil.provider.HsqldbProvider;
import io.oreto.toil.provider.TableClassBuilder;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ToilTests {
    private static DB db;

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
    }

    @AfterAll
    static void cleanup() throws Exception {
        db.close();
    }

    @Test
    void builderTest() throws SQLException {
        new TableClassBuilder().run(db.getProvider(), "PERSON", null);
    }
}
