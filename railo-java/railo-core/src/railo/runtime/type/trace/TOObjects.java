package railo.runtime.type.trace;

import railo.commons.io.log.LogResource;
import railo.commons.io.res.util.ResourceUtil;
import railo.commons.lang.StringUtil;
import railo.runtime.PageContext;
import railo.runtime.dump.DumpData;
import railo.runtime.dump.DumpProperties;
import railo.runtime.dump.DumpUtil;
import railo.runtime.engine.ThreadLocalPageContext;
import railo.runtime.exp.PageException;
import railo.runtime.op.Caster;
import railo.runtime.op.Duplicator;
import railo.runtime.op.Operator;
import railo.runtime.type.Collection.Key;
import railo.runtime.type.Collection;
import railo.runtime.type.KeyImpl;
import railo.runtime.type.Objects;
import railo.runtime.type.Struct;
import railo.runtime.type.UDF;
import railo.runtime.type.dt.DateTime;
import railo.runtime.util.VariableUtilImpl;

public class TOObjects extends TraceObjectSupport implements Objects {
	
	private static final long serialVersionUID = -2011026266467450312L;

	protected TOObjects(Object obj, String label, LogResource log) {
		super(obj,log,label);
	}

	/**
	 * @see railo.runtime.dump.Dumpable#toDumpData(railo.runtime.PageContext, int, railo.runtime.dump.DumpProperties)
	 */
	public DumpData toDumpData(PageContext pageContext, int maxlevel, DumpProperties properties) {
		log(null);
		return DumpUtil.toDumpData(o, pageContext, maxlevel, properties);
	}

	/**
	 * @see railo.runtime.op.Castable#castToString()
	 */
	public String castToString() throws PageException {
		log(null);
		return Caster.toString(o);
	}
	
	/**
	 * @see railo.runtime.op.Castable#castToString(java.lang.String)
	 */
	public String castToString(String defaultValue) {
		log(null);
		return Caster.toString(o,defaultValue);
	}
	
	/**
	 * @see railo.runtime.op.Castable#castToBooleanValue()
	 */
	public boolean castToBooleanValue() throws PageException {
		log(null);
		return Caster.toBooleanValue(o);
	}
	
	/**
	 * @see railo.runtime.op.Castable#castToBoolean(java.lang.Boolean)
	 */
	public Boolean castToBoolean(Boolean defaultValue) {
		log(null);
		return Caster.toBoolean(o,defaultValue);
	}
	
	/**
	 * @see railo.runtime.op.Castable#castToDoubleValue()
	 */
	public double castToDoubleValue() throws PageException {
		log(null);
		return Caster.toDoubleValue(o);
	}
	
	/**
	 * @see railo.runtime.op.Castable#castToDoubleValue(double)
	 */
	public double castToDoubleValue(double defaultValue) {
		log(null);
		return Caster.toDoubleValue(o,defaultValue);
	}
	
	/**
	 * @see railo.runtime.op.Castable#castToDateTime()
	 */
	public DateTime castToDateTime() throws PageException {
		log(null);
		return new TODateTime(Caster.toDate(o, false,null),label,log);
	}
	
	/**
	 * @see railo.runtime.op.Castable#castToDateTime(railo.runtime.type.dt.DateTime)
	 */
	public DateTime castToDateTime(DateTime defaultValue) {
		log(null);
		return new TODateTime(Caster.toDate(o, false,null,defaultValue),label,log);
	}
	
	/**
	 * @see railo.runtime.op.Castable#compareTo(boolean)
	 */
	public int compareTo(boolean b) throws PageException {
		log(null);
		return Operator.compare(o, b);
	}
	
	/**
	 * @see railo.runtime.op.Castable#compareTo(railo.runtime.type.dt.DateTime)
	 */
	public int compareTo(DateTime dt) throws PageException {
		log(null);
		return Operator.compare(o, (Object)dt);
	}

	/**
	 * @see railo.runtime.op.Castable#compareTo(double)
	 */
	public int compareTo(double d) throws PageException {
		log(null);
		return Operator.compare(o, d);
	}
	
	/**
	 * @see railo.runtime.op.Castable#compareTo(java.lang.String)
	 */
	public int compareTo(String str) throws PageException {
		log(null);
		return Operator.compare(o, str);
	}

	/**
	 * @see railo.runtime.type.Objects#get(railo.runtime.PageContext, railo.runtime.type.Collection.Key)
	 */
	public Object get(PageContext pc, Key key) throws PageException {
		log(key.getString());
		VariableUtilImpl var = (VariableUtilImpl) pc.getVariableUtil();
		return TraceObjectSupport.toTraceObject(var.get(pc, o, key), label, log);
	}
	
	/**
	 * @see railo.runtime.type.Objects#get(railo.runtime.PageContext, railo.runtime.type.Collection.Key, java.lang.Object)
	 */
	public Object get(PageContext pc, Key key, Object defaultValue) {
		log(key.getString());
		VariableUtilImpl var = (VariableUtilImpl) pc.getVariableUtil();
		return TraceObjectSupport.toTraceObject(var.get(pc, o, key, defaultValue), label, log);
	}
	
	/**
	 * @see railo.runtime.type.Objects#set(railo.runtime.PageContext, railo.runtime.type.Collection.Key, java.lang.Object)
	 */
	public Object set(PageContext pc, Key key, Object value) throws PageException {
		log(key.getString());
		VariableUtilImpl var = (VariableUtilImpl) pc.getVariableUtil();
		return TraceObjectSupport.toTraceObject(var.set(pc, o, key, value), label, log);
	}
	
	/**
	 * @see railo.runtime.type.Objects#setEL(railo.runtime.PageContext, railo.runtime.type.Collection.Key, java.lang.Object)
	 */
	public Object setEL(PageContext pc, Key key, Object value) {
		log(key.getString());
		VariableUtilImpl var = (VariableUtilImpl) pc.getVariableUtil();
		return TraceObjectSupport.toTraceObject(var.setEL(pc, o, key, value), label, log);
	}
	
	/**
	 * @see railo.runtime.type.Objects#setEL(railo.runtime.PageContext, java.lang.String, java.lang.Object)
	 */
	public Object setEL(PageContext pc, String propertyName, Object value) {
		log(propertyName);
		return TraceObjectSupport.toTraceObject(setEL(pc, KeyImpl.init(propertyName), value), label, log);
	}
	
	/**
	 * @see railo.runtime.type.Objects#set(railo.runtime.PageContext, java.lang.String, java.lang.Object)
	 */
	public Object set(PageContext pc, String propertyName, Object value) throws PageException {
		log(propertyName) ;
		return TraceObjectSupport.toTraceObject(set(pc, KeyImpl.init(propertyName), value), label, log);
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
		log(null);
		return true;
	}

	/**
	 * @see railo.runtime.type.Objects#get(railo.runtime.PageContext, java.lang.String, java.lang.Object)
	 */
	public Object get(PageContext pc, String propertyName, Object defaultValue) {
		log(propertyName);
		return TraceObjectSupport.toTraceObject(get(pc, KeyImpl.init(propertyName),defaultValue), label, log);
	}
	
	/**
	 * @see railo.runtime.type.Objects#get(railo.runtime.PageContext, java.lang.String)
	 */
	public Object get(PageContext pc, String propertyName) throws PageException {
		log(propertyName);
		return TraceObjectSupport.toTraceObject(get(pc, KeyImpl.init(propertyName)), label, log);
	}

}
