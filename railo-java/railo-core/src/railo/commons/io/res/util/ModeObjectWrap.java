package railo.commons.io.res.util;

import railo.commons.io.ModeUtil;
import railo.commons.io.res.Resource;
import railo.runtime.exp.PageException;
import railo.runtime.op.Castable;
import railo.runtime.op.Caster;
import railo.runtime.op.Operator;
import railo.runtime.op.date.DateCaster;
import railo.runtime.type.ObjectWrap;
import railo.runtime.type.dt.DateTime;

public final class ModeObjectWrap implements ObjectWrap,Castable {
	
	private final Resource res;
	private String mode=null;
	
	public ModeObjectWrap(Resource res) {
		this.res=res;
	}

	/**
	 * @see railo.runtime.type.ObjectWrap#getEmbededObject()
	 */
	public Object getEmbededObject() {
		return toString();
	}

	/**
	 * @see railo.runtime.type.ObjectWrap#getEmbededObject(java.lang.Object)
	 */
	public Object getEmbededObject(Object def) {
		return toString();
	}

	/**
	 *
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		//print.dumpStack();
		if(mode==null) mode=ModeUtil.toStringMode(res.getMode());
		return mode;
	}

	public String castString() {
		return toString();
	}

	/**
	 * @see railo.runtime.op.Castable#castToBooleanValue()
	 */
	public boolean castToBooleanValue() throws PageException {
		return Caster.toBooleanValue(toString());
	}
    
    /**
     * @see railo.runtime.op.Castable#castToBoolean(java.lang.Boolean)
     */
    public Boolean castToBoolean(Boolean defaultValue) {
        return Caster.toBoolean(toString(),defaultValue);
    }

	/**
	 * @see railo.runtime.op.Castable#castToDateTime()
	 */
	public DateTime castToDateTime() throws PageException {
		return Caster.toDatetime(toString(),null);
	}
    
    /**
     * @see railo.runtime.op.Castable#castToDateTime(railo.runtime.type.dt.DateTime)
     */
    public DateTime castToDateTime(DateTime defaultValue) {
        return DateCaster.toDateAdvanced(toString(),true,null,defaultValue);
    }

	/**
	 * @see railo.runtime.op.Castable#castToDoubleValue()
	 */
	public double castToDoubleValue() throws PageException {
		return Caster.toDoubleValue(toString());
	}
    
    /**
     * @see railo.runtime.op.Castable#castToDoubleValue(double)
     */
    public double castToDoubleValue(double defaultValue) {
        return Caster.toDoubleValue(toString(),defaultValue);
    }

	/**
	 * @see railo.runtime.op.Castable#castToString()
	 */
	public String castToString() throws PageException {
		return toString();
	}

	/**
	 * @see railo.runtime.op.Castable#castToString(java.lang.String)
	 */
	public String castToString(String defaultValue) {
		return toString();
	}

	/**
	 * @see railo.runtime.op.Castable#compareTo(java.lang.String)
	 */
	public int compareTo(String str) throws PageException {
		return Operator.compare(toString(), str);
	}

	/**
	 * @see railo.runtime.op.Castable#compareTo(boolean)
	 */
	public int compareTo(boolean b) throws PageException {
		return Operator.compare(castToBooleanValue(), b);
	}

	/**
	 * @see railo.runtime.op.Castable#compareTo(double)
	 */
	public int compareTo(double d) throws PageException {
		return Operator.compare(castToDoubleValue(), d);
	}

	/**
	 * @see railo.runtime.op.Castable#compareTo(railo.runtime.type.dt.DateTime)
	 */
	public int compareTo(DateTime dt) throws PageException {
		return Operator.compare(toString(), dt.castToString());
	}

}
