package io.oreto.toil.provider;

import io.oreto.toil.dsl.*;
import io.oreto.toil.dsl.column.ColumnInfo;
import io.oreto.toil.dsl.filter.Where;
import io.oreto.toil.dsl.query.Mappable;
import io.oreto.toil.dsl.query.Orderable;
import io.oreto.toil.dsl.query.Select;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.*;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public abstract class DbProvider implements AutoCloseable {
    public static Properties loadProperties(String file) throws IOException {
        Properties properties = new Properties();
        properties.load(Files.newInputStream(Paths.get(file)));
        return properties;
    }

    private final String url;
    private final String user;
    private final String pass;

    protected final String host;
    protected final String port;
    protected final String db;
    protected final Logger log;

    private Connection connection;

    public DbProvider(Properties properties) {
        this.log = LoggerFactory.getLogger(this.getClass().getSimpleName());
        configure(properties);
        this.host = properties.getProperty("host");
        this.port = properties.getProperty("port");
        this.db = properties.getProperty("db");
        this.url = buildUrl();
        this.user = properties.getProperty("user");
        this.pass = properties.getProperty("pass");
    }

    public DbProvider(String file) throws IOException {
        this(loadProperties(file));
    }

    public abstract String buildUrl();
    public abstract String driver();

    protected void configure(Properties properties) {}

    public Connection connection() throws SQLException {
        if(connection == null)
           return DriverManager.getConnection(url, user, pass);
        return connection;
    }

    @Override
    public void close() throws Exception {
        if (Objects.nonNull(connection) && !connection.isClosed())
            connection.close();
        connection = null;
    }

    //SELECT
    //    ccu.table_name
    //    ,ccu.constraint_name
    //    ,ccu.column_name
    //    ,kcu.table_name AS TARGET_TABLE
    //    ,kcu.column_name AS TARGET_COLUMN
    //FROM INFORMATION_SCHEMA.CONSTRAINT_COLUMN_USAGE ccu
    //    INNER JOIN INFORMATION_SCHEMA.REFERENTIAL_CONSTRAINTS rc
    //        ON ccu.CONSTRAINT_NAME = rc.CONSTRAINT_NAME
    //    INNER JOIN INFORMATION_SCHEMA.KEY_COLUMN_USAGE kcu
    //        ON kcu.CONSTRAINT_NAME = rc.UNIQUE_CONSTRAINT_NAME
    // WHERE ccu.table_name = ?
    //ORDER BY ccu.table_name

    //SELECT TC.TABLE_NAME, TC.CONSTRAINT_NAME, TC.CONSTRAINT_TYPE, KC.COLUMN_NAME
    //FROM INFORMATION_SCHEMA.TABLE_CONSTRAINTS TC
    //INNER JOIN INFORMATION_SCHEMA.KEY_COLUMN_USAGE AS KC
    //ON TC.CONSTRAINT_NAME = KC.CONSTRAINT_NAME
    //AND TC.CONSTRAINT_TYPE = 'PRIMARY KEY'
    //AND KC.table_name='CompanyNote';
    Map<String, ColumnInfo> tableColumns(String name, String schema) throws SQLException {
        boolean hasSchema = Objects.nonNull(schema);
        Map<String, ColumnInfo> columns = new LinkedHashMap<>();
        String query = "SELECT * FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME = ?"
                + (hasSchema ? " and TABLE_SCHEMA = ?" : "");
        try (PreparedStatement stmt = connection().prepareStatement(query)) {
            stmt.setString(1, name);
            if (hasSchema)
                stmt.setString(2, schema);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String columnName = rs.getString("COLUMN_NAME");
                String dataType = rs.getString("DATA_TYPE");
                String nullable = rs.getString("IS_NULLABLE");
                columns.put(columnName, new ColumnInfo() {
                    @Override
                    public String getName() {
                        return columnName;
                    }

                    @Override
                    public Class<?> getType() {
                        return Types.sqlTypeMap.getOrDefault(dataType, String.class);
                    }

                    @Override
                    public boolean isNullable() {
                        return "YES".equalsIgnoreCase(nullable);
                    }
                });
            }
        }
        return columns;
    }

    protected <T> Result<T> query(Select select, Function<ResultSet, Result<T>> resultFunction) throws SQLException {
        SQL sql = toSQL(select);
        log.debug(sql.getSql());
        try (PreparedStatement statement = connection().prepareStatement(sql.getSql())) {
            Object[] parameters = sql.getParameters();
            for (int i = 0; i < parameters.length; i++) {
                statement.setObject(i + 1, parameters[i]);
            }
            logParameters(parameters);
            return resultFunction.apply(statement.executeQuery());
        }
    }

    public Result<Record> fetch(Select select) throws SQLException {
        return query(select, resultSet -> new RecordResult(select, resultSet));
    }

    public <T> Result<T> fetch(Select select, Mappable<T> mappable) throws SQLException {
        return query(select, resultSet -> new Result<>(select, resultSet, mappable));
    }

    protected void logParameters(Object[] parameters) {
        if (parameters.length > 0)
            log.debug("parameters: [{}]", Arrays.stream(parameters).map(Object::toString)
                    .collect(Collectors.joining(", ")));
    }

    public SQL toSQL(Select select) {
        StringBuilder sb = new StringBuilder();
        // SELECT
        String selections = select.getExpressibles().isEmpty()
                ? "*" : select.getExpressibles().stream()
                .map(expressible -> expressible.isAliased()
                        ? expressible.getAlias().create()
                        : expressible.express())
                .collect(Collectors.joining(", "));
        String tables = select.getFrom().stream()
                .map(Table::getTableName)
                .collect(Collectors.joining(", "));
        sb.append("select ").append(selections).append(" from ").append(tables);
        Object[] params;
        // WHERE
        if (select.isFiltered()) {
            params = whereClause(sb, select.getWhere());
        } else
            params = new Object[]{};
        // ORDER BY
        if (!select.getOrderables().isEmpty()) {
            sb.append(" order by ").append(select.getOrderables().stream().map(orderable -> {
                Orderable.Direction direction = orderable.getDirection();
                if (direction == null)
                    return orderable.express();
                else
                    return String.format("%s %s", orderable.express(), orderable.getDirection());
            }).collect(Collectors.joining(", ")));
        }
        // OFFSET FETCH
        if (select.isPaged()) {
            sb.append(" ").append(page(select));
        }
        return SQL.of(sb.toString(), params);
    }

    protected Object[] whereClause(StringBuilder sb, Where where) {
        if (where.getLogic().isEmpty())
            return new Object[] {};

        List<Object> params = new ArrayList<>();
        boolean hasGroups = where.getLogic().size() > 1;
        sb.append(" where ");
        int i = 0;
        for (Where.Logical logic : where.getLogic()) {
            if (i > 0) sb.append(String.format(" %s ", logic.operator()));
            else i++;
            boolean group = hasGroups && logic.conditions().length > 1;
            if (group) sb.append('(');
            sb.append(
                    Arrays.stream(logic.conditions())
                            .map(condition -> {
                               params.addAll(condition.getParams());
                               return condition.toString();
                            })
                            .collect(Collectors.joining(String.format(" %s ", logic.operator())))
            );
            if (group) sb.append(')');
        }
        return params.toArray();
    }

    public String page(Select select) {
        Integer offset = select.getOffset();
        Integer limit = select.getLimit();
        if (Objects.nonNull(offset) && Objects.nonNull(limit)) {
            return String.format("offset %d rows fetch next %d rows only", offset, limit);
        } else if (Objects.nonNull(offset)) {
            return String.format("offset %d rows", offset);
        } else if (Objects.nonNull(limit)) {
            return String.format("fetch next %d rows only", limit);
        } else {
            return "";
        }
    }
}
