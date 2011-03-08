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
import railo.runtime.type.KeyImpl;
import railo.runtime.type.Objects;
import railo.runtime.type.Struct;
import railo.runtime.type.dt.DateTime;
import railo.runtime.util.VariableUtilImpl;

public class TOObjects extends TraceObjectSupport implements Objects {
	
	private static final long serialVersionUID = -2011026266467450312L;

	protected TOObjects(Debugger debugger,Object obj,int type,String category,String text) {
		super(debugger,obj,type,category,text);
	}

	/**
	 * @see railo.runtime.dump.Dumpable#toDumpData(railo.runtime.PageContext, int, railo.runtime.dump.DumpProperties)
	 */
	public DumpData toDumpData(PageContext pageContext, int maxlevel, DumpProperties properties) {
		log();
		return DumpUtil.toDumpData(o, pageContext, maxlevel, properties);
	}

	/**
	 * @see railo.runtime.op.Castable#castToString()
	 */
	public String castToString() throws PageException {
		log();
		return Caster.toString(o);
	}
	
	/**
	 * @see railo.runtime.op.Castable#castToString(java.lang.String)
	 */
	public String castToString(String defaultValue) {
		log();
		return Caster.toString(o,defaultValue);
	}
	
	/**
	 * @see railo.runtime.op.Castable#castToBooleanValue()
	 */
	public boolean castToBooleanValue() throws PageException {
		log();
		return Caster.toBooleanValue(o);
	}
	
	/**
	 * @see railo.runtime.op.Castable#castToBoolean(java.lang.Boolean)
	 */
	public Boolean castToBoolean(Boolean defaultValue) {
		log();
		return Caster.toBoolean(o,defaultValue);
	}
	
	/**
	 * @see railo.runtime.op.Castable#castToDoubleValue()
	 */
	public double castToDoubleValue() throws PageException {
		log();
		return Caster.toDoubleValue(o);
	}
	
	/**
	 * @see railo.runtime.op.Castable#castToDoubleValue(double)
	 */
	public double castToDoubleValue(double defaultValue) {
		log();
		return Caster.toDoubleValue(o,defaultValue);
	}
	
	/**
	 * @see railo.runtime.op.Castable#castToDateTime()
	 */
	public DateTime castToDateTime() throws PageException {
		log();
		return new TODateTime(debugger,Caster.toDate(o, false,null),type,category,text);
	}
	
	/**
	 * @see railo.runtime.op.Castable#castToDateTime(railo.runtime.type.dt.DateTime)
	 */
	public DateTime castToDateTime(DateTime defaultValue) {
		log();
		return new TODateTime(debugger,Caster.toDate(o, false,null,defaultValue),type,category,text);
	}
	
	/**
	 * @see railo.runtime.op.Castable#compareTo(boolean)
	 */
	public int compareTo(boolean b) throws PageException {
		log();
		return Operator.compare(o, b);
	}
	
	/**
	 * @see railo.runtime.op.Castable#compareTo(railo.runtime.type.dt.DateTime)
	 */
	public int compareTo(DateTime dt) throws PageException {
		log();
		return Operator.compare(o, (Object)dt);
	}

	/**
	 * @see railo.runtime.op.Castable#compareTo(double)
	 */
	public int compareTo(double d) throws PageException {
		log();
		return Operator.compare(o, d);
	}
	
	/**
	 * @see railo.runtime.op.Castable#compareTo(java.lang.String)
	 */
	public int compareTo(String str) throws PageException {
		log();
		return Operator.compare(o, str);
	}

	/**
	 * @see railo.runtime.type.Objects#get(railo.runtime.PageContext, railo.runtime.type.Collection.Key)
	 */
	public Object get(PageContext pc, Key key) throws PageException {
		log(key.getString());
		VariableUtilImpl var = (VariableUtilImpl) pc.getVariableUtil();
		return var.get(pc, o, key);
		//return TraceObjectSupport.toTraceObject(debugger,var.get(pc, o, key),type,category,text);
	}
	
	/**
	 * @see railo.runtime.type.Objects#get(railo.runtime.PageContext, railo.runtime.type.Collection.Key, java.lang.Object)
	 */
	public Object get(PageContext pc, Key key, Object defaultValue) {
		log(key.getString());
		VariableUtilImpl var = (VariableUtilImpl) pc.getVariableUtil();
		return var.get(pc, o, key, defaultValue);
		//return TraceObjectSupport.toTraceObject(debugger,var.get(pc, o, key, defaultValue),type,category,text);
	}
	
	/**
	 * @see railo.runtime.type.Objects#set(railo.runtime.PageContext, railo.runtime.type.Collection.Key, java.lang.Object)
	 */
	public Object set(PageContext pc, Key key, Object value) throws PageException {
		log(key,value);
		VariableUtilImpl var = (VariableUtilImpl) pc.getVariableUtil();
		return var.set(pc, o, key, value);
		//return TraceObjectSupport.toTraceObject(debugger,var.set(pc, o, key, value),type,category,text);
	}
	
	/**
	 * @see railo.runtime.type.Objects#setEL(railo.runtime.PageContext, railo.runtime.type.Collection.Key, java.lang.Object)
	 */
	public Object setEL(PageContext pc, Key key, Object value) {
		log(key,value);
		VariableUtilImpl var = (VariableUtilImpl) pc.getVariableUtil();
		return var.setEL(pc, o, key, value);
		//return TraceObjectSupport.toTraceObject(debugger,var.setEL(pc, o, key, value),type,category,text);
	}
	
	/**
	 * @see railo.runtime.type.Objects#setEL(railo.runtime.PageContext, java.lang.String, java.lang.Object)
	 */
	public Object setEL(PageContext pc, String propertyName, Object value) {
		log(propertyName,value);
		return setEL(pc, KeyImpl.init(propertyName), value);
		//return TraceObjectSupport.toTraceObject(debugger,setEL(pc, KeyImpl.init(propertyName), value),type,category,text);
	}
	
	/**
	 * @see railo.runtime.type.Objects#set(railo.runtime.PageContext, java.lang.String, java.lang.Object)
	 */
	public Object set(PageContext pc, String propertyName, Object value) throws PageException {
		log(propertyName,value) ;
		return set(pc, KeyImpl.init(propertyName), value);
		//return TraceObjectSupport.toTraceObject(debugger,set(pc, KeyImpl.init(propertyName), value),type,category,text);
	}

	/**
	 * @see railo.runtime.type.Objects#call(railo.runtime.PageContext, railo.runtime.type.Collection.Key, java.lang.Object[])
	 */
	public Object call(PageContext pc, Key key, Object[] args) throws PageException {
		log(key.getString());
		VariableUtilImpl var = (VariableUtilImpl) pc.getVariableUtil();
		return var.callFunctionWithoutNamedValues(pc, o, key, args);
	}
	
	/**
	 * @see railo.runtime.type.Objects#callWithNamedValues(railo.runtime.PageContext, railo.runtime.type.Collection.Key, railo.runtime.type.Struct)
	 */
	public Object callWithNamedValues(PageContext pc, Key key, Struct args) throws PageException {
		log(key.getString());
		VariableUtilImpl var = (VariableUtilImpl) pc.getVariableUtil();
		return var.callFunctionWithNamedValues(pc, o, key, args);
	}
	
	/**
	 * @see railo.runtime.type.Objects#callWithNamedValues(railo.runtime.PageContext, java.lang.String, railo.runtime.type.Struct)
	 */
	public Object callWithNamedValues(PageContext pc, String key, Struct args) throws PageException {
		log(key);
		return callWithNamedValues(pc, KeyImpl.init(key), args);
	}

	/**
	 * @see railo.runtime.type.Objects#call(railo.runtime.PageContext, java.lang.String, java.lang.Object[])
	 */
	public Object call(PageContext pc, String key, Object[] arguments) throws PageException {
		log(key);
		return call(pc, KeyImpl.init(key), arguments);
	}
	
	/**
	 * @see railo.runtime.type.Objects#isInitalized()
	 */
	public boolean isInitalized() {
		log();
		return true;
	}

	/**
	 * @see railo.runtime.type.Objects#get(railo.runtime.PageContext, java.lang.String, java.lang.Object)
	 */
	public Object get(PageContext pc, String propertyName, Object defaultValue) {
		log(propertyName);
		return get(pc, KeyImpl.init(propertyName),defaultValue);
		//return TraceObjectSupport.toTraceObject(debugger,get(pc, KeyImpl.init(propertyName),defaultValue),type,category,text);
	}
	
	/**
	 * @see railo.runtime.type.Objects#get(railo.runtime.PageContext, java.lang.String)
	 */
	public Object get(PageContext pc, String propertyName) throws PageException {
		log(propertyName);
		return get(pc, KeyImpl.init(propertyName));
		//return TraceObjectSupport.toTraceObject(debugger,get(pc, KeyImpl.init(propertyName)),type,category,text);
	}

}
