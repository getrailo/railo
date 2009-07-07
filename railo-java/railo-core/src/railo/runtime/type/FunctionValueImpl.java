package railo.runtime.type;

import java.util.Date;

import railo.runtime.PageContext;
import railo.runtime.dump.DumpData;
import railo.runtime.dump.DumpProperties;
import railo.runtime.dump.DumpUtil;
import railo.runtime.dump.Dumpable;
import railo.runtime.exp.PageException;
import railo.runtime.op.Caster;
import railo.runtime.op.Operator;
import railo.runtime.op.date.DateCaster;
import railo.runtime.type.dt.DateTime;

/**
 * represent a named function value for a functions
 */
public final class FunctionValueImpl implements FunctionValue,Dumpable {
	

	private String name;
	private Object value;

	public static FunctionValue newInstance(String name,Object value) {
		return new FunctionValueImpl(name,value);
	}
	
	/**
	 * constructor of the class
	 * @param name name of the value
	 * @param value value himself
	 */
	public FunctionValueImpl(String name,Object value) {
        this.name=name;
		this.value=value;
	} 
	/**
     * @see railo.runtime.type.FunctionValue#getName()
     */
	public String getName() {
		return name;
	}
	/**
     * @see railo.runtime.type.FunctionValue#getValue()
     */
	public Object getValue() {
		return value;
	}
	/**
	 * @see railo.runtime.op.Castable#castToString()
	 */
	public String castToString() throws PageException {
		return Caster.toString(value);
	}

    /**
     * @see railo.runtime.op.Castable#castToString(java.lang.String)
     */
    public String castToString(String defaultValue) {
        return Caster.toString(value,defaultValue);
    }
    
	/**
	 * @see railo.runtime.op.Castable#castToBooleanValue()
	 */
	public boolean castToBooleanValue() throws PageException {
		return Caster.toBooleanValue(value);
	}
    
    /**
     * @see railo.runtime.op.Castable#castToBoolean(java.lang.Boolean)
     */
    public Boolean castToBoolean(Boolean defaultValue) {
        return Caster.toBoolean(value,defaultValue);
    }
	
	/**
	 * @see railo.runtime.op.Castable#castToDoubleValue()
	 */
	public double castToDoubleValue() throws PageException {
		return Caster.toDoubleValue(value);
	}
    
    /**
     * @see railo.runtime.op.Castable#castToDoubleValue(double)
     */
    public double castToDoubleValue(double defaultValue) {
        return Caster.toDoubleValue(value,defaultValue);
    }
    
	/**
	 * @see railo.runtime.op.Castable#castToDateTime()
	 */
	public DateTime castToDateTime() throws PageException {
		return DateCaster.toDateSimple(value,null);
	}
    
    /**
     * @see railo.runtime.op.Castable#castToDateTime(railo.runtime.type.dt.DateTime)
     */
    public DateTime castToDateTime(DateTime defaultValue) {
        return DateCaster.toDateSimple(value,true,null,defaultValue);
    }

	/**
	 * @see railo.runtime.op.Castable#compare(boolean)
	 */
	public int compareTo(boolean b) throws PageException {
		return Operator.compare(value, b?1D:0D);
	}

	/**
	 * @see railo.runtime.op.Castable#compareTo(railo.runtime.type.dt.DateTime)
	 */
	public int compareTo(DateTime dt) throws PageException {
		return Operator.compare(value, (Date)dt);
	}

	/**
	 * @see railo.runtime.op.Castable#compareTo(double)
	 */
	public int compareTo(double d) throws PageException {
		return Operator.compare(value, d);
	}

	/**
	 * @see railo.runtime.op.Castable#compareTo(java.lang.String)
	 */
	public int compareTo(String str) throws PageException {
		return Operator.compare(value, str);
	}

	public DumpData toDumpData(PageContext pageContext, int maxlevel, DumpProperties properties) {
		return DumpUtil.toDumpData(value, pageContext, maxlevel, properties);
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return name+":"+value;
	}
}