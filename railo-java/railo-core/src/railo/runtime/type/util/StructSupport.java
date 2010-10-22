package railo.runtime.type.util;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import railo.runtime.PageContext;
import railo.runtime.converter.LazyConverter;
import railo.runtime.dump.DumpData;
import railo.runtime.dump.DumpProperties;
import railo.runtime.exp.ExpressionException;
import railo.runtime.exp.PageException;
import railo.runtime.type.Collection;
import railo.runtime.type.KeyImpl;
import railo.runtime.type.List;
import railo.runtime.type.Sizeable;
import railo.runtime.type.Struct;
import railo.runtime.type.dt.DateTime;

public abstract class StructSupport implements Map,Struct,Sizeable {



	/**
	 * throw exception for invalid key
	 * @param key Invalid key
	 * @return returns a invalid key Exception
	 */
	protected ExpressionException invalidKey(Key key) {
		return new ExpressionException("key ["+key.getString()+"] doesn't exist in struct (keys:"+List.arrayToList(keysAsString(), ",")+")");
	}
	
	
	/**
	 * @see railo.runtime.type.Sizeable#sizeOf()
	 */
	public long sizeOf() {
		return StructUtil.sizeOf(this);
	}
	
	/**
	 * @see java.util.Map#entrySet()
	 */
	public Set entrySet() {
		return StructUtil.entrySet(this);
	}

	/**
	 * @see java.util.Map#get(java.lang.Object)
	 */
	public final Object get(Object key) {
		return get(KeyImpl.toKey(key,null), null);
	}

	/**
	 * @see java.util.Map#isEmpty()
	 */
	public final boolean isEmpty() {
		return size()==0;
	}

	/**
	 * @see java.util.Map#keySet()
	 */
	public Set keySet() {
		return StructUtil.keySet(this);
	}

	/**
	 * @see java.util.Map#put(K, V)
	 */
	public final Object put(Object key, Object value) {
		return setEL(KeyImpl.toKey(key,null), value);
	}

	/**
	 * @see java.util.Map#putAll(java.util.Map)
	 */
	public final void putAll(Map t) {
		StructUtil.putAll(this, t);
	}

	/**
	 * @see java.util.Map#remove(java.lang.Object)
	 */
	public final Object remove(Object key) {
		return removeEL(KeyImpl.toKey(key,null));
	}

	/**
	 * @see java.lang.Object#clone()
	 */
	public final Object clone(){
		return duplicate(true);
	}
	
	/**
	 * @see java.util.Map#containsKey(java.lang.Object)
	 */
	public final boolean containsKey(Object key) {
		return containsKey(KeyImpl.toKey(key,null));
	}

	/**
	 * @see railo.runtime.type.Collection#containsKey(java.lang.String)
	 */
	public final boolean containsKey(String key) {
		return containsKey(KeyImpl.init(key));
	}

	/**
	 * @see railo.runtime.type.Collection#get(java.lang.String, java.lang.Object)
	 */
	public final Object get(String key, Object defaultValue) {
		return get(KeyImpl.init(key), defaultValue);
	}

	/**
	 * @see railo.runtime.type.Collection#get(java.lang.String)
	 */
	public final Object get(String key) throws PageException {
		return get(KeyImpl.init(key));
	}

	/**
	 * @see railo.runtime.type.Collection#set(java.lang.String, java.lang.Object)
	 */
	public final Object set(String key, Object value) throws PageException {
		return set(KeyImpl.init(key), value);
	}

	/**
	 * @see railo.runtime.type.Collection#setEL(java.lang.String, java.lang.Object)
	 */
	public final Object setEL(String key, Object value) {
		//print.dumpStack("StructSupport.setEL");
		return setEL(KeyImpl.init(key), value);
	}

	/**
	 * @see railo.runtime.dump.Dumpable#toDumpData(railo.runtime.PageContext, int, railo.runtime.dump.DumpProperties)
	 */
	public DumpData toDumpData(PageContext pageContext, int maxlevel,DumpProperties properties) {
		return StructUtil.toDumpTable(this,"Struct",pageContext,maxlevel,properties);
	}

	/**
     * @see railo.runtime.op.Castable#castToBooleanValue()
     */
    public boolean castToBooleanValue() throws PageException {
        throw new ExpressionException("can't cast Complex Object Type Struct to a boolean value");
    }
    
    /**
     * @see railo.runtime.op.Castable#castToBoolean(java.lang.Boolean)
     */
    public Boolean castToBoolean(Boolean defaultValue) {
        return defaultValue;
    }

    /**
     * @see railo.runtime.op.Castable#castToDoubleValue()
     */
    public double castToDoubleValue() throws PageException {
        throw new ExpressionException("can't cast Complex Object Type Struct to a number value");
    }
    
    /**
     * @see railo.runtime.op.Castable#castToDoubleValue(double)
     */
    public double castToDoubleValue(double defaultValue) {
        return defaultValue;
    }


    /**
     * @see railo.runtime.op.Castable#castToDateTime()
     */
    public DateTime castToDateTime() throws PageException {
        throw new ExpressionException("can't cast Complex Object Type Struct to a Date");
    }
    
    /**
     * @see railo.runtime.op.Castable#castToDateTime(railo.runtime.type.dt.DateTime)
     */
    public DateTime castToDateTime(DateTime defaultValue) {
        return defaultValue;
    }

    /**
     * @see railo.runtime.op.Castable#castToString()
     */
    public String castToString() throws PageException {
        throw new ExpressionException("Can't cast Complex Object Type Struct to String",
          "Use Build-In-Function \"serialize(Struct):String\" to create a String from Struct");
    }

    /**
     * @see railo.runtime.op.Castable#castToString(java.lang.String)
     */
    public String castToString(String defaultValue) {
        return defaultValue;
    }


	/**
	 * @see railo.runtime.op.Castable#compare(boolean)
	 */
	public int compareTo(boolean b) throws PageException {
		throw new ExpressionException("can't compare Complex Object Type Struct with a boolean value");
	}

	/**
	 * @see railo.runtime.op.Castable#compareTo(railo.runtime.type.dt.DateTime)
	 */
	public int compareTo(DateTime dt) throws PageException {
		throw new ExpressionException("can't compare Complex Object Type Struct with a DateTime Object");
	}

	/**
	 * @see railo.runtime.op.Castable#compareTo(double)
	 */
	public int compareTo(double d) throws PageException {
		throw new ExpressionException("can't compare Complex Object Type Struct with a numeric value");
	}

	/**
	 * @see railo.runtime.op.Castable#compareTo(java.lang.String)
	 */
	public int compareTo(String str) throws PageException {
		throw new ExpressionException("can't compare Complex Object Type Struct with a String");
	}
	
	/**
	 *
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return LazyConverter.serialize(this);
	}

	/**
	 * @see java.util.Map#values()
	 */
	public java.util.Collection values() {
		return StructUtil.values(this);
	}

	/**
	 * @see java.util.Map#containsValue(java.lang.Object)
	 */
	public boolean containsValue(Object value) {
		return values().contains(value);
	}
	
	/**
	 *
	 * @see railo.runtime.type.Iteratorable#iterator()
	 */
	public final Iterator iterator() {
		return keyIterator();
	}
	

	/**
	 * @see railo.runtime.type.Iteratorable#valueIterator()
	 */
	public Iterator valueIterator() {
		return values().iterator();
	}
	
	public boolean equals(Object obj){
		if(!(obj instanceof Collection)) return false;
		return CollectionUtil.equals(this,(Collection)obj);
	}
}
