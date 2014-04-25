package railo.runtime.type.it;

import java.util.Iterator;

public class UCKeyIterator implements Iterator {

	
	
	private Iterator it;

	public UCKeyIterator(Iterator it) {
		this.it=it;
	}

	@Override
	public boolean hasNext() {
		return it.hasNext();
	}

	@Override
	public Object next() {
		return nextString();
	}
	
	public String nextString() {
		return it.next().toString().toUpperCase();
	}

	@Override
	public void remove() {
		it.remove();
	}

}
