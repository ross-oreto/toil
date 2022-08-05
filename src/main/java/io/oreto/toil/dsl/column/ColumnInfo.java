package io.oreto.toil.dsl.column;

public interface ColumnInfo {
    String getName();
    Class<?> getType();
    boolean isNullable();
}
