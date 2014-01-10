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

	@Override
	public Object getEmbededObject() {
		return toString();
	}

	@Override
	public Object getEmbededObject(Object def) {
		return toString();
	}

	@Override
	public String toString() {
		//print.dumpStack();
		if(mode==null) mode=ModeUtil.toStringMode(res.getMode());
		return mode;
	}

	public String castString() {
		return toString();
	}

	@Override
	public boolean castToBooleanValue() throws PageException {
		return Caster.toBooleanValue(toString());
	}
    
    @Override
    public Boolean castToBoolean(Boolean defaultValue) {
        return Caster.toBoolean(toString(),defaultValue);
    }

	@Override
	public DateTime castToDateTime() throws PageException {
		return Caster.toDatetime(toString(),null);
	}
    
    @Override
    public DateTime castToDateTime(DateTime defaultValue) {
        return DateCaster.toDateAdvanced(toString(),true,null,defaultValue);
    }

	@Override
	public double castToDoubleValue() throws PageException {
		return Caster.toDoubleValue(toString());
	}
    
    @Override
    public double castToDoubleValue(double defaultValue) {
        return Caster.toDoubleValue(toString(),defaultValue);
    }

	@Override
	public String castToString() throws PageException {
		return toString();
	}

	@Override
	public String castToString(String defaultValue) {
		return toString();
	}

	@Override
	public int compareTo(String str) throws PageException {
		return Operator.compare(toString(), str);
	}

	@Override
	public int compareTo(boolean b) throws PageException {
		return Operator.compare(castToBooleanValue(), b);
	}

	@Override
	public int compareTo(double d) throws PageException {
		return Operator.compare(castToDoubleValue(), d);
	}

	@Override
	public int compareTo(DateTime dt) throws PageException {
		return Operator.compare(toString(), dt.castToString());
	}

}
