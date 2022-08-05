package io.oreto.toil.provider;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

public class Types {
    // -------------------------------------------------------------------------
    // Precisions
    // -------------------------------------------------------------------------
    /**
     * The minimum decimal precision needed to represent a Java {@link Long}
     * type.
     */
    private static final int LONG_PRECISION = String.valueOf(Long.MAX_VALUE).length();

    /**
     * The minimum decimal precision needed to represent a Java {@link Integer}
     * type.
     */
    private static final int INTEGER_PRECISION = String.valueOf(Integer.MAX_VALUE).length();

    /**
     * The minimum decimal precision needed to represent a Java {@link Short}
     * type.
     */
    private static final int SHORT_PRECISION = String.valueOf(Short.MAX_VALUE).length();

    /**
     * The minimum decimal precision needed to represent a Java {@link Byte}
     * type.
     */
    private static final int BYTE_PRECISION = String.valueOf(Byte.MAX_VALUE).length();

    /**
     * The minimum decimal precision needed to represent a Java {@link Float}
     * type.
     */
    private static final int FLOAT_PRECISION = String.valueOf(Float.MAX_VALUE).length();

    /**
     * The minimum decimal precision needed to represent a Java {@link Double}
     * type.
     */
    private static final int DOUBLE_PRECISION = String.valueOf(Double.MAX_VALUE).length();


    static Map<String, Class<?>> sqlTypeMap = new HashMap<String, Class<?>>() {{
        put("char", String.class);
        put("bit", boolean.class);
        put("tinyint", short.class);
        put("smallint", short.class);
        put("int", int.class);
        put("real", float.class);
        put("bigint", long.class);
        put("float", float.class);
        put("nchar", String.class);
        put("nvarchar", String.class);
        put("binary", byte[].class);
        put("varbinary", byte[].class);
        put("uniqueidentifier", String.class);
        put("varchar", String.class);
        put("date", Date.class);
        put("numeric", BigDecimal.class);
        put("decimal", BigDecimal.class);
        put("money", BigDecimal.class);
        put("smallmoney", BigDecimal.class);
        put("smalldatetime", Timestamp.class);
        put("datetime", Timestamp.class);
        put("datetime2", Timestamp.class);
    }};

    static Map<String, Class<?>> oracleTypeMap = new HashMap<String, Class<?>>() {{
        put("VARCHAR", String.class);
        put("VARCHAR2", String.class);
        put("CHAR", char.class);
        put("DATE", Timestamp.class);
    }};

    static Class<?> oracleType(String type, int precision, int scale) {
        if (oracleTypeMap.containsKey(type))
            return oracleTypeMap.get(type);
        if ("NUMBER".equalsIgnoreCase(type)) {
            if (scale == 0) {
                if (precision == 0)
                    return int.class;
                if (precision == 1) {
                    return boolean.class;
                }
                if (precision < BYTE_PRECISION) {
                    return byte.class;
                }
                if (precision < SHORT_PRECISION) {
                    return short.class;
                }
                if (precision < INTEGER_PRECISION) {
                    return int.class;
                }
                if (precision < LONG_PRECISION) {
                    return long.class;
                }
                // Default integer number
                return BigInteger.class;
            } else {
               if (precision < FLOAT_PRECISION)
                   return Float.class;
               else if (precision < DOUBLE_PRECISION)
                    return Double.class;
               return BigDecimal.class;
            }
        }
        return String.class;
    }
}
