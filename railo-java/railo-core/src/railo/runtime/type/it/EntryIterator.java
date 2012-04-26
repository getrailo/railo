package railo.runtime.type.it;

import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map.Entry;

import railo.runtime.type.Collection;
import railo.runtime.type.Collection.Key;

public class EntryIterator implements Iterator<Entry<Key, Object>>, Enumeration<Entry<Key, Object>> {
	
	

	private Collection coll;
	protected Key[] keys;
	protected int pos;

	public EntryIterator(Collection coll, Collection.Key[] keys){
		this.coll=coll;
		this.keys=keys;
	}
	
	@Override
	public boolean hasNext() {
		return (keys.length)>pos;
	}

	@Override
	public Entry<Key, Object> next() {
		Key key = keys[pos++];
		if(key==null) return null;
		return new EntryImpl(coll,key);
	}

	@Override
	public boolean hasMoreElements() {
		return hasNext();
	}

	@Override
	public Entry<Key, Object> nextElement() {
		return next();
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException("this operation is not suppored");
	}

	
	public class EntryImpl implements Entry<Key, Object> {

		private Collection coll;
		protected Key key;

		public EntryImpl(Collection coll, Key key) {
			this.coll=coll;
			this.key=key;
		}

		@Override
		public Key getKey() {
			return key;
		}

		@Override
		public Object getValue() {
			return coll.get(key,null);
		}

		@Override
		public Object setValue(Object value) {
			return coll.setEL(key, value);
		}

	}
}
