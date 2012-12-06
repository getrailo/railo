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
    
    @Override
    public String castToString() throws ExpressionException {
        return "";
    }

	@Override
	public String castToString(String defaultValue) {
		return "";
	}
	
    @Override
    public boolean castToBooleanValue() throws ExpressionException {
        throw new ExpressionException("can't convert null to a boolean");
    }
    
    @Override
    public Boolean castToBoolean(Boolean defaultValue) {
        return defaultValue;
    }
    
    @Override
    public double castToDoubleValue() throws ExpressionException {
        throw new ExpressionException("can't convert null to a numberic value");
    }
    
    @Override
    public double castToDoubleValue(double defaultValue) {
        return defaultValue;
    }
    
    @Override
    public DateTime castToDateTime() throws ExpressionException {
        throw new ExpressionException("can't convert null to a date object");
    }
    
    @Override
    public DateTime castToDateTime(DateTime defaultValue) {
        return defaultValue;
    }
    
    @Override
    public DumpData toDumpData(PageContext pageContext, int maxlevel, DumpProperties dp) {
        return DumpUtil.toDumpData(null,pageContext,maxlevel,dp);
    }
    @Override
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