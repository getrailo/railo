package railo.runtime.type;

import java.io.Serializable;
import java.sql.Types;
import java.util.Date;

import railo.runtime.op.Caster;
import railo.runtime.op.Decision;
import railo.runtime.type.dt.DateTime;


/**
 * Helper class for the QueryColumnImpl
 */
public final class QueryColumnUtil implements Serializable {
    
    private QueryColumnImpl column;

    /**
     * constructor of the class
     * @param column 
     */
    public QueryColumnUtil(QueryColumnImpl column) {
        this.column=column;
    }
    
    /**
     * reset the type of the column
     */
    protected void resetType() {
        column.type=Types.OTHER;
    }
    
	/**
	 * redefine type of value 
     * @param value
	 * @return redefined type of the value
     */
    protected Object reDefineType(Object value) {
        column.typeChecked=false;
        if(value==null)return value;
        switch(column.type) {
        	case Types.OTHER:	return value;
        
        // Numeric Values
			case Types.DOUBLE:	return reDefineDouble(value);
			case Types.BIGINT:	return reDefineDecimal(value);
    		case Types.NUMERIC:	return reDefineDouble(value);
    		case Types.INTEGER:	return reDefineInteger(value);
    		case Types.TINYINT:	return reDefineTinyInt(value);
    		case Types.FLOAT:	return reDefineFloat(value);
    		case Types.DECIMAL:	return reDefineDecimal(value);
    		case Types.REAL:	return reDefineFloat(value);
    		case Types.SMALLINT:return reDefineShort(value);
    		
    	// DateTime Values
    		case Types.TIMESTAMP:	return reDefineDateTime(value);
    		case Types.DATE:		return reDefineDateTime(value);
    		case Types.TIME:		return reDefineDateTime(value);
    	
    	// Char
    		case Types.CHAR: 		return reDefineString(value);
    		case Types.VARCHAR: 	return reDefineString(value);
    		case Types.LONGVARCHAR: return reDefineString(value);
    		case Types.CLOB:		return reDefineClob(value);
    	
    	// Boolean
    		case Types.BOOLEAN:		return reDefineBoolean(value);
    		case Types.BIT:			return reDefineBoolean(value);	
    	
    	// Binary
    		case Types.BINARY:			return reDefineBinary(value);
    		case Types.VARBINARY:		return reDefineBinary(value);
    		case Types.LONGVARBINARY:	return reDefineBinary(value);
    		case Types.BLOB:			return reDefineBlob(value);
    		
    	// Others
    		case Types.ARRAY:				return reDefineOther(value);
    		case Types.DATALINK:				return reDefineOther(value);
    		case Types.DISTINCT:				return reDefineOther(value);
    		case Types.JAVA_OBJECT:				return reDefineOther(value);
    		case Types.NULL:				return reDefineOther(value);
    		case Types.STRUCT:				return reDefineOther(value);
    		case Types.REF:				return reDefineOther(value);
    		default: return value; 
        }
        
    }

    private Object reDefineBoolean(Object value) {
    	if(Decision.isCastableToBoolean(value))
    		return value;
    	
    	/*Boolean bool = Caster.toBoolean(value,null);
        if(bool!=null) {                
            return bool;
        }*/
    	
        resetType();
        return value;
    }

    private Object reDefineDouble(Object value) {
    	if(Decision.isCastableToNumeric(value))
    		return value;
    	
        resetType();
        return value;
    }

    private Object reDefineFloat(Object value) {
    	if(Decision.isCastableToNumeric(value))
    		return value;
    	
    	
        resetType();
        return value;
    }

    private Object reDefineInteger(Object value) {
    	if(Decision.isCastableToNumeric(value))
    		return value;
    	
    	
        resetType();
        return value;
    }

    private Object reDefineShort(Object value) {
    	
    	double dbl = Caster.toDoubleValue(value,Double.NaN);
        if(Decision.isValid(dbl)) {  
            short sht=(short)dbl;
            if(sht==dbl)return value;
            
            column.type=Types.DOUBLE;
            return value;
        }
        resetType();
        return value;
    }

    private Object reDefineTinyInt(Object value) {
    	if(Decision.isCastableToNumeric(value))
    		return value;
    	
    	
        resetType();
        return value;
    }

    private Object reDefineDecimal(Object value) {
    	if(Decision.isCastableToNumeric(value))
    		return value;
    	
        resetType();
        return value;
    }

    private Object reDefineDateTime(Object value) {
    	if(Decision.isDateSimple(value,true))
    		return value;
    	
        resetType();
        return value;
    }

    private Object reDefineString(Object value) {
    	if(Decision.isCastableToString(value))
    		return value;
    	
        resetType();
        return value;
    }

    private Object reDefineClob(Object value) {
    	if(Decision.isCastableToString(value))
    		return value;
    	
    	
        resetType();
        return value;
    }

    private Object reDefineBinary(Object value) {
    	if(Decision.isCastableToBinary(value,false))
    		return value;
    	
    	resetType();
        return value;
    	
    	
    }

    private Object reDefineBlob(Object value) {
    	if(Decision.isCastableToBinary(value,false))
    		return value;
    	
    	resetType();
        return value;
        
    
    }
    
    private Object reDefineOther(Object value) {
        resetType();
        return value;
    }
    

    /**
     * reorganize type of a column
     * @param reorganize 
     */
    protected void reOrganizeType() {
    	
        if((column.type==Types.OTHER) && !column.typeChecked) {
        	column.typeChecked=true;
        	if(column.size()>0) {
                checkOther(column.data[0]);
                
                // get Type
                for(int i=1;i<column.size();i++) {
                    switch(column.type) {
                        case Types.NULL:checkOther(column.data[i]);break;
                        case Types.TIMESTAMP:checkDate(column.data[i]);break;
                        //case Types.DATE:checkDate(column.data[i]);break;
                        case Types.BOOLEAN:checkBoolean(column.data[i]);break;
                        case Types.DOUBLE:checkDouble(column.data[i]);break;
                        case Types.VARCHAR:checkBasic(column.data[i]);break;
                        default:break;
                    }
                }
                //print.out("type:"+QueryImpl.getColumTypeName(column.type));

                // Date
                /*if(Types.TIMESTAMP==column.type) {
                    for(int i=0;i<column.size();i++) {
                        column.data[i]=toDate(column.data[i]);
                    }
                }
                // Double
                else if(Types.DOUBLE==column.type) {
                    for(int i=0;i<column.size();i++) {
                        column.data[i]=toDouble(column.data[i]);
                    }
                }
                // Boolean
                else if(Types.BOOLEAN==column.type) {
                    for(int i=0;i<column.size();i++) {
                        column.data[i]=toBoolean(column.data[i]);
                    }
                }
                // Varchar
                else if(Types.VARCHAR==column.type) {
                    for(int i=0;i<column.size();i++) {
                        column.data[i]=toString(column.data[i]);
                    }
                }*/
            }
        }
    }
    
    
    

    private void checkOther(Object value) {
        // NULL
        if(value==null) {
            column.type=Types.NULL;
            return;
        }
        // DateTime
        if(Decision.isDateSimple(value,false)) {
            column.type=Types.TIMESTAMP;
            return;
        }
        // Boolean
        if(Decision.isBoolean(value)) {
            column.type=Types.BOOLEAN;
            return;
        }
        // Double
        if(Decision.isNumeric(value)) {
            column.type=Types.DOUBLE;
            return;
        }
        // String
        String str = Caster.toString(value,null);
        if(str!=null) {
            column.type=Types.VARCHAR;
            return;
        }
    }
    
    private void checkDate(Object value) {
        // NULL
        if(value==null) return;
        // DateTime
        if(Decision.isDateSimple(value,false)) {
            column.type=Types.TIMESTAMP;
            return;
        }
        // String
        String str = Caster.toString(value,null);
        if(str!=null) {
            column.type=Types.VARCHAR;
            return;
        }
        // Other
        column.type=Types.OTHER;
        return;
    }

    private void checkBoolean(Object value) {
        // NULL
        if(value==null) return;
        // Boolean
        if(Decision.isBoolean(value)) {
            column.type=Types.BOOLEAN;
            return;
        }
        // Double
        if(Decision.isNumeric(value)) {
            column.type=Types.DOUBLE;
            return;
        }
        // String
        String str = Caster.toString(value,null);
        if(str!=null) {
            column.type=Types.VARCHAR;
            return;
        }
        // Other
        column.type=Types.OTHER;
        return;
    }
    private void checkDouble(Object value) {
        // NULL
        if(value==null) return;
        // Double
        if(Decision.isNumeric(value)) {
            column.type=Types.DOUBLE;
            return;
        }
        // String
        String str = Caster.toString(value,null);
        if(str!=null) {
            column.type=Types.VARCHAR;
            return;
        }
        // Other
        column.type=Types.OTHER;
        return;
    }
    
    private void checkBasic(Object value) {
        // NULL
        if(value==null) return;
        // Date
        if(value instanceof Date || value instanceof Number) return;
        // String
        String str = Caster.toString(value,null);
        if(str!=null) {
            return;
        }
        // OTHER
        column.type=Types.OTHER;
        return;
    }
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    

    private DateTime toDate(Object value) {
        // NULL
        if(value==null) return null;
        // DateTime
        return Caster.toDate(value,true,null,null);
    }

    private Boolean toBoolean(Object value) {
        // NULL
        if(value==null) return null;
        // Boolean
        return Caster.toBoolean(value,null);
    }
    private Double toDouble(Object value) {
        // NULL
        if(value==null) return null;
        // Double
        return Caster.toDouble(value,null);
    }
    
    private String toString(Object value) {
        // NULL
        if(value==null) return null;
        // String
        return Caster.toString(value,null);
    }
}