package railo.runtime.type.it;

import java.util.Iterator;

import railo.runtime.engine.ThreadLocalPageContext;
import railo.runtime.type.Collection.Key;
import railo.runtime.type.KeyImpl;
import railo.runtime.type.Objects;

public class ObjectsIterator implements Iterator<Object> {

	private Iterator<Key> keys;
	private Objects objs;

	public ObjectsIterator(Key[] keys, Objects objs) {
		this.keys=new KeyIterator(keys);
		this.objs=objs;
	}
	public ObjectsIterator(Iterator<Key> keys, Objects objs) {
		this.keys=keys;
		this.objs=objs;
	}

	public boolean hasNext() {
		return keys.hasNext();
	}

	@Override
	public Object next() {
		return objs.get(ThreadLocalPageContext.get(),KeyImpl.toKey(keys.next(),null),null);
	}

	public void remove() {
		throw new UnsupportedOperationException("this operation is not suppored");
	}

}
