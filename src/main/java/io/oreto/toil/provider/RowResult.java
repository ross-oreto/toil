package io.oreto.toil.provider;

import io.oreto.toil.dsl.query.Row;

import java.sql.ResultSet;

public class RowResult extends Result<Row> {
    RowResult(ResultSet resultSet) {
        super(resultSet);
    }
}
