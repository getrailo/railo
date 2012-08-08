package railo.runtime.type.it;

import java.util.Iterator;

import railo.runtime.type.Collection;

public class KeyAsStringIterator  implements Iterator<String> {

	private Iterator<Collection.Key> it;

	public KeyAsStringIterator(Iterator<Collection.Key> it){
		this.it=it;
	}

	@Override
	public boolean hasNext() {
		return it.hasNext();
	}

	@Override
	public String next() {
		return it.next().getString();
	}

	@Override
	public void remove() {
		it.remove();
	}

}
