package railo.runtime.type.it;

import java.util.ListIterator;
import java.util.NoSuchElementException;

import railo.runtime.type.Array;


public class ArrayListIteratorImpl implements ListIterator {
	
	private static final int UNDEFINED = Integer.MIN_VALUE;
	private Array array;
	private int index=-1;
	private int current=UNDEFINED;

	/**
	 * Constructor of the class
	 * @param arr
	 * @param index 
	 */
	public ArrayListIteratorImpl(Array array, int index){
		this.array=array;
		this.index=index-1;
	}

	@Override
	public void add(Object o) {
		array.setEL((++index)+1,o);
	}

	public void remove() {
		if(current==UNDEFINED)throw new IllegalStateException();
		array.removeEL(current+1);
		current=UNDEFINED;
	}

	public void set(Object o) {
		if(current==UNDEFINED) throw new IllegalStateException();
		array.setEL(current+1, o);
	}
	
/////////////	
	

	public boolean hasNext() {
		return array.size()>index+1;
	}

	public boolean hasPrevious() {
		return index>-1;
	}

	public int previousIndex() {
		return index;
	}

	public int nextIndex() {
		return index+1;
	}

	public Object previous() {
		if(!hasPrevious())
			throw new NoSuchElementException();
		current=index;
		return array.get((index--)+1,null);
	}

	public Object next() {
		if(!hasNext())
			throw new NoSuchElementException();
		return array.get((current=++index)+1,null);
	}

}
