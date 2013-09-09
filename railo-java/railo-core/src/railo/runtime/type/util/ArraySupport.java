package railo.runtime.type.util;

import java.util.AbstractList;
import java.util.Iterator;
import java.util.List;

import railo.commons.lang.CFTypes;
import railo.runtime.PageContext;
import railo.runtime.converter.LazyConverter;
import railo.runtime.exp.ExpressionException;
import railo.runtime.exp.PageException;
import railo.runtime.exp.PageRuntimeException;
import railo.runtime.op.Caster;
import railo.runtime.type.Array;
import railo.runtime.type.Collection;
import railo.runtime.type.KeyImpl;
import railo.runtime.type.Objects;
import railo.runtime.type.Sizeable;
import railo.runtime.type.Struct;
import railo.runtime.type.dt.DateTime;

public abstract class ArraySupport extends AbstractList implements Array,List,Sizeable,Objects {

	
	public static final short TYPE_OBJECT = 0;
	public static final short TYPE_BOOLEAN = 1;
	public static final short TYPE_BYTE = 2;
	public static final short TYPE_SHORT = 3;
	public static final short TYPE_INT = 4;
	public static final short TYPE_LONG = 5;
	public static final short TYPE_FLOAT = 6;
	public static final short TYPE_DOUBLE = 7;
	public static final short TYPE_CHARACTER = 8;
	public static final short TYPE_STRING = 9;
	
	@Override
	public final void add(int index, Object element) {
		try {
			insert(index+1, element);
		} catch (PageException e) {
			throw new IndexOutOfBoundsException("can't insert value to List at position "+index+", " +
					"valid values are from 0 to "+(size()-1)+", size is "+size());
		}
	}

	@Override
	public final boolean addAll(java.util.Collection c) {
		Iterator it = c.iterator();
		while(it.hasNext()) {
			add(it.next());
		}
		return true;
	}
	
	@Override
	public final boolean remove(Object o) {
		int index = indexOf(o);
		if(index==-1) return false;
		
		try {
			removeE(index+1);
		} catch (PageException e) {
			return false;
		}
		return true;
	}

	@Override
	public final boolean removeAll(java.util.Collection c) {
		Iterator it = c.iterator();
		boolean rtn=false;
		while(it.hasNext()) {
			if(remove(it.next()))rtn=true;
		}
		return rtn;
	}

	@Override
	public final boolean retainAll(java.util.Collection c) {
		boolean modified = false;
		Key[] keys = CollectionUtil.keys(this);
		Key k;
		for(int i=keys.length-1;i>=0;i--) {
			k = keys[i];
			if(!c.contains(get(k,null))) {
		    	removeEL(k);
		    	modified = true;
		    }
		}
		return modified;
	}

	@Override
	public final Object[] toArray(Object[] a) {
		if(a==null) return toArray();
		
		
		Class trgClass=a.getClass().getComponentType();
		short type=TYPE_OBJECT;
		if(trgClass==Boolean.class) type=TYPE_BOOLEAN;
		else if(trgClass==Byte.class) type=TYPE_BYTE;
		else if(trgClass==Short.class) type=TYPE_SHORT;
		else if(trgClass==Integer.class) type=TYPE_INT;
		else if(trgClass==Long.class) type=TYPE_LONG;
		else if(trgClass==Float.class) type=TYPE_FLOAT;
		else if(trgClass==Double.class) type=TYPE_DOUBLE;
		else if(trgClass==Character.class) type=TYPE_CHARACTER;
		else if(trgClass==String.class) type=TYPE_STRING;
		
		
		Iterator it = iterator();
		int i=0;
		Object o;
		try {
			while(it.hasNext()) {
				o=it.next();
				switch(type){
				case TYPE_BOOLEAN:
					o=Caster.toBoolean(o);
				break;
				case TYPE_BYTE:
					o=Caster.toByte(o);
				break;
				case TYPE_CHARACTER:
					o=Caster.toCharacter(o);
				break;
				case TYPE_DOUBLE:
					o=Caster.toDouble(o);
				break;
				case TYPE_FLOAT:
					o=Caster.toFloat(o);
				break;
				case TYPE_INT:
					o=Caster.toInteger(o);
				break;
				case TYPE_LONG:
					o=Caster.toLong(o);
				break;
				case TYPE_SHORT:
					o=Caster.toShort(o);
				break;
				case TYPE_STRING:
					o=Caster.toString(o);
				break;
				}
				a[i++]=o;
			}
		}
		catch(PageException pe){
			throw new PageRuntimeException(pe);
		}
		return a;
	}

	@Override
	public final Object get(int index) {
		if(index<0)	
			throw new IndexOutOfBoundsException("invalid index defintion ["+index+"], " +
					"index should be a number between [0 - "+(size()-1)+"], size is "+size());
		if(index>=size())
			throw new IndexOutOfBoundsException("invalid index ["+index+"] defintion, " +
					"index should be a number between [0 - "+(size()-1)+"], size is "+size());
		
		return get(index+1, null); 
	}

	@Override
	public final Object remove(int index) {
		if(index<0)	
			throw new IndexOutOfBoundsException("invalid index defintion ["+index+"], " +
					"index should be a number between [0 - "+(size()-1)+"], size is "+size());
		if(index>=size())
			throw new IndexOutOfBoundsException("invalid index ["+index+"] defintion, " +
					"index should be a number between [0 - "+(size()-1)+"], size is "+size());
		
		return removeEL(index+1); 
	}

	@Override
	public final Object set(int index, Object element) {
		Object o=get(index);
		setEL(index+1, element); 
		return o;
	}
	

    @Override
    public boolean containsKey(String key) {
    	return get(KeyImpl.init(key),null)!=null;
    }
    
    @Override
    public boolean containsKey(Collection.Key key) {
        return get(key,null)!=null;
    }

    @Override
    public boolean containsKey(int key) {
        return get(key,null)!=null;
    }


	@Override
	public String toString() {
		return LazyConverter.serialize(this);
	}
	
	@Override
	public synchronized Object clone() {
		return duplicate(true);
	}

    @Override
    public String castToString() throws PageException {
        throw new ExpressionException("Can't cast Complex Object Type Array to String",
          "Use Built-In-Function \"serialize(Array):String\" to create a String from Array");
    }

    @Override
    public String castToString(String defaultValue) {
        return defaultValue;
    }


    @Override
    public boolean castToBooleanValue() throws PageException {
        throw new ExpressionException("Can't cast Complex Object Type Array to a boolean value");
    }
    
    @Override
    public Boolean castToBoolean(Boolean defaultValue) {
        return defaultValue;
    }
    


    @Override
    public double castToDoubleValue() throws PageException {
        throw new ExpressionException("Can't cast Complex Object Type Array to a number value");
    }
    
    @Override
    public double castToDoubleValue(double defaultValue) {
        return defaultValue;
    }
    


    @Override
    public DateTime castToDateTime() throws PageException {
        throw new ExpressionException("Can't cast Complex Object Type Array to a Date");
    }
    
    @Override
    public DateTime castToDateTime(DateTime defaultValue) {
        return defaultValue;
    }

	@Override
	public int compareTo(boolean b) throws PageException {
		throw new ExpressionException("can't compare Complex Object Type Array with a boolean value");
	}

	@Override
	public int compareTo(DateTime dt) throws PageException {
		throw new ExpressionException("can't compare Complex Object Type Array with a DateTime Object");
	}

	@Override
	public int compareTo(double d) throws PageException {
		throw new ExpressionException("can't compare Complex Object Type Array with a numeric value");
	}

	@Override
	public int compareTo(String str) throws PageException {
		throw new ExpressionException("can't compare Complex Object Type Array with a String");
	}

	@Override
	public List toList() {
		return this;
	}
	
	@Override
	public Iterator<Object> valueIterator() {
		return iterator();
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
		return MemberUtil.call(pc, this, methodName, args, CFTypes.TYPE_ARRAY, "array");
	}

	@Override
	public Object callWithNamedValues(PageContext pc, Key methodName, Struct args) throws PageException {
		return MemberUtil.callWithNamedValues(pc,this,methodName,args, CFTypes.TYPE_ARRAY, "array");
	}

	@Override
	public java.util.Iterator<Object> getIterator() {
    	return valueIterator();
    } 

	@Override
	public synchronized void sort(String sortType, String sortOrder) throws PageException {
		if(getDimension()>1)
			throw new ExpressionException("only 1 dimensional arrays can be sorted");
		sort(ArrayUtil.toComparator(null, sortType, sortOrder,false));
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
