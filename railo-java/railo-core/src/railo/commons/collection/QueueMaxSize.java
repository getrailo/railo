/**
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either 
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public 
 * License along with this library.  If not, see <http://www.gnu.org/licenses/>.
 * 
 **/
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

	public int size() {
		return list.size();
	}

	public boolean contains(Object o) {
		return list.contains(o);
	}

	public Iterator<E> iterator() {
		return list.iterator();
	}

	public <T> T[] toArray(T[] a) {
		return list.toArray(a);
	}

	public boolean remove(Object o) {
		return list.remove(o);
	}

	public void clear() {
		list.clear();
	}
	
	public E remove() {
		return list.remove();
	}

	public E poll() {
		return list.poll();
	}

	public E element() {
		return list.element();
	}

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
