package io.oreto.toil.provider;

import io.oreto.toil.dsl.query.Select;

import java.sql.ResultSet;

public class RecordResult extends Result<Record> {
    RecordResult(Select select, ResultSet resultSet) {
        super(select, resultSet, null);
    }
}
