package railo.runtime.type.util;

import java.util.Date;

import railo.runtime.engine.ThreadLocalPageContext;
import railo.runtime.exp.PageException;
import railo.runtime.op.Castable;
import railo.runtime.op.Caster;
import railo.runtime.op.Decision;
import railo.runtime.op.Operator;
import railo.runtime.op.date.DateCaster;
import railo.runtime.type.ObjectWrap;
import railo.runtime.type.UDF;
import railo.runtime.type.dt.DateTime;

/**
 * @deprecated this class is no longer used with no replacment
 */
public class UDFDefaultValue implements ObjectWrap,Castable {


	private static final String COMPLEX_DEFAULT_TYPE="[runtime expression]";
	
	private UDF udf;
	private int index;

	/**
	 * Constructor of the class
	 * @param udf
	 * @param index
	 */
	public UDFDefaultValue(UDF udf, int index) {
		this.udf=udf;
		this.index=index;
	}


	/**
	 * @see railo.runtime.type.ObjectWrap#getEmbededObject()
	 */
	public Object getEmbededObject() {
		try {
            Object defaultValue = udf.getDefaultValue(ThreadLocalPageContext.get(), index);
            
            if(defaultValue==null || Decision.isSimpleValue(defaultValue)) {
            	return defaultValue;
            }
        }
        catch(Throwable e) {}
		
		return COMPLEX_DEFAULT_TYPE;
	}
	
	/**
	 * @see railo.runtime.type.ObjectWrap#getEmbededObject(java.lang.Object)
	 */
	public Object getEmbededObject(Object defaultValue) {
		return getEmbededObject();
	}

	/**
	 * @see railo.runtime.op.Castable#castToBooleanValue()
	 */
	public boolean castToBooleanValue() throws PageException {
		return Caster.toBooleanValue(getEmbededObject());
	}
    
    /**
     * @see railo.runtime.op.Castable#castToBoolean(java.lang.Boolean)
     */
    public Boolean castToBoolean(Boolean defaultValue) {
    	return Caster.toBoolean(getEmbededObject(),defaultValue);
    }

	/**
	 * @see railo.runtime.op.Castable#castToDateTime()
	 */
	public DateTime castToDateTime() throws PageException {
		return Caster.toDatetime(getEmbededObject(),null);
	}
    
    /**
     * @see railo.runtime.op.Castable#castToDateTime(railo.runtime.type.dt.DateTime)
     */
    public DateTime castToDateTime(DateTime defaultValue) {
        return DateCaster.toDateAdvanced(getEmbededObject(),true,null,defaultValue);
    }

	/**
	 * @see railo.runtime.op.Castable#castToDoubleValue()
	 */
	public double castToDoubleValue() throws PageException {
		return Caster.toDoubleValue(getEmbededObject());
	}
    
    /**
     * @see railo.runtime.op.Castable#castToDoubleValue(double)
     */
    public double castToDoubleValue(double defaultValue) {
        return Caster.toDoubleValue(getEmbededObject(),defaultValue);
    }

	/**
	 * @see railo.runtime.op.Castable#castToString()
	 */
	public String castToString() throws PageException {
		return Caster.toString(getEmbededObject());
	}

	/**
	 * @see railo.runtime.op.Castable#castToString(java.lang.String)
	 */
	public String castToString(String defaultValue) {
		return Caster.toString(getEmbededObject(),defaultValue);
	}

	/**
	 * @see railo.runtime.op.Castable#compareTo(java.lang.String)
	 */
	public int compareTo(String str) throws PageException {
		return Operator.compare(castToString(),str);
	}

	/**
	 * @see railo.runtime.op.Castable#compareTo(boolean)
	 */
	public int compareTo(boolean b) throws PageException {
		return Operator.compare(castToBooleanValue(),b);
	}

	/**
	 * @see railo.runtime.op.Castable#compareTo(double)
	 */
	public int compareTo(double d) throws PageException {
		return Operator.compare(castToDoubleValue(),d);
	}

	/**
	 * @see railo.runtime.op.Castable#compareTo(railo.runtime.type.dt.DateTime)
	 */
	public int compareTo(DateTime dt) throws PageException {
		return Operator.compare((Date)castToDateTime(), (Date)dt);
	}

}
