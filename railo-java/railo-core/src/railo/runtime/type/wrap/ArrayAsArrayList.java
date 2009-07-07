package railo.runtime.type.wrap;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import railo.runtime.exp.PageException;
import railo.runtime.exp.PageRuntimeException;
import railo.runtime.type.Array;

public class ArrayAsArrayList extends ArrayList {

	Array array;
	
	private ArrayAsArrayList(Array array) {
		this.array=array;
	}
	
	public static ArrayList toArrayList(Array array) {
		return new ArrayAsArrayList(array);
	}
	
	
	/**
	 * @see java.util.List#addEntry(E)
	 */
	public boolean add(Object o) {
		try {
			array.append(o);
		} 
		catch (PageException e) {
			return false;
		}
		return true;
	}

	public void add(int index, Object element) {
		try {
			array.insert(index+1, element);
		} catch (PageException e) {
			throw new IndexOutOfBoundsException(e.getMessage());
		}
	}

	/**
	 * @see java.util.List#addAll(java.util.Collection)
	 */
	public boolean addAll(Collection c) {
		Iterator it = c.iterator();
		while(it.hasNext()) {
			add(it.next());
		}
		return !c.isEmpty();
	}

	/**
	 * @see java.util.List#addAll(int, java.util.Collection)
	 */
	public boolean addAll(int index, Collection c) {
		Iterator it = c.iterator();
		while(it.hasNext()) {
			add(index++,it.next());
		}
		return !c.isEmpty();
	}

	/**
	 * @see java.util.List#clear()
	 */
	public void clear() {
		array.clear();
	}

	/**
	 * @see java.util.List#contains(java.lang.Object)
	 */
	public boolean contains(Object o) {
		return indexOf(o)!=-1;
	}

	/**
	 * @see java.util.List#containsAll(java.util.Collection)
	 */
	public boolean containsAll(Collection c) {
		Iterator it = c.iterator();
		while(it.hasNext()) {
			if(!contains(it.next()))return false;
		}
		return true;
	}

	/**
	 * @see java.util.List#get(int)
	 */
	public Object get(int index) {
		try {
			return array.getE(index+1);
		} catch (PageException e) {
			throw new IndexOutOfBoundsException(e.getMessage());
		}
	}

	public int indexOf(Object o) {
		Iterator it=array.iterator();
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
	public boolean isEmpty() {
		return array.size()==0;
	}

	/**
	 * @see java.util.List#iterator()
	 */
	public Iterator iterator() {
		return array.iterator();
	}

	public int lastIndexOf(Object o) {
		Iterator it=array.iterator();
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
	public ListIterator listIterator() {
		return listIterator(0);
	}

	/**
	 * @see java.util.List#listIterator(int)
	 */
	public ListIterator listIterator(int index) {
		return array.toList().listIterator(index);
	}
	

	/**
	 * @see java.util.List#remove(java.lang.Object)
	 */
	public boolean remove(Object o) {
		int index = indexOf(o);
		if(index==-1) return false;
		
		try {
			array.removeE(index+1);
		} catch (PageException e) {
			return false;
		}
		return true;
	}

	public Object remove(int index) {
		try {
			return array.removeE(index+1);
		} catch (PageException e) {
			throw new IndexOutOfBoundsException(e.getMessage());
		}
	}

	public boolean removeAll(Collection c) {
		Iterator it = c.iterator();
		boolean rtn=false;
		while(it.hasNext()) {
			if(remove(it.next()))rtn=true;
		}
		return rtn;
	}

	public boolean retainAll(Collection c) {new ArrayList().retainAll(c);
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

	public Object set(int index, Object element) {
		try {
			if(!array.containsKey(index+1)) throw new IndexOutOfBoundsException("Index: "+(index+1)+", Size: "+size());
			return array.setE(index+1,element);
		} catch (PageException e) {
			throw new PageRuntimeException(e);
		}
	}

	/**
	 * @see java.util.List#size()
	 */
	public int size() {
		return array.size();
	}

	/**
	 * @see java.util.List#subList(int, int)
	 */
	public List subList(int fromIndex, int toIndex) {
		return array.toList().subList(fromIndex, toIndex);
	}

	/**
	 * @see java.util.List#toArray()
	 */
	public Object[] toArray() {
		return array.toArray();
	}

	/**
	 * @see java.util.List#toArray(T[])
	 */
	public Object[] toArray(Object[] a) {
		return array.toArray();
	}

	/**
	 *
	 * @see java.util.ArrayList#clone()
	 */
	public Object clone() {
		return toArrayList((Array) array.duplicate(true));
	}

	/**
	 *
	 * @see java.util.ArrayList#ensureCapacity(int)
	 */
	public void ensureCapacity(int minCapacity) {
		throw new PageRuntimeException("not supported");
	}

	/**
	 *
	 * @see java.util.ArrayList#trimToSize()
	 */
	public void trimToSize() {
		throw new PageRuntimeException("not supported");
	}
	
	/*public static void main(String[] args) throws PageException {
		test(new ArrayList());
		test(new ArrayAsList(new ArrayImpl()));
	}

	private static void test(List list) throws PageException {
		print.out("***************************");
		print.out(list.getClass().getName());
		list.add("1");
		list.add("2");
		list.add(0,"a");
		list.add(1,"b");
		list.add(4,"3");
		List l1=new ArrayList();
		l1.add("4");
		l1.add("5");
		list.addAll(l1);
		List l2=new ArrayList();
		l2.add("6");
		l2.add("7");

		list.addAll(2,l2);
		list.addAll(9,l2);
		
		print.out(railo.runtime.type.List.arrayToList(Caster.toArray(list), ","));
		print.out(list.contains("a"));
		print.out(list.contains("z"));
		print.out(list.containsAll(l2));
		l2.add("z");
		print.out(list.containsAll(l2));
		print.out(list.get(3));
		print.out(list.indexOf("7"));
		print.out(list.lastIndexOf("7"));
		print.out(list.indexOf("z"));
		print.out(list.remove("7"));
		print.out(railo.runtime.type.List.arrayToList(Caster.toArray(list), ","));
		print.out(list.remove(1));
		List l3=new ArrayList();
		l3.add("y");
		print.out(list.removeAll(l3));
		l3.add("a");
		print.out(list.removeAll(l3));
		list.set(7, "aa");
		print.out(railo.runtime.type.List.arrayToList(Caster.toArray(list), ","));
		
	}*/

}
