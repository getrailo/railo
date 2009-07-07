package railo.runtime.type.it;

import java.util.Iterator;

import railo.runtime.engine.ThreadLocalPageContext;
import railo.runtime.type.KeyImpl;
import railo.runtime.type.Objects;
import railo.runtime.type.Collection.Key;

public class ObjectsIterator implements Iterator {

	private Iterator keys;
	private Objects objs;

	public ObjectsIterator(Key[] keys, Objects objs) {
		this.keys=new KeyIterator(keys);
		this.objs=objs;
	}
	public ObjectsIterator(Iterator keys, Objects objs) {
		this.keys=keys;
		this.objs=objs;
	}

	public boolean hasNext() {
		return keys.hasNext();
	}

	/**
	 * @see java.util.Iterator#next()
	 */
	public Object next() {
		return objs.get(ThreadLocalPageContext.get(),KeyImpl.toKey(keys.next(),null),null);
	}

	public void remove() {
		throw new UnsupportedOperationException("this operation is not suppored");
	}

}
