package railo.runtime.util;

import java.util.Iterator;

/**
 * Iterator Implementation for a Object Array
 */
public final class ArrayIterator implements Iterator {
	
	private Object[] arr;
	private int offset;
	private int length;

	/**
	 * constructor for the class
	 * @param arr Base Array
	 */
	public ArrayIterator(Object[] arr) {
		this.arr=arr;
		this.offset=0;
		this.length=arr.length;
	}

	public ArrayIterator(Object[] arr, int offset, int length) {
		this.arr=arr;
		this.offset=offset;
		this.length=offset+length;
		if(this.length>arr.length)this.length=arr.length;
		
	}

	public ArrayIterator(Object[] arr, int offset) {
		this.arr=arr;
		this.offset=offset;
		this.length=arr.length;
		
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
		return (length)>offset;
	}

	/**
	 * @see java.util.Iterator#next()
	 */
	public Object next() {
		return arr[offset++];
	}
	
}