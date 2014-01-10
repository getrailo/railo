package railo.runtime.type.trace;

import railo.runtime.PageContext;
import railo.runtime.debug.Debugger;
import railo.runtime.dump.DumpData;
import railo.runtime.dump.DumpProperties;
import railo.runtime.dump.DumpUtil;
import railo.runtime.exp.PageException;
import railo.runtime.op.Caster;
import railo.runtime.op.Operator;
import railo.runtime.type.Collection.Key;
import railo.runtime.type.Objects;
import railo.runtime.type.Struct;
import railo.runtime.type.dt.DateTime;
import railo.runtime.util.VariableUtilImpl;

public class TOObjects extends TraceObjectSupport implements Objects {
	
	private static final long serialVersionUID = -2011026266467450312L;

	protected TOObjects(Debugger debugger,Object obj,int type,String category,String text) {
		super(debugger,obj,type,category,text);
	}

	@Override
	public DumpData toDumpData(PageContext pageContext, int maxlevel, DumpProperties properties) {
		log();
		return DumpUtil.toDumpData(o, pageContext, maxlevel, properties);
	}

	@Override
	public String castToString() throws PageException {
		log();
		return Caster.toString(o);
	}
	
	@Override
	public String castToString(String defaultValue) {
		log();
		return Caster.toString(o,defaultValue);
	}
	
	@Override
	public boolean castToBooleanValue() throws PageException {
		log();
		return Caster.toBooleanValue(o);
	}
	
	@Override
	public Boolean castToBoolean(Boolean defaultValue) {
		log();
		return Caster.toBoolean(o,defaultValue);
	}
	
	@Override
	public double castToDoubleValue() throws PageException {
		log();
		return Caster.toDoubleValue(o);
	}
	
	@Override
	public double castToDoubleValue(double defaultValue) {
		log();
		return Caster.toDoubleValue(o,defaultValue);
	}
	
	@Override
	public DateTime castToDateTime() throws PageException {
		log();
		return new TODateTime(debugger,Caster.toDate(o, false,null),type,category,text);
	}
	
	@Override
	public DateTime castToDateTime(DateTime defaultValue) {
		log();
		return new TODateTime(debugger,Caster.toDate(o, false,null,defaultValue),type,category,text);
	}
	
	@Override
	public int compareTo(boolean b) throws PageException {
		log();
		return Operator.compare(o, b);
	}
	
	@Override
	public int compareTo(DateTime dt) throws PageException {
		log();
		return Operator.compare(o, (Object)dt);
	}

	@Override
	public int compareTo(double d) throws PageException {
		log();
		return Operator.compare(o, d);
	}
	
	@Override
	public int compareTo(String str) throws PageException {
		log();
		return Operator.compare(o, str);
	}

	@Override
	public Object get(PageContext pc, Key key) throws PageException {
		log(key.getString());
		VariableUtilImpl var = (VariableUtilImpl) pc.getVariableUtil();
		return var.get(pc, o, key);
		//return TraceObjectSupport.toTraceObject(debugger,var.get(pc, o, key),type,category,text);
	}
	
	@Override
	public Object get(PageContext pc, Key key, Object defaultValue) {
		log(key.getString());
		VariableUtilImpl var = (VariableUtilImpl) pc.getVariableUtil();
		return var.get(pc, o, key, defaultValue);
		//return TraceObjectSupport.toTraceObject(debugger,var.get(pc, o, key, defaultValue),type,category,text);
	}
	
	@Override
	public Object set(PageContext pc, Key key, Object value) throws PageException {
		log(key,value);
		VariableUtilImpl var = (VariableUtilImpl) pc.getVariableUtil();
		return var.set(pc, o, key, value);
		//return TraceObjectSupport.toTraceObject(debugger,var.set(pc, o, key, value),type,category,text);
	}
	
	@Override
	public Object setEL(PageContext pc, Key key, Object value) {
		log(key,value);
		VariableUtilImpl var = (VariableUtilImpl) pc.getVariableUtil();
		return var.setEL(pc, o, key, value);
		//return TraceObjectSupport.toTraceObject(debugger,var.setEL(pc, o, key, value),type,category,text);
	}

	@Override
	public Object call(PageContext pc, Key key, Object[] args) throws PageException {
		log(key.getString());
		VariableUtilImpl var = (VariableUtilImpl) pc.getVariableUtil();
		return var.callFunctionWithoutNamedValues(pc, o, key, args);
	}
	
	@Override
	public Object callWithNamedValues(PageContext pc, Key key, Struct args) throws PageException {
		log(key.getString());
		VariableUtilImpl var = (VariableUtilImpl) pc.getVariableUtil();
		return var.callFunctionWithNamedValues(pc, o, key, args);
	}
	
	public boolean isInitalized() {
		log();
		return true;
	}
}
