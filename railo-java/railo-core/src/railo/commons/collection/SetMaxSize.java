package railo.commons.collection;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

public class SetMaxSize<E> implements Set<E> {
	
	private int maxSize;
	
	private final LinkedHashMapMaxSize<E, String> map;

	public SetMaxSize(int maxSize){
		this(maxSize,new LinkedHashMapMaxSize<E, String>(maxSize));
	}
	private SetMaxSize(int maxSize, LinkedHashMapMaxSize<E, String> map){
		this.maxSize=maxSize;
		this.map=map;
	}

	@Override
	public Iterator<E> iterator() {
		return map.keySet().iterator();
	}

	@Override
	public int size() {
		return map.size();
	}

	@Override
	public boolean isEmpty() {
		return map.isEmpty();
	}

	@Override
	public boolean contains(Object o) {
		return map.containsKey(o);
	}

	@Override
	public boolean add(E e) {
		map.put(e,"");
		return true;
	}

	@Override
	public boolean remove(Object o) {
		return map.remove(o)!=null;
	}

	@Override
	public void clear() {
		map.clear();
	}

	@Override
	public Object clone() {
		return new SetMaxSize<E>(maxSize,(LinkedHashMapMaxSize<E, String>)map.clone());
	}

	@Override
	public boolean equals(Object o) {
		if(!(o instanceof SetMaxSize)) return false;
		SetMaxSize other=(SetMaxSize) o;
		return ((SetMaxSize)o).map.equals(map);
	}

	@Override
	public int hashCode() {
		return map.hashCode();
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Object[] toArray() {
		return map.keySet().toArray();
	}

	@Override
	public <T> T[] toArray(T[] a) {
		return map.keySet().toArray(a);
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
	public boolean retainAll(Collection<?> c) {
		throw new UnsupportedOperationException();
	}

	@Override
	public String toString() {
		return map.keySet().toString();
	}
}
