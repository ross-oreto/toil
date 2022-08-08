package io.oreto.toil.dsl.query;

import io.oreto.toil.dsl.DataSource;
import io.oreto.toil.provider.Result;
import io.oreto.toil.provider.RowResult;

import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

public interface Fetchable extends DataSource {
//    Result<Row> fetch() throws SQLException;
    RowResult fetch() throws SQLException;
    <T> Result<T> fetch(Mappable<T> mappable) throws SQLException;

    List<Orderable<?>> getOrderables();
    Fetchable offset(Integer offset);
    Fetchable limit(Integer limit);

    Integer getOffset();
    Integer getLimit();

    default boolean isPaged() {
        return Objects.nonNull(getOffset()) || Objects.nonNull(getLimit());
    }

    default boolean isOrdered() {
        return !getOrderables().isEmpty();
    }
}
