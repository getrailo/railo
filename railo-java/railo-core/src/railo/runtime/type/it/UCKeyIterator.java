package railo.runtime.type.it;

import java.util.Iterator;

public class UCKeyIterator implements Iterator {

	
	
	private Iterator it;

	public UCKeyIterator(Iterator it) {
		this.it=it;
	}

	public boolean hasNext() {
		return it.hasNext();
	}

	public Object next() {
		return nextString();
	}
	
	public String nextString() {
		return it.next().toString().toUpperCase();
	}

	public void remove() {
		it.remove();
	}

}
