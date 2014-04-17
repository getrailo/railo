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
import railo.runtime.type.util.ListUtil;

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

	@Override
	public synchronized Collection duplicate(boolean deepCopy) {
		return duplicate(new CastableArray(value),deepCopy);
	}



	@Override
	public boolean castToBooleanValue() throws PageException {
		return Caster.toBooleanValue(getValue());
		
	}
    
    @Override
    public Boolean castToBoolean(Boolean defaultValue) {
        try {
			return Caster.toBoolean(getValue(),defaultValue);
		} catch (PageException e) {
			return defaultValue;
		}
    }

	@Override
	public DateTime castToDateTime() throws PageException {
		return Caster.toDate(getValue(),null);
	}
    
    @Override
    public DateTime castToDateTime(DateTime defaultValue) {
        try {
			return DateCaster.toDateAdvanced(getValue(), DateCaster.CONVERTING_TYPE_OFFSET, null,defaultValue);
		} catch (PageException e) {
			return defaultValue;
		}
    }

	@Override
	public double castToDoubleValue() throws PageException {
		return Caster.toDoubleValue(getValue());
	}
    
    @Override
    public double castToDoubleValue(double defaultValue) {
        try {
			return Caster.toDoubleValue(getValue(),true,defaultValue);
		} catch (PageException e) {
			return defaultValue;
		}
    }
	
	@Override
	public String castToString() throws PageException {
		return Caster.toString(getValue());
	}
	
	@Override
	public String castToString(String defaultValue) {
		try {
			return Caster.toString(getValue(),defaultValue);
		} catch (PageException e) {
			return defaultValue;
		}
	}

	@Override
	public int compareTo(boolean b) throws PageException {
		return Operator.compare(getValue(), b);
	}

	@Override
	public int compareTo(DateTime dt) throws PageException {
		return Operator.compare(getValue(), (Date)dt);
	}

	@Override
	public int compareTo(double d) throws PageException {
		return Operator.compare(getValue(),d);
	}

	@Override
	public int compareTo(String str) throws PageException {
		return Operator.compare(getValue(), str);
	}
	

	private Object getValue() throws PageException {
		if(value!=null)return value;
		return ListUtil.arrayToList(this, ",");
	}

	@Override
	public DumpData toDumpData(PageContext pageContext, int maxlevel,DumpProperties dp) {
		DumpTable dt= (DumpTable) super.toDumpData(pageContext, maxlevel, dp);
		dt.setTitle("Castable Array");
		return dt;
	}

}
