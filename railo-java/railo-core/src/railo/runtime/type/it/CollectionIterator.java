package railo.runtime.type.it;

import java.util.Iterator;

import railo.runtime.type.Collection;
import railo.runtime.type.Collection.Key;

public class CollectionIterator implements Iterator<Object> {

	private Iterator<Collection.Key> keys;
	private Collection coll;

	public CollectionIterator(Key[] keys, Collection coll) {
		this.keys=new KeyIterator(keys);
		this.coll=coll;
	}
	public CollectionIterator(Iterator<Collection.Key> keys, Collection coll) {
		this.keys=keys;
		this.coll=coll;
	}

	public boolean hasNext() {
		return keys.hasNext();
	}

	@Override
	public Object next() {
		return coll.get(keys.next(),null);
	}

	public void remove() {
		throw new UnsupportedOperationException("this operation is not suppored");
	}

}
