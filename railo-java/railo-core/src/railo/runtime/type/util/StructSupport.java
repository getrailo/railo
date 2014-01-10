package railo.runtime.type.util;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import railo.commons.lang.CFTypes;
import railo.commons.lang.ExceptionUtil;
import railo.runtime.PageContext;
import railo.runtime.config.Config;
import railo.runtime.converter.LazyConverter;
import railo.runtime.dump.DumpData;
import railo.runtime.dump.DumpProperties;
import railo.runtime.engine.ThreadLocalPageContext;
import railo.runtime.exp.ExpressionException;
import railo.runtime.exp.PageException;
import railo.runtime.type.Collection;
import railo.runtime.type.KeyImpl;
import railo.runtime.type.Sizeable;
import railo.runtime.type.Struct;
import railo.runtime.type.UDFPlus;
import railo.runtime.type.dt.DateTime;
import railo.runtime.type.it.KeyAsStringIterator;

public abstract class StructSupport implements Map,Struct,Sizeable {

	private static final long serialVersionUID = 7433668961838400995L;

	/**
	 * throw exception for invalid key
	 * @param key Invalid key
	 * @return returns an invalid key Exception
	 */
	public static ExpressionException invalidKey(Config config,Struct sct,Key key) {
		StringBuilder sb=new StringBuilder();
		Iterator<Key> it = sct.keyIterator();
		Key k;

		while(it.hasNext()){
			k = it.next();
			if( k.equals( key ) )
				return new ExpressionException( "the value from key [" + key.getString() + "] is NULL, which is the same as not existing in CFML" );
			if(sb.length()>0)sb.append(',');
			sb.append(k.getString());
		}
		config=ThreadLocalPageContext.getConfig(config);
		if(config!=null && config.debug())
			return new ExpressionException(ExceptionUtil.similarKeyMessage(sct, key.getString(), "key", "keys",true));
		
		
		return new ExpressionException( "key [" + key.getString() + "] doesn't exist (existing keys:" + sb.toString() + ")" );
	}
	
	@Override
	public long sizeOf() {
		return StructUtil.sizeOf(this);
	}
	
	@Override
	public Set entrySet() {
		return StructUtil.entrySet(this);
	}

	@Override
	public final Object get(Object key) {
		return get(KeyImpl.toKey(key,null), null);
	}

	@Override
	public final boolean isEmpty() {
		return size()==0;
	}

	@Override
	public Set keySet() {
		return StructUtil.keySet(this);
	}

	@Override
	public Object put(Object key, Object value) {
		return setEL(KeyImpl.toKey(key,null), value);
	}

	@Override
	public final void putAll(Map t) {
		StructUtil.putAll(this, t);
	}

	@Override
	public final Object remove(Object key) {
		return removeEL(KeyImpl.toKey(key,null));
	}

	@Override
	public final Object clone(){
		return duplicate(true);
	}
	
	@Override
	public final boolean containsKey(Object key) {
		return containsKey(KeyImpl.toKey(key,null));
	}

	@Override
	public final boolean containsKey(String key) {
		return containsKey(KeyImpl.init(key));
	}

	@Override
	public final Object get(String key, Object defaultValue) {
		return get(KeyImpl.init(key), defaultValue);
	}

	@Override
	public final Object get(String key) throws PageException {
		return get(KeyImpl.init(key));
	}

	@Override
	public final Object set(String key, Object value) throws PageException {
		return set(KeyImpl.init(key), value);
	}

	@Override
	public final Object setEL(String key, Object value) {
		return setEL(KeyImpl.init(key), value);
	}

	@Override
	public DumpData toDumpData(PageContext pageContext, int maxlevel,DumpProperties properties) {
		return StructUtil.toDumpTable(this,"Struct",pageContext,maxlevel,properties);
	}

	@Override
	public boolean castToBooleanValue() throws PageException {
        throw new ExpressionException("can't cast Complex Object Type Struct to a boolean value");
    }
    
    @Override
	public Boolean castToBoolean(Boolean defaultValue) {
        return defaultValue;
    }

    @Override
	public double castToDoubleValue() throws PageException {
        throw new ExpressionException("can't cast Complex Object Type Struct to a number value");
    }
    
    @Override
	public double castToDoubleValue(double defaultValue) {
        return defaultValue;
    }

    @Override
	public DateTime castToDateTime() throws PageException {
        throw new ExpressionException("can't cast Complex Object Type Struct to a Date");
    }
    
    @Override
	public DateTime castToDateTime(DateTime defaultValue) {
        return defaultValue;
    }

    @Override
	public String castToString() throws PageException {
        throw new ExpressionException("Can't cast Complex Object Type Struct to String",
          "Use Built-In-Function \"serialize(Struct):String\" to create a String from Struct");
    }

    @Override
	public String castToString(String defaultValue) {
        return defaultValue;
    }

    @Override
	public int compareTo(boolean b) throws PageException {
		throw new ExpressionException("can't compare Complex Object Type Struct with a boolean value");
	}

	@Override
	public int compareTo(DateTime dt) throws PageException {
		throw new ExpressionException("can't compare Complex Object Type Struct with a DateTime Object");
	}

	@Override
	public int compareTo(double d) throws PageException {
		throw new ExpressionException("can't compare Complex Object Type Struct with a numeric value");
	}

	@Override
	public int compareTo(String str) throws PageException {
		throw new ExpressionException("can't compare Complex Object Type Struct with a String");
	}
	
	@Override
	public String toString() {
		return LazyConverter.serialize(this);
	}

	@Override
	public java.util.Collection values() {
		return StructUtil.values(this);
	}

	@Override
	public boolean containsValue(Object value) {
		return values().contains(value);
	}
	
    @Override
	public Iterator<String> keysAsStringIterator() {
    	return new KeyAsStringIterator(keyIterator());
    }

    @Override
	public Object get(PageContext pc, Key key, Object defaultValue) {
		return get(key, defaultValue);
	}

    @Override
	public Object get(PageContext pc, Key key) throws PageException {
		return get(key);
	}

    @Override
	public Object set(PageContext pc, Key propertyName, Object value) throws PageException {
		return set(propertyName, value);
	}

    @Override
	public Object setEL(PageContext pc, Key propertyName, Object value) {
		return setEL(propertyName, value);
	}

    @Override
	public Object call(PageContext pc, Key methodName, Object[] args) throws PageException {
		Object obj = get(methodName,null);
		if(obj instanceof UDFPlus) {
			return ((UDFPlus)obj).call(pc,methodName,args,false);
		}
		return MemberUtil.call(pc, this, methodName, args, CFTypes.TYPE_STRUCT, "struct");
	}

    @Override
	public Object callWithNamedValues(PageContext pc, Key methodName, Struct args) throws PageException {
		Object obj = get(methodName,null);
		if(obj instanceof UDFPlus) {
			return ((UDFPlus)obj).callWithNamedValues(pc,methodName,args,false);
		}
		return MemberUtil.callWithNamedValues(pc,this,methodName,args, CFTypes.TYPE_STRUCT, "struct");
	}
    
    public java.util.Iterator<String> getIterator() {
    	return keysAsStringIterator();
    } 

    @Override
	public boolean equals(Object obj){
		if(!(obj instanceof Collection)) return false;
		return CollectionUtil.equals(this,(Collection)obj);
	}

    /*@Override
	public int hashCode() {
		return CollectionUtil.hashCode(this);
	}*/
}
