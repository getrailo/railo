package railo.runtime.type.scope;

import java.util.Iterator;

import railo.runtime.PageContext;
import railo.runtime.dump.DumpData;
import railo.runtime.dump.DumpProperties;
import railo.runtime.engine.ThreadLocalPageContext;
import railo.runtime.exp.ExpressionException;
import railo.runtime.exp.PageException;
import railo.runtime.exp.PageRuntimeException;
import railo.runtime.java.JavaObject;
import railo.runtime.reflection.Reflector;
import railo.runtime.type.Collection;
import railo.runtime.type.KeyImpl;
import railo.runtime.type.Objects;
import railo.runtime.type.Struct;
import railo.runtime.type.dt.DateTime;
import railo.runtime.type.it.KeyIterator;
import railo.runtime.type.util.StructSupport;

public final class ObjectStruct extends StructSupport implements Struct,Objects {


	private JavaObject jo;

	public ObjectStruct(Object o) {
		if(o instanceof JavaObject) this.jo=(JavaObject) o;
		else this.jo=new JavaObject(ThreadLocalPageContext.get().getVariableUtil(),o);
	}

	public ObjectStruct(JavaObject jo) {
		this.jo=jo;
	}

	/**
	 *
	 * @see railo.runtime.java.JavaObject#call(railo.runtime.PageContext, java.lang.String, java.lang.Object[])
	 */
	public Object call(PageContext pc, String methodName, Object[] arguments) throws PageException {
		return jo.call(pc, methodName, arguments);
	}

	/**
	 *
	 * @see railo.runtime.java.JavaObject#call(railo.runtime.PageContext, railo.runtime.type.Collection.Key, java.lang.Object[])
	 */
	public Object call(PageContext pc, Key methodName, Object[] arguments) throws PageException {
		return jo.call(pc, methodName, arguments);
	}

	/**
	 *
	 * @see railo.runtime.java.JavaObject#callWithNamedValues(railo.runtime.PageContext, java.lang.String, railo.runtime.type.Struct)
	 */
	public Object callWithNamedValues(PageContext pc, String methodName, Struct args) throws PageException {
		return jo.callWithNamedValues(pc, methodName, args);
	}

	/**
	 *
	 * @see railo.runtime.java.JavaObject#callWithNamedValues(railo.runtime.PageContext, railo.runtime.type.Collection.Key, railo.runtime.type.Struct)
	 */
	public Object callWithNamedValues(PageContext pc, Key methodName, Struct args) throws PageException {
		return jo.callWithNamedValues(pc, methodName, args);
	}

	/**
	 *
	 * @see railo.runtime.java.JavaObject#get(railo.runtime.PageContext, java.lang.String)
	 */
	public Object get(PageContext pc, String propertyName) throws PageException {
		return jo.get(pc, propertyName);
	}

	/**
	 *
	 * @see railo.runtime.java.JavaObject#get(railo.runtime.PageContext, railo.runtime.type.Collection.Key)
	 */
	public Object get(PageContext pc, Key key) throws PageException {
		return jo.get(pc, key);
	}

	/**
	 *
	 * @see railo.runtime.java.JavaObject#get(railo.runtime.PageContext, java.lang.String, java.lang.Object)
	 */
	public Object get(PageContext pc, String propertyName, Object defaultValue) {
		return jo.get(pc, propertyName, defaultValue);
	}

	/**
	 *
	 * @see railo.runtime.java.JavaObject#get(railo.runtime.PageContext, railo.runtime.type.Collection.Key, java.lang.Object)
	 */
	public Object get(PageContext pc, Key key, Object defaultValue) {
		return jo.get(pc, key, defaultValue);
	}

	/**
	 *
	 * @see railo.runtime.java.JavaObject#isInitalized()
	 */
	public boolean isInitalized() {
		return jo.isInitalized();
	}

	/**
	 *
	 * @see railo.runtime.java.JavaObject#set(railo.runtime.PageContext, java.lang.String, java.lang.Object)
	 */
	public Object set(PageContext pc, String propertyName, Object value) throws PageException {
		return jo.set(pc, propertyName, value);
	}

	/**
	 *
	 * @see railo.runtime.java.JavaObject#set(railo.runtime.PageContext, railo.runtime.type.Collection.Key, java.lang.Object)
	 */
	public Object set(PageContext pc, Key propertyName, Object value) throws PageException {
		return jo.set(pc, propertyName, value);
	}

	/**
	 *
	 * @see railo.runtime.java.JavaObject#setEL(railo.runtime.PageContext, java.lang.String, java.lang.Object)
	 */
	public Object setEL(PageContext pc, String propertyName, Object value) {
		return jo.setEL(pc, propertyName, value);
	}

	/**
	 *
	 * @see railo.runtime.java.JavaObject#setEL(railo.runtime.PageContext, railo.runtime.type.Collection.Key, java.lang.Object)
	 */
	public Object setEL(PageContext pc, Key propertyName, Object value) {
		return jo.setEL(pc, propertyName, value);
	}
	
	/**
	 * @see railo.runtime.type.Collection#clear()
	 */
	public void clear() {
		//throw new PageRuntimeException(new ExpressionException("can't clear fields from object ["+objects.getClazz().getName()+"]"));
	}

	public Collection duplicate(boolean deepCopy) {
		throw new PageRuntimeException(new ExpressionException("can't clone object of type ["+jo.getClazz().getName()+"]"));
		//return null;
	}

	

	/**
	 * @see railo.runtime.type.Collection#containsKey(java.lang.String)
	 */
	public boolean containsKey(Key key) {
		return Reflector.hasPropertyIgnoreCase(jo.getClazz(), key.getString());
	}

	/**
	 * @see railo.runtime.type.Collection#get(railo.runtime.type.Collection.Key)
	 */
	public Object get(Key key) throws PageException {
		return jo.get(ThreadLocalPageContext.get(), key);
	}

	/**
	 * @see railo.runtime.type.Collection#get(railo.runtime.type.Collection.Key, java.lang.Object)
	 */
	public Object get(Key key, Object defaultValue) {
		return jo.get(ThreadLocalPageContext.get(), key,defaultValue);
	}

	/**
	 * @see railo.runtime.type.Collection#keys()
	 */
	public Key[] keys() {
		String[] strKeys = keysAsString();
		Key[] keys=new Key[strKeys.length];
		for(int i=0;i<strKeys.length;i++) {
			keys[i]=KeyImpl.init(strKeys[i]);
		}
		return keys;
	}

	/**
	 * @see railo.runtime.type.Collection#keysAsString()
	 */
	public String[] keysAsString() {
		return Reflector.getPropertyKeys(jo.getClazz());
	}

	/**
	 * @see railo.runtime.type.Collection#remove(railo.runtime.type.Collection.Key)
	 */
	public Object remove(Key key) throws PageException {
		throw new ExpressionException("can't remove field ["+key.getString()+"] from object ["+jo.getClazz().getName()+"]");
	}

	/**
	 * @see railo.runtime.type.Collection#removeEL(railo.runtime.type.Collection.Key)
	 */
	public Object removeEL(Key key) {
		return null;
	}

	/**
	 * @see railo.runtime.type.Collection#set(railo.runtime.type.Collection.Key, java.lang.Object)
	 */
	public Object set(Key key, Object value) throws PageException {
		return jo.set(ThreadLocalPageContext.get(), key, value);
	}

	/**
	 * @see railo.runtime.type.Collection#setEL(railo.runtime.type.Collection.Key, java.lang.Object)
	 */
	public Object setEL(Key key, Object value) {
		return jo.setEL(ThreadLocalPageContext.get(), key, value);
	}

	/**
	 * @see railo.runtime.type.Collection#size()
	 */
	public int size() {
		return keysAsString().length;
	}

	/**
	 * @see railo.runtime.dump.Dumpable#toDumpData(railo.runtime.PageContext, int)
	 */
	public DumpData toDumpData(PageContext pageContext, int maxlevel, DumpProperties dp) {
		return jo.toDumpData(pageContext, maxlevel,dp);
	}

	/**
	 * @see railo.runtime.type.Iteratorable#keyIterator()
	 */
	public Iterator keyIterator() {
		return new KeyIterator(keys());
	}

	/**
	 * @see railo.runtime.op.Castable#castToBooleanValue()
	 */
	public boolean castToBooleanValue() throws PageException {
		return jo.castToBooleanValue();
	}
    
    /**
     * @see railo.runtime.op.Castable#castToBoolean(java.lang.Boolean)
     */
    public Boolean castToBoolean(Boolean defaultValue) {
        return jo.castToBoolean(defaultValue);
    }

	/**
	 * @see railo.runtime.op.Castable#castToDateTime()
	 */
	public DateTime castToDateTime() throws PageException {
		return jo.castToDateTime();
	}
    
    /**
     * @see railo.runtime.op.Castable#castToDateTime(railo.runtime.type.dt.DateTime)
     */
    public DateTime castToDateTime(DateTime defaultValue) {
        return jo.castToDateTime(defaultValue);
    }

	/**
	 * @see railo.runtime.op.Castable#castToDoubleValue()
	 */
	public double castToDoubleValue() throws PageException {
		return jo.castToDoubleValue();
	}
    
    /**
     * @see railo.runtime.op.Castable#castToDoubleValue(double)
     */
    public double castToDoubleValue(double defaultValue) {
        return jo.castToDoubleValue(defaultValue);
    }

	/**
	 * @see railo.runtime.op.Castable#castToString()
	 */
	public String castToString() throws PageException {
		return jo.castToString();
	}
	
	/**
	 * @see railo.runtime.type.util.StructSupport#castToString(java.lang.String)
	 */
	public String castToString(String defaultValue) {
		return jo.castToString(defaultValue);
	}

	/**
	 * @see railo.runtime.op.Castable#compareTo(java.lang.String)
	 */
	public int compareTo(String str) throws PageException {
		return jo.compareTo(str);
	}

	/**
	 * @see railo.runtime.op.Castable#compareTo(boolean)
	 */
	public int compareTo(boolean b) throws PageException {
		return jo.compareTo(b);
	}

	/**
	 * @see railo.runtime.op.Castable#compareTo(double)
	 */
	public int compareTo(double d) throws PageException {
		return jo.compareTo(d);
	}

	/**
	 * @see railo.runtime.op.Castable#compareTo(railo.runtime.type.dt.DateTime)
	 */
	public int compareTo(DateTime dt) throws PageException {
		return jo.compareTo(dt);
	}
}
