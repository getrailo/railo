package railo.runtime.type.it;

import java.util.Iterator;
import java.util.Map.Entry;

import railo.runtime.engine.ThreadLocalPageContext;
import railo.runtime.type.Collection.Key;
import railo.runtime.type.KeyImpl;
import railo.runtime.type.Objects;

public class ObjectsEntryIterator implements Iterator<Entry<Key, Object>> {

	private Iterator<Key> keys;
	private Objects objs;

	public ObjectsEntryIterator(Key[] keys, Objects objs) {
		this.keys=new KeyIterator(keys);
		this.objs=objs;
	}
	public ObjectsEntryIterator(Iterator<Key> keys, Objects objs) {
		this.keys=keys;
		this.objs=objs;
	}

	public boolean hasNext() {
		return keys.hasNext();
	}

	@Override
	public Entry<Key, Object> next() {
		Key key = KeyImpl.toKey(keys.next(),null);
		return new EntryImpl(objs,key);
	}

	public void remove() {
		throw new UnsupportedOperationException("this operation is not suppored");
	}

	public class EntryImpl implements Entry<Key, Object> {

		protected Key key;
		private Objects  objcts;

		public EntryImpl(Objects objcts,Key key) {
			this.key=key;
			this. objcts= objcts;
		}

		@Override
		public Key getKey() {
			return key;
		}

		@Override
		public Object getValue() {
			return objcts.get(ThreadLocalPageContext.get(),key,null);
		}

		@Override
		public Object setValue(Object value) {
			return objcts.setEL(ThreadLocalPageContext.get(),key,value);
		}

	}
}
