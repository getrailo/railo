package railo.runtime.type;

import java.util.Date;

import railo.runtime.PageContext;
import railo.runtime.dump.DumpData;
import railo.runtime.dump.DumpProperties;
import railo.runtime.dump.DumpTable;
import railo.runtime.exp.PageException;
import railo.runtime.op.Caster;
import railo.runtime.op.Operator;
import railo.runtime.op.date.DateCaster;
import railo.runtime.type.dt.DateTime;

public final class CastableArray extends ArrayImpl {
	
	private final Object value;

	/**
	 * Constructor of the class
	 * generates as string list of the array
	 */
	public CastableArray(){value=null;}
	
	public CastableArray(Object value){
		this.value=value;
	}

	/**
	 * @see railo.runtime.type.ArrayImpl#duplicate(boolean)
	 */
	public synchronized Collection duplicate(boolean deepCopy) {
		return duplicate(new CastableArray(value),deepCopy);
	}



	/**
	 * @see railo.runtime.type.util.ArraySupport#castToBooleanValue()
	 */
	public boolean castToBooleanValue() throws PageException {
		return Caster.toBooleanValue(getValue());
		
	}
    
    /**
     * @see railo.runtime.op.Castable#castToBoolean(java.lang.Boolean)
     */
    public Boolean castToBoolean(Boolean defaultValue) {
        try {
			return Caster.toBoolean(getValue(),defaultValue);
		} catch (PageException e) {
			return defaultValue;
		}
    }

	/**
	 * @see railo.runtime.type.util.ArraySupport#castToDateTime()
	 */
	public DateTime castToDateTime() throws PageException {
		return Caster.toDate(getValue(),null);
	}
    
    /**
     * @see railo.runtime.op.Castable#castToDateTime(railo.runtime.type.dt.DateTime)
     */
    public DateTime castToDateTime(DateTime defaultValue) {
        try {
			return DateCaster.toDateAdvanced(getValue(), true, null,defaultValue);
		} catch (PageException e) {
			return defaultValue;
		}
    }

	/**
	 * @see railo.runtime.type.util.ArraySupport#castToDoubleValue()
	 */
	public double castToDoubleValue() throws PageException {
		return Caster.toDoubleValue(getValue());
	}
    
    /**
     * @see railo.runtime.op.Castable#castToDoubleValue(double)
     */
    public double castToDoubleValue(double defaultValue) {
        try {
			return Caster.toDoubleValue(getValue(),defaultValue);
		} catch (PageException e) {
			return defaultValue;
		}
    }
	
	/**
	 * @see railo.runtime.type.util.ArraySupport#castToString()
	 */
	public String castToString() throws PageException {
		return Caster.toString(getValue());
	}
	
	/**
	 * @see railo.runtime.type.util.StructSupport#castToString(java.lang.String)
	 */
	public String castToString(String defaultValue) {
		try {
			return Caster.toString(getValue(),defaultValue);
		} catch (PageException e) {
			return defaultValue;
		}
	}

	/**
	 * @see railo.runtime.type.util.ArraySupport#compareTo(boolean)
	 */
	public int compareTo(boolean b) throws PageException {
		return Operator.compare(getValue(), b);
	}

	/**
	 * @see railo.runtime.type.util.ArraySupport#compareTo(railo.runtime.type.dt.DateTime)
	 */
	public int compareTo(DateTime dt) throws PageException {
		return Operator.compare(getValue(), (Date)dt);
	}

	/**
	 * @see railo.runtime.type.util.ArraySupport#compareTo(double)
	 */
	public int compareTo(double d) throws PageException {
		return Operator.compare(getValue(),d);
	}

	/**
	 * @see railo.runtime.type.util.ArraySupport#compareTo(java.lang.String)
	 */
	public int compareTo(String str) throws PageException {
		return Operator.compare(getValue(), str);
	}
	

	private Object getValue() throws PageException {
		if(value!=null)return value;
		return List.arrayToList(this, ",");
	}

	/**
	 * @see railo.runtime.type.ArrayImpl#toDumpData(railo.runtime.PageContext, int, railo.runtime.dump.DumpProperties)
	 */
	public DumpData toDumpData(PageContext pageContext, int maxlevel,DumpProperties dp) {
		DumpTable dt= (DumpTable) super.toDumpData(pageContext, maxlevel, dp);
		dt.setTitle("Castable Array");
		return dt;
	}

}
