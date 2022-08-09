package io.oreto.toil;

import io.oreto.toil.dsl.Table;
import io.oreto.toil.dsl.column.Column;
import io.oreto.toil.dsl.function.Func;
import io.oreto.toil.dsl.query.Mapper;
import io.oreto.toil.dsl.query.Select;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public abstract class Repository<T> {
    protected final DB db;
    protected final Mapper<T> mapper;

    protected final Table table;

    protected Repository(DB db, Table table, Mapper<T> mapper) {
        this.db = db;
        this.table = table;
        this.mapper = mapper;
    }

    public abstract T create(T t) throws SQLException;

    public Optional<T> get(Map<Column<?>, Serializable> ids) throws SQLException {
        Select select = db.select().from(table);
        for (Column<? extends Serializable> column : table.getPrimaryKey()) {
            select.where(column.eq(ids.get(column)));
        }
        return select.fetch(mapper).getOneRecord();
    }

    public List<T> find(Map<Column<?>, Serializable> criteria) throws SQLException {
        Select select = db.select().from(table);
        criteria.forEach(((column, serializable) -> select.where(column.eq(serializable))));
        return select.fetch(mapper).getRecords();
    }

    public long count() throws SQLException {
        return db.select(Func.count()).from(table).fetch().singleValue();
    }
}
