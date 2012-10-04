package railo.runtime.type.it;

import java.util.Enumeration;
import java.util.Iterator;

import railo.runtime.type.Collection;

/**
 * Iterator Implementation for a Object Array
 */
public final class StringIterator implements Iterator<String>,Enumeration<String> {
	
	private Collection.Key[] arr;
	private int pos;

	/**
	 * constructor for the class
	 * @param arr Base Array
	 */
	public StringIterator(Collection.Key[] arr) {
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
	public String next() {
		Collection.Key key = arr[pos++];
		if(key==null) return null;
		return key.getString();
	}

	public boolean hasMoreElements() {
		return hasNext();
	}

	public String nextElement() {
		return next();
	}
}