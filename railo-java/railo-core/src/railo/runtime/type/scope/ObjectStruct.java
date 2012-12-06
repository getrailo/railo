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
import railo.runtime.type.it.EntryIterator;
import railo.runtime.type.it.KeyIterator;
import railo.runtime.type.it.StringIterator;
import railo.runtime.type.it.ValueIterator;
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

	@Override
	public Object call(PageContext pc, Key methodName, Object[] arguments) throws PageException {
		return jo.call(pc, methodName, arguments);
	}

	@Override
	public Object callWithNamedValues(PageContext pc, Key methodName, Struct args) throws PageException {
		return jo.callWithNamedValues(pc, methodName, args);
	}

	@Override
	public Object get(PageContext pc, Key key) throws PageException {
		return jo.get(pc, key);
	}

	@Override
	public Object get(PageContext pc, Key key, Object defaultValue) {
		return jo.get(pc, key, defaultValue);
	}

	public boolean isInitalized() {
		return jo.isInitalized();
	}

	@Override
	public Object set(PageContext pc, Key propertyName, Object value) throws PageException {
		return jo.set(pc, propertyName, value);
	}

	@Override
	public Object setEL(PageContext pc, Key propertyName, Object value) {
		return jo.setEL(pc, propertyName, value);
	}
	
	@Override
	public void clear() {
		//throw new PageRuntimeException(new ExpressionException("can't clear fields from object ["+objects.getClazz().getName()+"]"));
	}

	public Collection duplicate(boolean deepCopy) {
		throw new PageRuntimeException(new ExpressionException("can't clone object of type ["+jo.getClazz().getName()+"]"));
		//return null;
	}

	

	@Override
	public boolean containsKey(Key key) {
		return Reflector.hasPropertyIgnoreCase(jo.getClazz(), key.getString());
	}

	@Override
	public Object get(Key key) throws PageException {
		return jo.get(ThreadLocalPageContext.get(), key);
	}

	@Override
	public Object get(Key key, Object defaultValue) {
		return jo.get(ThreadLocalPageContext.get(), key,defaultValue);
	}

	@Override
	public Key[] keys() {
		String[] strKeys = Reflector.getPropertyKeys(jo.getClazz());
		Key[] keys=new Key[strKeys.length];
		for(int i=0;i<strKeys.length;i++) {
			keys[i]=KeyImpl.init(strKeys[i]);
		}
		return keys;
	}

	@Override
	public Object remove(Key key) throws PageException {
		throw new ExpressionException("can't remove field ["+key.getString()+"] from object ["+jo.getClazz().getName()+"]");
	}

	@Override
	public Object removeEL(Key key) {
		return null;
	}

	@Override
	public Object set(Key key, Object value) throws PageException {
		return jo.set(ThreadLocalPageContext.get(), key, value);
	}

	@Override
	public Object setEL(Key key, Object value) {
		return jo.setEL(ThreadLocalPageContext.get(), key, value);
	}

	@Override
	public int size() {
		return keys().length;
	}

	@Override
	public DumpData toDumpData(PageContext pageContext, int maxlevel, DumpProperties dp) {
		return jo.toDumpData(pageContext, maxlevel,dp);
	}

	@Override
	public Iterator<Collection.Key> keyIterator() {
		return new KeyIterator(keys());
	}
    
    @Override
	public Iterator<String> keysAsStringIterator() {
    	return new StringIterator(keys());
    }
	
	@Override
	public Iterator<Entry<Key, Object>> entryIterator() {
		return new EntryIterator(this,keys());
	}
	
	@Override
	public Iterator<Object> valueIterator() {
		return new ValueIterator(this,keys());
	}

	@Override
	public boolean castToBooleanValue() throws PageException {
		return jo.castToBooleanValue();
	}
    
    @Override
    public Boolean castToBoolean(Boolean defaultValue) {
        return jo.castToBoolean(defaultValue);
    }

	@Override
	public DateTime castToDateTime() throws PageException {
		return jo.castToDateTime();
	}
    
    @Override
    public DateTime castToDateTime(DateTime defaultValue) {
        return jo.castToDateTime(defaultValue);
    }

	@Override
	public double castToDoubleValue() throws PageException {
		return jo.castToDoubleValue();
	}
    
    @Override
    public double castToDoubleValue(double defaultValue) {
        return jo.castToDoubleValue(defaultValue);
    }

	@Override
	public String castToString() throws PageException {
		return jo.castToString();
	}
	
	@Override
	public String castToString(String defaultValue) {
		return jo.castToString(defaultValue);
	}

	@Override
	public int compareTo(String str) throws PageException {
		return jo.compareTo(str);
	}

	@Override
	public int compareTo(boolean b) throws PageException {
		return jo.compareTo(b);
	}

	@Override
	public int compareTo(double d) throws PageException {
		return jo.compareTo(d);
	}

	@Override
	public int compareTo(DateTime dt) throws PageException {
		return jo.compareTo(dt);
	}
}
