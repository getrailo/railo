package railo.runtime.type.it;

import java.util.Enumeration;
import java.util.Iterator;

import railo.runtime.type.Collection;
import railo.runtime.type.Collection.Key;

public class ValueIterator implements Iterator<Object>, Enumeration<Object> {
	
	

	private Collection coll;
	protected Key[] keys;
	protected int pos;

	public ValueIterator(Collection coll, Collection.Key[] keys){
		this.coll=coll;
		this.keys=keys;
	}
	
	@Override
	public boolean hasNext() {
		return (keys.length)>pos;
	}

	@Override
	public Object next() {
		Key key = keys[pos++];
		if(key==null) return null;
		return coll.get(key,null);
	}

	@Override
	public boolean hasMoreElements() {
		return hasNext();
	}

	@Override
	public Object nextElement() {
		return next();
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException("this operation is not suppored");
	}
}
