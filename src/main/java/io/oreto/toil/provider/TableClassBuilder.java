package io.oreto.toil.provider;

import io.oreto.toil.Toil;
import io.oreto.toil.dsl.column.ColumnInfo;
import java.sql.SQLException;
import java.util.Map;
import java.util.Properties;

public class TableClassBuilder {
    public enum DbType {
        hsqldb, sqlserver, oracle
    }

    public static DbProvider getProvider(DbType dbType, Properties properties) {
        return switch (dbType) {
            case hsqldb -> new HsqldbProvider(properties);
            case sqlserver -> new SqlProvider(properties);
            case oracle -> new OracleProvider(properties);
        };
    }

    public static void main(String[] args) throws SQLException {
        String tableName = args[5];
        String schema = args.length > 6 ? args[6] : null;
        DbType dbType = DbType.valueOf(args[0]);
        Properties properties = new Toil.Config()
                .host(args[1])
                .db(args[2])
                .user(args[3])
                .pass(args[4])
                .getProperties();
        new TableClassBuilder().run(getProvider(dbType, properties), tableName, schema);
    }

    public void run(DbProvider provider, String tableName, String schema) throws SQLException {
        Map<String, ColumnInfo> columns = provider.tableColumns(tableName, schema);
        columns.forEach((k, v) -> {
            System.out.println(v.getName());
            System.out.println(v.getType());
            System.out.println(v.isNullable());
        });
    }
}
