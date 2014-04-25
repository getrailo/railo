package railo.runtime.type.util;

import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;


public class ListIteratorImpl implements ListIterator {
	
	private static final int UNDEFINED = Integer.MIN_VALUE;
	private List list;
	private int index=-1;
	private int current=UNDEFINED;

	/**
	 * Constructor of the class
	 * @param arr
	 * @param index 
	 */
	public ListIteratorImpl(List list, int index){
		this.list=list;
		this.index=index-1;
	}

	@Override
	public void add(Object o) {
		list.add(++index,o);
	}

	@Override
	public void remove() {
		if(current==UNDEFINED)throw new IllegalStateException();
		list.remove(current);
		current=UNDEFINED;
	}

	@Override
	public void set(Object o) {
		if(current==UNDEFINED) throw new IllegalStateException();
		list.set(current, o);
	}
	
/////////////	
	

	@Override
	public boolean hasNext() {
		return list.size()>index+1;
	}

	@Override
	public boolean hasPrevious() {
		return index>-1;
	}

	@Override
	public int previousIndex() {
		return index;
	}

	@Override
	public int nextIndex() {
		return index+1;
	}

	@Override
	public Object previous() {
		if(!hasPrevious())
			throw new NoSuchElementException();
		current=index;
		return list.get(index--);
	}

	@Override
	public Object next() {
		if(!hasNext())
			throw new NoSuchElementException();
		return list.get(current=++index);
	}

}
