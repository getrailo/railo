package railo.runtime.type;

import railo.runtime.PageContext;
import railo.runtime.dump.DumpData;
import railo.runtime.dump.DumpProperties;
import railo.runtime.dump.DumpUtil;
import railo.runtime.dump.Dumpable;
import railo.runtime.exp.ExpressionException;
import railo.runtime.exp.PageException;
import railo.runtime.op.Castable;
import railo.runtime.type.dt.DateTime;

/**
 * Custom Null Type
 */
public final class Null implements Castable,Dumpable {
    public static final Null NULL=new Null();
    private Null() {}
    
    /**
     * @see railo.runtime.op.Castable#castToString()
     */
    public String castToString() throws ExpressionException {
        return "";
    }

	/**
	 * @see railo.runtime.op.Castable#castToString(java.lang.String)
	 */
	public String castToString(String defaultValue) {
		return "";
	}
	
    /**
     * @see railo.runtime.op.Castable#castToBooleanValue()
     */
    public boolean castToBooleanValue() throws ExpressionException {
        throw new ExpressionException("can't convert null to a boolean");
    }
    
    /**
     * @see railo.runtime.op.Castable#castToBoolean(java.lang.Boolean)
     */
    public Boolean castToBoolean(Boolean defaultValue) {
        return defaultValue;
    }
    
    /**
     * @see railo.runtime.op.Castable#castToDoubleValue()
     */
    public double castToDoubleValue() throws ExpressionException {
        throw new ExpressionException("can't convert null to a numberic value");
    }
    
    /**
     * @see railo.runtime.op.Castable#castToDoubleValue(double)
     */
    public double castToDoubleValue(double defaultValue) {
        return defaultValue;
    }
    
    /**
     * @see railo.runtime.op.Castable#castToDateTime()
     */
    public DateTime castToDateTime() throws ExpressionException {
        throw new ExpressionException("can't convert null to a date object");
    }
    
    /**
     * @see railo.runtime.op.Castable#castToDateTime(railo.runtime.type.dt.DateTime)
     */
    public DateTime castToDateTime(DateTime defaultValue) {
        return defaultValue;
    }
    
    /**
     *
     * @see railo.runtime.dump.Dumpable#toDumpData(railo.runtime.PageContext, int)
     */
    public DumpData toDumpData(PageContext pageContext, int maxlevel, DumpProperties dp) {
        return DumpUtil.toDumpData(null,pageContext,maxlevel,dp);
    }
    /**
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return null;
    }
	public int compareTo(String str) throws PageException {
		return "".compareTo(str);
        //throw new ExpressionException("can't compare null with a string value");
	}
	public int compareTo(boolean b) throws PageException {
        throw new ExpressionException("can't compare null with a boolean value");
	}
	public int compareTo(double d) throws PageException {
        throw new ExpressionException("can't compare null with a numeric value");
	}
	public int compareTo(DateTime dt) throws PageException {
        throw new ExpressionException("can't compare null with a date object");
	}
}