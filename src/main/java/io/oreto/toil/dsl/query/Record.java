package io.oreto.toil.dsl.query;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a database row from a SQL result set
 */
public class Record {
    /**
     * Create new record list from a result set
     * @param resultSet The SQL result set
     * @return a list of records
     * @throws SQLException if a database access error occurs or this method is called on a closed result set
     */
    public static List<Record> of(ResultSet resultSet) throws SQLException {
        List<Record> records = new ArrayList<>();
        while (resultSet.next()) {
            records.add(new Record(resultSet));
        }
        return records;
    }

    /**
     * Represents a row column
     * @param index Zero based index of the column
     * @param name Name of the column
     * @param value Column value
     */
    protected record Entry(int index, String name, Object value) {
        public int getPosition() {
            return index + 1;
        }
    }

    // list of column entries
    private final List<Entry> values;

    /**
     * Create a new Record from a SQL result set current cursor row
     * @param resultSet The SQL result set
     * @throws SQLException if a database access error occurs or this method is called on a closed result set
     */
    protected Record(ResultSet resultSet) throws SQLException {
        values = new ArrayList<>();
        ResultSetMetaData metaData = resultSet.getMetaData();
        for (int i = 1; i <= metaData.getColumnCount(); i++) {
            values.add(new Entry(i, metaData.getColumnName(i), resultSet.getObject(i)));
        }
    }

    public List<Entry> getValues() {
        return values;
    }
}
