package railo.runtime.db;

import java.sql.Types;

import railo.runtime.exp.DatabaseException;

/**
 * 
 */
public final class SQLTypeCaster {
    
    /**
     * cast a String type to in type from (java.sql.Types)
     * @param type
     * @return
     * @throws DatabaseException
     */
    public static int toSQLType(String type) throws DatabaseException {
        type=type.toLowerCase().trim();
        if(type.length()>2) {
            char first=type.charAt(0);
            if(first=='b') {
                if("bit".equals(type)) return Types.BIT;
                else if("binary".equals(type)) return Types.BINARY;
                else if("bigint".equals(type)) return Types.BIGINT;
                else if("boolean".equals(type)) return Types.BIT;
                else if("bool".equals(type)) return Types.BIT;
            }
            else if(first=='d') {
                if("double".equals(type)) return Types.DOUBLE;
                if("decimal".equals(type)) return Types.DECIMAL;
                if("date".equals(type)) return Types.TIMESTAMP;
            }
            else if(first=='i') {
                if("integer".equals(type)) return Types.INTEGER;
                if("int".equals(type)) return Types.INTEGER;
            }
            else if(first=='n') {
                if("numeric".equals(type)) return Types.DOUBLE;
                if("number".equals(type)) return Types.DOUBLE;
            }
            else if(first=='s') {
                if("string".equals(type)) return Types.VARCHAR;
            }
            else if(first=='t') {
                if("time".equals(type)) return Types.TIME;
            }
            else if(first=='v') {
                if("varchar".equals(type)) return Types.VARCHAR;
            }
            else if(first=='o') {
                if("other".equals(type)) return Types.OTHER;
                if("object".equals(type)) return Types.OTHER;
            }
        }
        throw new DatabaseException("invalid type Definition ["+type+"]",
        "valid types are [bit, binary,bigint, double, decimal, date, integer, time, varchar]",null,null,null);
    }
}