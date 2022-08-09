package io.oreto.toil.provider;

import io.oreto.toil.dsl.query.Row;

import java.sql.ResultSet;

public class RowResult extends Result<Row> {
    RowResult(ResultSet resultSet) {
        super(resultSet);
    }

    public <T> T singleValue() {
        return getFirstRecord().get(0);
    }

    public <T> T value(int index) {
       return getFirstRecord().get(index);
    }

    public <T> T value(String name) {
        return getFirstRecord().get(name);
    }
}
