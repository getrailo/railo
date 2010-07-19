package railo.runtime.type.it;

import java.util.Enumeration;
import java.util.Iterator;

import railo.print;
import railo.runtime.type.Collection;
import railo.runtime.type.Collection.Key;

/**
 * Iterator Implementation for a Object Array
 */
public final class KeyIterator implements Iterator,Enumeration {
	
	private Collection.Key[] arr;
	private int pos;

	/**
	 * constructor for the class
	 * @param arr Base Array
	 */
	public KeyIterator(Collection.Key[] arr) {
		if(arr==null)throw new NullPointerException();
		for(int i=0;i<arr.length;i++){
			if(arr[i]==null) print.ds();
		}
		
		
		this.arr=arr;
		this.pos=0;
	}

	/**
	 * @see java.util.Iterator#remove()
	 */
	public void remove() {
		throw new UnsupportedOperationException("this operation is not suppored");
	}

	/**
	 * @see java.util.Iterator#hasNext()
	 */
	public boolean hasNext() {
		return (arr.length)>pos;
	}

	/**
	 * @see java.util.Iterator#next()
	 */
	public Object next() {
		Key key = arr[pos++];
		if(key==null) return null;
		return key.getString();
	}

	public boolean hasMoreElements() {
		return hasNext();
	}

	public Object nextElement() {
		return next();
	}
}