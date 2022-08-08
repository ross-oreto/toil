package io.oreto.toil.provider;

import io.oreto.toil.dsl.Expressible;
import io.oreto.toil.dsl.SQL;
import io.oreto.toil.dsl.Table;
import io.oreto.toil.dsl.column.ColumnInfo;
import io.oreto.toil.dsl.filter.Where;
import io.oreto.toil.dsl.function.Func;
import io.oreto.toil.dsl.insert.Insert;
import io.oreto.toil.dsl.query.Mappable;
import io.oreto.toil.dsl.query.Orderable;
import io.oreto.toil.dsl.query.Row;
import io.oreto.toil.dsl.query.Select;
import io.oreto.toil.dsl.sequence.Sequence;
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
            prepareParameters(statement, sql.getParameters());
            try(ResultSet resultSet = statement.executeQuery()) {
                return resultFunction.apply(resultSet);
            }
        }
    }

    public Result<Row> fetch(Select select) throws SQLException {
        return query(select, RowResult::new);
    }

    public <T> Result<T> fetch(Select select, Mappable<T> mappable) throws SQLException {
        return query(select, resultSet -> new Result<>(resultSet, select.getExpressibles(), select.getFrom().get(0), mappable));
    }

    protected void prepareParameters(PreparedStatement statement, Collection<Object> parameters) throws SQLException {
        logParameters(parameters);
        int i = 1;
        for (Object parameter : parameters) {
            statement.setObject(i, parameter);
            i++;
        }
    }

    public int execute(Insert insert) throws SQLException {
        SQL sql = toSQL(insert);
        log.debug(sql.getSql());
        try (PreparedStatement statement = connection().prepareStatement(sql.getSql())) {
            prepareParameters(statement, sql.getParameters());
            return statement.executeUpdate();
        }
    }

    public RowResult fetch(Insert insert) throws SQLException {
        SQL sql = toSQL(insert);
        log.debug(sql.getSql());
        try (PreparedStatement statement = connection().prepareStatement(sql.getSql()
                , insert.getReturning().stream().map(ColumnInfo::getName).toArray(String[]::new))) {
            prepareParameters(statement, sql.getParameters());
            statement.executeUpdate();
            return new RowResult(statement.getGeneratedKeys());
        }
    }

    public <T> Result<T> fetch(Insert insert, Mappable<T> mappable) throws SQLException {
        SQL sql = toSQL(insert);
        log.debug(sql.getSql());
        try (PreparedStatement statement = connection().prepareStatement(sql.getSql()
                , insert.getReturning().stream().map(ColumnInfo::getName).toArray(String[]::new))) {
            prepareParameters(statement, sql.getParameters());
            statement.executeUpdate();
            return new Result<>(statement.getGeneratedKeys(), insert.getReturning(), insert.getTable(), mappable);
        }
    }

    protected void logParameters(Collection<Object> parameters) {
        if (!parameters.isEmpty())
            log.debug("parameters: [{}]", parameters.stream().map(Object::toString)
                    .collect(Collectors.joining(", ")));
    }

    public SQL toSQL(Insert insert) {
        StringBuilder sb = new StringBuilder();
        sb.append("insert into ").append(insert.getTable().qualify()).append(' ');

        if (!insert.getColumns().isEmpty()) {
            sb.append('(')
                    .append(insert.getColumns().stream().map(ColumnInfo::getName).collect(Collectors.joining(", ")))
                    .append(") ");
        }
        if (insert.getSelectValues() == null) {
            List<Object> parameters = new ArrayList<>();
            sb.append("values (")
                    .append(insert.getValues().stream().map(value -> {
                       SQL sql = value.express(this);
                       if (sql.hasParameters())
                           parameters.addAll(sql.getParameters());
                       return sql.getSql();
                    }).collect(Collectors.joining(", ")))
                    .append(')');
            return SQL.of(sb.toString(), parameters.toArray());
        } else {
            SQL sql = toSQL(insert.getSelectValues());
            sb.append(' ').append(sql.getSql());
            return SQL.of(sb.toString(), sql.getParameters());
        }
    }

    public SQL toSQL(Select select) {
        StringBuilder sb = new StringBuilder();
        // SELECT
        List<String> selectionList = new ArrayList<>();
        List<Object> paramsList = new ArrayList<>();
        if (select.getExpressibles().isEmpty())
            selectionList.add("*");
        else {
           for (Expressible<?> expressible : select.getExpressibles()) {
               SQL sql = expressible.isAliased()
                       ? expressible.getAlias().create(this)
                       : expressible.express(this);
               selectionList.add(sql.getSql());
               if (sql.hasParameters())
                   paramsList.addAll(sql.getParameters());
           }
        }
        String tables = select.getFrom().stream()
                .map(Table::qualify)
                .collect(Collectors.joining(", "));
        sb.append("select ").append(String.join(", ", selectionList));
        if (tables.length() > 0)
            sb.append(" from ").append(tables);

        // WHERE
        if (select.isFiltered()) {
            paramsList.addAll(whereClause(sb, select.getWhere()));
        }

        // ORDER BY
        if (!select.getOrderables().isEmpty()) {
            sb.append(" order by ").append(select.getOrderables().stream().map(orderable -> {
                Orderable.Direction direction = orderable.getDirection();
                SQL sql = orderable.express(this);
                if (sql.hasParameters())
                    paramsList.addAll(sql.getParameters());
                if (direction == null) {
                    return sql.getSql();
                }
                else {
                    return String.format("%s %s", sql.getSql(), orderable.getDirection());
                }
            }).collect(Collectors.joining(", ")));
        }

        // OFFSET FETCH
        if (select.isPaged()) {
            SQL sql = page(select);
            if (sql.hasParameters())
                paramsList.addAll(sql.getParameters());
            sb.append(" ").append(sql.getSql());
        }
        return SQL.of(sb.toString(), paramsList.toArray());
    }

    protected Collection<Object> whereClause(StringBuilder sb, Where where) {
        if (where.getLogic().isEmpty())
            return new ArrayList<>();

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
                                SQL sql = condition.toSQL(this);
                                if (sql.hasParameters())
                                    params.addAll(sql.getParameters());
                                return sql.getSql();
                            })
                            .collect(Collectors.joining(String.format(" %s ", logic.operator())))
            );
            if (group) sb.append(')');
        }
        return params;
    }

    public SQL page(Select select) {
        Integer offset = select.getOffset();
        Integer limit = select.getLimit();
        if (Objects.nonNull(offset) && Objects.nonNull(limit)) {
            return SQL.of("offset ? rows fetch next ? rows only", offset, limit);
        } else if (Objects.nonNull(offset)) {
            return SQL.of("offset ? rows", offset);
        } else if (Objects.nonNull(limit)) {
            return SQL.of("fetch next ? rows only", limit);
        } else {
            return SQL.of("");
        }
    }

    @SuppressWarnings("unchecked")
    protected <T extends Number> T seqval(Sequence<T> sequence, boolean current) throws SQLException {
        try(Statement statement = connection().createStatement()) {
           SQL sql = toSQL(sequence, current);
           log.debug(sql.getSql());
           try(ResultSet resultSet = statement.executeQuery(sql.getSql())) {
               resultSet.next();
               return resultSet.getObject(1) == null
                       ? (T) sequence.getType().getMethod("valueOf", String.class).invoke(null, "0")
                       : resultSet.getObject(1, sequence.getType());
           }
        } catch (ReflectiveOperationException ignored) {
            return null;
        }
    }

    public <T extends Number> SQL toSQL(Sequence<T> sequence, boolean current) {
        if (current) {
            return SQL.of(String.format("SELECT current_value FROM sys.sequences where name = '%s'", sequence.qualify()));
        } else {
            return SQL.of(String.format("call NEXT VALUE FOR %s", sequence.qualify()));
        }
    }

    public <T extends Number> T nextval(Sequence<T> sequence) throws SQLException {
        return seqval(sequence, false);
    }

    public <T extends Number> T currval(Sequence<T> sequence) throws SQLException {
        return seqval(sequence, true);
    }

    // *********************** FUNCTIONS
   protected SQL toSQL(Func<?> func, Expressible<?>... parameters) {
        List<Object> params = new ArrayList<>();
        return SQL.of(String.format(
                "%s(%s)"
                , func.getName()
                , Arrays.stream(parameters)
                        .map(parameter -> {
                           SQL sql = parameter.express(this);
                           if (sql.hasParameters())
                               params.addAll(sql.getParameters());
                           return sql.getSql();
                        })
                        .collect(Collectors.joining(", "))
        ), params.toArray());
   }

    public SQL nextVal(Sequence<?> sequence) {
        return SQL.of(String.format("%s(%s)", Func.Names.NEXTVAL.name(), sequence.qualify()));
    }

    public SQL lower(Expressible<CharSequence> string) {
        SQL sql = string.express(this);
        return SQL.of(String.format("%s(%s)", Func.Names.LOWER.name(), sql.getSql()), sql.getParameters());
    }

    public SQL upper(Expressible<CharSequence> string) {
        SQL sql = string.express(this);
        return SQL.of(String.format("%s(%s)", Func.Names.UPPER.name(), sql.getSql()), sql.getParameters());
    }
}
