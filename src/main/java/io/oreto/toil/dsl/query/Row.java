package io.oreto.toil.dsl.query;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a database row from a SQL result set
 */
public class Row {
    /**
     * Create new row list from a result set
     * @param resultSet The SQL result set
     * @return a list of rows
     * @throws SQLException if a database access error occurs or this method is called on a closed result set
     */
    public static List<Row> of(ResultSet resultSet) throws SQLException {
        List<Row> rows = new ArrayList<>();
        while (resultSet.next()) {
            rows.add(new Row(resultSet));
        }
        return rows;
    }

    /**
     * Represents a row column
     * @param index Zero based index of the column
     * @param name Name of the column
     * @param value Column value
     */
    public record Entry(int index, String name, Object value) {
        public int getPosition() {
            return index + 1;
        }

        @SuppressWarnings("unchecked")
        public <T> T as() {
            return value == null ? null : (T) value;
        }
    }

    // list of column entries
    private final List<Entry> entries;

    /**
     * Create a new row from a SQL result set current cursor row
     * @param resultSet The SQL result set
     * @throws SQLException if a database access error occurs or this method is called on a closed result set
     */
    protected Row(ResultSet resultSet) throws SQLException {
        entries = new ArrayList<>();
        ResultSetMetaData metaData = resultSet.getMetaData();
        for (int i = 1; i <= metaData.getColumnCount(); i++) {
            entries.add(new Entry(i, metaData.getColumnName(i), resultSet.getObject(i)));
        }
    }

    public List<Entry> getEntries() {
        return entries;
    }

    public int size() {
        return entries.size();
    }

    public Entry firstEntry() {
        return entries.get(0);
    }

    protected Entry getEntry(int index) {
        return entries.get(index);
    }

    protected Entry getEntry(String name) {
        for (Entry entry : entries) {
            if (entry.name().equals(name))
                return entry;
        }
        return null;
    }

    public <T> T get(int index) {
        return getEntry(index).as();
    }
    public <T> T get(String name) {
        return getEntry(name).as();
    }

    public String getString(int index) {
        return getEntry(index).as();
    }
    public String getString(String name) {
        return getEntry(name).as();
    }

    public Long getLong(int index) {
        return getEntry(index).as();
    }
    public Long getLong(String name) {
        return getEntry(name).as();
    }

    public Integer getInt(int index) {
        return getEntry(index).as();
    }
    public Integer getInt(String name) {
        return getEntry(name).as();
    }

    public Double getDouble(int index) {
        return getEntry(index).as();
    }
    public Double getDouble(String name) {
        return getEntry(name).as();
    }

    public Float getFloat(int index) {
        return getEntry(index).as();
    }
    public Float getFloat(String name) {
        return getEntry(name).as();
    }

    public Short getShort(int index) {
        return getEntry(index).as();
    }
    public Short getShort(String name) {
        return getEntry(name).as();
    }

    public BigDecimal getBigDecimal(int index) {
        return getEntry(index).as();
    }
    public BigDecimal getBigDecimal(String name) {
        ResultSet resultSet;
        return getEntry(name).as();
    }

    public Byte getByte(int index) {
        return getEntry(index).as();
    }
    public Byte getByte(String name) {
        return getEntry(name).as();
    }

    public Boolean getBoolean(int index) {
        return getEntry(index).as();
    }
    public Boolean getBoolean(String name) {
        return getEntry(name).as();
    }

    public Date getDate(int index) {
        return getEntry(index).as();
    }
    public Date getDate(String name) {
        return getEntry(name).as();
    }

    public Timestamp getTimestamp(int index) {
        return getEntry(index).as();
    }
    public Timestamp getTimestamp(String name) {
        return getEntry(name).as();
    }

    public LocalDate getLocalDate(int index) {
        Date date = getDate(index);
        return date == null ? null : date.toLocalDate();
    }
    public LocalDate getLocalDate(String name) {
        Date date = getDate(name);
        return date == null ? null : date.toLocalDate();
    }

    public LocalDateTime getLocalDateTime(int index) {
        Timestamp timestamp = getTimestamp(index);
        return timestamp == null ? null : timestamp.toLocalDateTime();
    }
    public LocalDateTime getLocalDateTime(String name) {
        Timestamp timestamp = getTimestamp(name);
        return timestamp == null ? null : timestamp.toLocalDateTime();
    }
}
