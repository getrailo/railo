package railo.commons.collection;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

public class QueueMaxSize<E> implements Queue<E> {
	
	private int maxSize;
	private LinkedList<E> list=new LinkedList<E>();

	public QueueMaxSize(int maxSize){
		this.maxSize=maxSize;
	}


	@Override
	public boolean add(E e) {
		if(!list.add(e)) return false;
        while (size() > maxSize) { list.remove(); }
        return true;
	}

	@Override
	public int size() {
		return list.size();
	}

	@Override
	public boolean contains(Object o) {
		return list.contains(o);
	}

	@Override
	public Iterator<E> iterator() {
		return list.iterator();
	}

	@Override
	public <T> T[] toArray(T[] a) {
		return list.toArray(a);
	}

	@Override
	public boolean remove(Object o) {
		return list.remove(o);
	}

	@Override
	public void clear() {
		list.clear();
	}
	
	@Override
	public E remove() {
		return list.remove();
	}

	@Override
	public E poll() {
		return list.poll();
	}

	@Override
	public E element() {
		return list.element();
	}

	@Override
	public E peek() {
		throw new UnsupportedOperationException();
	}


	@Override
	public boolean isEmpty() {
		throw new UnsupportedOperationException();
	}


	@Override
	public Object[] toArray() {
		throw new UnsupportedOperationException();
	}


	@Override
	public boolean containsAll(Collection<?> c) {
		throw new UnsupportedOperationException();
	}


	@Override
	public boolean addAll(Collection<? extends E> c) {
		throw new UnsupportedOperationException();
	}


	@Override
	public boolean removeAll(Collection<?> c) {
		throw new UnsupportedOperationException();
	}


	@Override
	public boolean retainAll(Collection<?> c) {
		throw new UnsupportedOperationException();
	}


	@Override
	public boolean offer(E e) {
		throw new UnsupportedOperationException();
	}
}
