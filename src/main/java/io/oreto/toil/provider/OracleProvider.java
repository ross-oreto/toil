package io.oreto.toil.provider;

import io.oreto.toil.dsl.column.ColumnInfo;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

public class OracleProvider extends DbProvider {
    public OracleProvider(String file) throws IOException {
        super(file);
    }

    public OracleProvider(Properties properties) {
        super(properties);
    }

    @Override
    public String buildUrl() {
        return String.format("%s:@%s:%s:%s", driver(), host, port, db);
    }

    @Override
    public String driver() {
        return "jdbc:oracle:thin";
    }

    @Override
    Map<String, ColumnInfo> tableColumns(String name, String schema) throws SQLException {
        Map<String, ColumnInfo> columns = new LinkedHashMap<>();
        Connection connection = connection();
        String query = "select * from sys.all_tab_columns col\n" +
                "inner join sys.all_tables t on col.owner = t.owner and col.table_name = t.table_name\n" +
                "where col.table_name = ? and col.owner = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, name);
            stmt.setString(2, schema);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String columnName = rs.getString("COLUMN_NAME");
                String dataType = rs.getString("DATA_TYPE");
                String nullable = rs.getString("NULLABLE");
                int precision = rs.getInt("DATA_PRECISION");
                int scale = rs.getInt("DATA_SCALE");
                columns.put(columnName, new ColumnInfo() {
                    @Override
                    public String getName() {
                        return columnName;
                    }

                    @Override
                    public Class<?> getType() {
                        return Types.oracleType(dataType, precision, scale);
                    }

                    @Override
                    public boolean isNullable() {
                        return "Y".equalsIgnoreCase(nullable);
                    }
                });
            }
        }
        return columns;
    }
}
