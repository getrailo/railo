package railo.runtime.type.wrap;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map.Entry;

import railo.commons.lang.CFTypes;
import railo.runtime.PageContext;
import railo.runtime.converter.LazyConverter;
import railo.runtime.dump.DumpData;
import railo.runtime.dump.DumpProperties;
import railo.runtime.dump.DumpUtil;
import railo.runtime.exp.ExpressionException;
import railo.runtime.exp.PageException;
import railo.runtime.op.Caster;
import railo.runtime.op.Duplicator;
import railo.runtime.type.Array;
import railo.runtime.type.ArrayImpl;
import railo.runtime.type.Collection;
import railo.runtime.type.KeyImpl;
import railo.runtime.type.Sizeable;
import railo.runtime.type.Struct;
import railo.runtime.type.dt.DateTime;
import railo.runtime.type.it.EntryIterator;
import railo.runtime.type.it.KeyIterator;
import railo.runtime.type.it.StringIterator;
import railo.runtime.type.util.ArrayUtil;
import railo.runtime.type.util.MemberUtil;

/**
 * 
 */
public class ListAsArray implements Array,List,Sizeable {

	protected List list;
	
	private ListAsArray(List list) {
		this.list=list;
	}
	
	public static Array toArray(List list) {
		if(list instanceof ArrayAsList) return ((ArrayAsList)list).array;
		if(list instanceof Array) return (Array) list;
		return new ListAsArray(list);
	}
	
	
	@Override
	public Object append(Object o) throws PageException {
		list.add(o);
		return o;
	}
	
	
	@Override
	public Object appendEL(Object o) {
		list.add(o);
		return o;
	}

	@Override
	public boolean containsKey(int index) {
		return get(index-1,null)!=null;
	}

	@Override
	public Object get(int key, Object defaultValue) {
		try {
			Object rtn = list.get(key-1);
			if(rtn==null) return defaultValue;
			return rtn;
		}
		catch(Throwable t) {
			return defaultValue;
		}
	}

	@Override
	public Object getE(int key) throws PageException {
		try {
			Object rtn = list.get(key-1);
			if(rtn==null) throw new ExpressionException("Element at position ["+key+"] does not exist in list");
			return rtn;
		}
		catch(Throwable t) {
			throw new ExpressionException("Element at position ["+key+"] does not exist in list",t.getMessage());
		}
	}

	@Override
	public int getDimension() {
		return 1;
	}

	public boolean insert(int key, Object value) throws PageException {
		try {
		list.add(key-1, value);
		}
		catch(Throwable t) {
			throw new ExpressionException("can't insert value to array at position "+key+", array goes from 1 to "+size());
		}
		return true;
	}

	public int[] intKeys() {
		ListIterator lit = list.listIterator();
		ArrayList keys = new ArrayList();
		int index=0;
		Object v;
		while(lit.hasNext()) {
			index=lit.nextIndex()+1;
			v=lit.next();
			if(v!=null)keys.add(Integer.valueOf(index));
		}
		int[] intKeys = new int[keys.size()];
		Iterator it = keys.iterator();
		index=0;
		while(it.hasNext()) {
			intKeys[index++]=((Integer)it.next()).intValue();
		}
		
		return intKeys;
	}

	@Override
	public Object prepend(Object o) throws PageException {
		list.add(0,o);
		return o;
	}

	public Object removeE(int key) throws PageException {
		try {
		return list.remove(key-1);
		}
		catch(Throwable t) {
			throw new ExpressionException("can not remove Element at position ["+key+"]",t.getMessage());
		}
	}

	@Override
	public Object removeEL(int key) {
		try {
			return removeE(key);
		} catch (PageException e) {
			return null;
		}
	}

	public void resize(int to) throws PageException {
		while(size()<to)list.add(null);
	}

	@Override
	public Object setE(int key, Object value) throws PageException {
		if(key<=size()) {
			try {
			list.set(key-1, value);
			}
			catch(Throwable t) {
				throw new ExpressionException("can not set Element at position ["+key+"]",t.getMessage());
			}
			
		}
		else {
			while(size()<key-1)list.add(null);
			list.add(value);
		}
		return value;
	}

	@Override
	public Object setEL(int key, Object value) {
		try {
			return setE(key, value);
		} catch (Throwable t) {}
		return value;
	}

	@Override
	public void sort(String sortType, String sortOrder) throws PageException {
		sort(ArrayUtil.toComparator(null, sortType, sortOrder, false));
	}

	@Override
	public synchronized void sort(Comparator comp) throws PageException {
		if(getDimension()>1)
			throw new ExpressionException("only 1 dimensional arrays can be sorted");
		Collections.sort(list,comp);
	}

	@Override
	public Object[] toArray() {
		return list.toArray();
	}

	public ArrayList toArrayList() {
		return new ArrayList(list);
	}

	@Override
	public void clear() {
		list.clear();
	}

	@Override
	public boolean containsKey(String key) {
		return get(key,null)!=null;
	}

	@Override
	public boolean containsKey(Key key) {
		return get(key,null)!=null;
	}

	@Override
	public Collection duplicate(boolean deepCopy) {new ArrayImpl().duplicate(deepCopy);
		return new ListAsArray((List)Duplicator.duplicate(list,deepCopy));
	}

	

	@Override
	public Object get(String key) throws PageException {
		return getE(Caster.toIntValue(key));
	}

	@Override
	public Object get(Key key) throws PageException {
		return get(key.getString());
	}

	@Override
	public Object get(String key, Object defaultValue) {
		double index=Caster.toIntValue(key,Integer.MIN_VALUE);
		if(index==Integer.MIN_VALUE) return defaultValue;
	    return get((int)index,defaultValue);
	}

	@Override
	public Object get(Key key, Object defaultValue) {
		return get(key.getString(),defaultValue);
	}

	@Override
	public Key[] keys() {
		int[] intKeys = intKeys();
		Collection.Key[] keys = new Collection.Key[intKeys.length];
		for(int i=0;i<intKeys.length;i++) {
			keys[i]=KeyImpl.init(Caster.toString(intKeys[i]));
		}
		return keys;
	}

	@Override
	public Object remove(Key key) throws PageException {
		return removeE(Caster.toIntValue(key.getString()));
	}

	@Override
	public Object removeEL(Key key) {
		double index=Caster.toIntValue(key.getString(),Integer.MIN_VALUE);
		if(index==Integer.MIN_VALUE) return null;
	    return removeEL((int)index);
	}

	@Override
	public Object set(String key, Object value) throws PageException {
		return setE(Caster.toIntValue(key),value);
	}

	@Override
	public Object set(Key key, Object value) throws PageException {
		return set(key.getString(),value);
	}

	@Override
	public Object setEL(String key, Object value) {
		double index=Caster.toIntValue(key,Integer.MIN_VALUE);
		if(index==Integer.MIN_VALUE) return value;
	    return setEL((int)index,value);
	}

	@Override
	public Object setEL(Key key, Object value) {
		return setEL(key.getString(), value);
	}

	@Override
	public int size() {
		return list.size();
	}

	public DumpData toDumpData(PageContext pageContext, int maxlevel, DumpProperties dp) {
		return DumpUtil.toDumpData(list, pageContext,maxlevel,dp);
	}

	@Override
	public Iterator iterator() {
		return list.iterator();
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
    public String castToString() throws PageException {
        throw new ExpressionException("Can't cast Complex Object Type "+Caster.toClassName(list)+" to String",
          "Use Built-In-Function \"serialize(Array):String\" to create a String from Array");
    }

	@Override
	public String castToString(String defaultValue) {
		return defaultValue;
	}


    @Override
    public boolean castToBooleanValue() throws PageException {
        throw new ExpressionException("Can't cast Complex Object Type "+Caster.toClassName(list)+" to a boolean value");
    }
    
    @Override
    public Boolean castToBoolean(Boolean defaultValue) {
        return defaultValue;
    }


    @Override
    public double castToDoubleValue() throws PageException {
        throw new ExpressionException("Can't cast Complex Object Type "+Caster.toClassName(list)+" to a number value");
    }
    
    @Override
    public double castToDoubleValue(double defaultValue) {
        return defaultValue;
    }


    @Override
    public DateTime castToDateTime() throws PageException {
        throw new ExpressionException("Can't cast Complex Object Type "+Caster.toClassName(list)+" to a Date");
    }
    
    @Override
    public DateTime castToDateTime(DateTime defaultValue) {
        return defaultValue;
    }

	@Override
	public int compareTo(boolean b) throws PageException {
		throw new ExpressionException("can't compare Complex Object Type "+Caster.toClassName(list)+" with a boolean value");
	}

	@Override
	public int compareTo(DateTime dt) throws PageException {
		throw new ExpressionException("can't compare Complex Object Type "+Caster.toClassName(list)+" with a DateTime Object");
	}

	@Override
	public int compareTo(double d) throws PageException {
		throw new ExpressionException("can't compare Complex Object Type "+Caster.toClassName(list)+" with a numeric value");
	}

	@Override
	public int compareTo(String str) throws PageException {
		throw new ExpressionException("can't compare Complex Object Type "+Caster.toClassName(list)+" with a String");
	}

	@Override
	public String toString() {
		return LazyConverter.serialize(this);
	}
	
	@Override
	public Object clone() {
		return duplicate(true);
	}

	@Override
	public boolean add(Object o) {
		return list.add(o);
	}

	@Override
	public void add(int index, Object element) {
		list.add(index, element);
	}

	@Override
	public boolean addAll(java.util.Collection c) {
		return list.addAll(c);
	}

	@Override
	public boolean addAll(int index, java.util.Collection c) {
		return list.addAll(index, c);
	}

	@Override
	public boolean contains(Object o) {
		return list.contains(o);
	}

	@Override
	public boolean containsAll(java.util.Collection c) {
		return list.contains(c);
	}

	@Override
	public Object get(int index) {
		return list.get(index);
	}

	@Override
	public int indexOf(Object o) {
		return list.indexOf(o);
	}

	@Override
	public boolean isEmpty() {
		return list.isEmpty();
	}

	@Override
	public int lastIndexOf(Object o) {
		return list.lastIndexOf(o);
	}

	@Override
	public ListIterator listIterator() {
		return list.listIterator();
	}

	@Override
	public ListIterator listIterator(int index) {
		return list.listIterator(index);
	}

	@Override
	public boolean remove(Object o) {
		return list.remove(o);
	}

	@Override
	public Object remove(int index) {
		return list.remove(index);
	}

	@Override
	public boolean removeAll(java.util.Collection c) {
		return list.removeAll(c);
	}

	@Override
	public boolean retainAll(java.util.Collection c) {
		return list.retainAll(c);
	}

	@Override
	public Object set(int index, Object element) {
		return list.set(index, element);
	}

	@Override
	public List subList(int fromIndex, int toIndex) {
		return list.subList(fromIndex, toIndex);
	}

	@Override
	public Object[] toArray(Object[] a) {
		return list.toArray(a);
	}


	@Override
	public List toList() {
		return this;
	}

	public Iterator valueIterator() {
		return list.iterator();
	}

	@Override
	public long sizeOf() {
		return ArrayUtil.sizeOf(list);
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
}
