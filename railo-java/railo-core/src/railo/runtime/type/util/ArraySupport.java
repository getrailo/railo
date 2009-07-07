package railo.runtime.type.util;

import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import railo.runtime.converter.LazyConverter;
import railo.runtime.exp.ExpressionException;
import railo.runtime.exp.PageException;
import railo.runtime.type.Array;
import railo.runtime.type.Collection;
import railo.runtime.type.KeyImpl;
import railo.runtime.type.dt.DateTime;

public abstract class ArraySupport implements Array,List {


	/**
	 * @see java.util.List#add(int, E)
	 */
	public final void add(int index, Object element) {
		try {
			insert(index+1, element);
		} catch (PageException e) {
			throw new IndexOutOfBoundsException("can't insert value to List at position "+index+", " +
					"valid values are from 0 to "+(size()-1)+", size is "+size());
		}
	}

	/**
	 * @see java.util.List#addAll(java.util.Collection)
	 */
	public final boolean addAll(java.util.Collection c) {
		Iterator it = c.iterator();
		while(it.hasNext()) {
			add(it.next());
		}
		return true;
	}

	/**
	 * @see java.util.List#addAll(int, java.util.Collection)
	 */
	public final boolean addAll(int index, java.util.Collection c) {
		Iterator it = c.iterator();
		while(it.hasNext()) {
			add(index++,it.next());
		}
		return !c.isEmpty();
	}
	
	/**
     * adds a value and return this array
     * @param o
     * @return this Array
     */
    public synchronized boolean add(Object o) {
    	appendEL(o);
        return true;
    }

	/**
	 * @see java.util.List#contains(java.lang.Object)
	 */
	public final boolean contains(Object o) {
		return indexOf(o)!=-1;
	}

	/**
	 * @see java.util.List#containsAll(java.util.Collection)
	 */
	public final boolean containsAll(java.util.Collection c) {
		Iterator it = c.iterator();
		while(it.hasNext()) {
			if(!contains(it.next()))return false;
		}
		return true;
	}

	/**
	 * @see java.util.List#indexOf(java.lang.Object)
	 */
	public final int indexOf(Object o) {
		Iterator it=iterator();
		int index=0;
		while(it.hasNext()) {
			if(it.next().equals(o))return index;
			index++;
		}
		return -1;
	}

	/**
	 * @see java.util.List#isEmpty()
	 */
	public final boolean isEmpty() {
		return size()==0;
	}

	/**
	 * @see java.util.List#lastIndexOf(java.lang.Object)
	 */
	public final int lastIndexOf(Object o) {
		Iterator it=iterator();
		int index=0;
		int rtn=-1;
		while(it.hasNext()) {
			if(it.next().equals(o))rtn=index;
			index++;
		}
		return rtn;
	}

	/**
	 * @see java.util.List#listIterator()
	 */
	public final ListIterator listIterator() {
		return new ListIteratorImpl(this,0);
	}

	/**
	 * @see java.util.List#listIterator(int)
	 */
	public final ListIterator listIterator(int index) {
		return new ListIteratorImpl(this,index);
		//return toArrayList().listIterator(index);
	}

	/**
	 * @see java.util.List#remove(java.lang.Object)
	 */
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

	/**
	 * @see java.util.List#removeAll(java.util.Collection)
	 */
	public final boolean removeAll(java.util.Collection c) {
		Iterator it = c.iterator();
		boolean rtn=false;
		while(it.hasNext()) {
			if(remove(it.next()))rtn=true;
		}
		return rtn;
	}

	/**
	 * @see java.util.List#retainAll(java.util.Collection)
	 */
	public final boolean retainAll(java.util.Collection c) {
		boolean modified = false;
		Iterator it = iterator();
		while (it.hasNext()) {
		    if(!c.contains(it.next())) {
		    	it.remove();
		    	modified = true;
		    }
		}
		return modified;
	}

	/**
	 * @see java.util.List#subList(int, int)
	 */
	public final List subList(int fromIndex, int toIndex) {
		throw new RuntimeException("method subList is not supported");
	}

	/**
	 * @see java.util.List#toArray(T[])
	 */
	public final Object[] toArray(Object[] a) {
		Iterator it = iterator();
		int i=0;
		while(it.hasNext()) {
			a[i++]=it.next();
		}
		return a;
	}

	/**
	 * @see java.util.List#get(int)
	 */
	public final Object get(int index) {
		if(index<0)	
			throw new IndexOutOfBoundsException("invalid index defintion ["+index+"], " +
					"index should be a number between [0 - "+(size()-1)+"], size is "+size());
		if(index>=size())
			throw new IndexOutOfBoundsException("invalid index ["+index+"] defintion, " +
					"index should be a number between [0 - "+(size()-1)+"], size is "+size());
		
		return get(index+1, null); 
	}

	/**
	 * @see java.util.List#remove(int)
	 */
	public final Object remove(int index) {
		if(index<0)	
			throw new IndexOutOfBoundsException("invalid index defintion ["+index+"], " +
					"index should be a number between [0 - "+(size()-1)+"], size is "+size());
		if(index>=size())
			throw new IndexOutOfBoundsException("invalid index ["+index+"] defintion, " +
					"index should be a number between [0 - "+(size()-1)+"], size is "+size());
		
		return removeEL(index+1); 
	}

	/**
	 * @see java.util.List#set(int, java.lang.Object)
	 */
	public final Object set(int index, Object element) {
		return setEL(index+1, element); 
	}
	

    /**
     * @see railo.runtime.type.Collection#containsKey(java.lang.String)
     */
    public boolean containsKey(String key) {
    	return get(KeyImpl.init(key),null)!=null;
    }
    
    /**
     * @see railo.runtime.type.Collection#_contains(java.lang.String)
     */
    public boolean containsKey(Collection.Key key) {
        return get(key,null)!=null;
    }

    /**
     * @see railo.runtime.type.Array#containsKey(int)
     */
    public boolean containsKey(int key) {
        return get(key,null)!=null;
    }


	/**
	 *
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return LazyConverter.serialize(this);
	}
	
	/**
	 * @see java.lang.Object#clone()
	 */
	public synchronized Object clone() {
		return duplicate(true);
	}

    /**
     * @see railo.runtime.op.Castable#castToString()
     */
    public String castToString() throws PageException {
        throw new ExpressionException("Can't cast Complex Object Type Array to String",
          "Use Build-In-Function \"serialize(Array):String\" to create a String from Array");
    }

    /**
     * @see railo.runtime.op.Castable#castToString(java.lang.String)
     */
    public String castToString(String defaultValue) {
        return defaultValue;
    }


    /**
     * @see railo.runtime.op.Castable#castToBooleanValue()
     */
    public boolean castToBooleanValue() throws PageException {
        throw new ExpressionException("Can't cast Complex Object Type Array to a boolean value");
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
        throw new ExpressionException("Can't cast Complex Object Type Array to a number value");
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
        throw new ExpressionException("Can't cast Complex Object Type Array to a Date");
    }
    
    /**
     * @see railo.runtime.op.Castable#castToDateTime(railo.runtime.type.dt.DateTime)
     */
    public DateTime castToDateTime(DateTime defaultValue) {
        return defaultValue;
    }

	/**
	 * @see railo.runtime.op.Castable#compare(boolean)
	 */
	public int compareTo(boolean b) throws PageException {
		throw new ExpressionException("can't compare Complex Object Type Array with a boolean value");
	}

	/**
	 * @see railo.runtime.op.Castable#compareTo(railo.runtime.type.dt.DateTime)
	 */
	public int compareTo(DateTime dt) throws PageException {
		throw new ExpressionException("can't compare Complex Object Type Array with a DateTime Object");
	}

	/**
	 * @see railo.runtime.op.Castable#compareTo(double)
	 */
	public int compareTo(double d) throws PageException {
		throw new ExpressionException("can't compare Complex Object Type Array with a numeric value");
	}

	/**
	 * @see railo.runtime.op.Castable#compareTo(java.lang.String)
	 */
	public int compareTo(String str) throws PageException {
		throw new ExpressionException("can't compare Complex Object Type Array with a String");
	}

	/**
	 * @see railo.runtime.type.Array#toList()
	 */
	public List toList() {
		return this;
	}
	
	/**
	 * @see railo.runtime.type.Iteratorable#valueIterator()
	 */
	public Iterator valueIterator() {
		return iterator();
	}
	
	
}
