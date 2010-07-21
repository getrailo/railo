package railo.commons.collections;

import java.util.Map;

import railo.runtime.type.Collection;
import railo.runtime.type.KeyImpl;

public final class BinaryTreeMap {

	private final BinEntry root=new BinEntry(new DummyKey(),null);
	
	public Object get(String key) {
		return get(KeyImpl.init(key));
	}
	
	public Object get(Collection.Key key) {
		BinEntry e = root;
		int outer=key.getId(),inner;
		while(true) {
			if(e==null) return null;
			if((inner=e.key.getId())==outer) return e.value;
			if(inner>outer) e=e.left;
			else e=e.right;
		}	
	}

	public Object put(String key, Object value) {
		return put(KeyImpl.init(key), value);
	}
	
	public Object put(Collection.Key key, Object value) {
		BinEntry e = root;
		int outer=key.getId(),inner;
		while(true) {
			if((inner=e.key.getId())==outer) {
				return e.value=value;
			}
			if(inner>outer) {
				if(e.left==null) {
					e.left=new BinEntry(key,value);
					return value;
				}
				e=e.left;
			}
			else {
				if(e.right==null) {
					e.right=new BinEntry(key,value);
					return value;
				}
				e=e.right;
			}
		}
	}
	
	class BinEntry implements Map.Entry {

		private Collection.Key key;
		private Object value;
		private BinEntry left;
		private BinEntry right;

		private BinEntry(Collection.Key key, Object value) {
			this.key=key;
			this.value=value;
		}

		/**
		 * @see java.util.Map$Entry#getKey()
		 */
		public Object getKey() {
			return key.getString();
		}

		/**
		 * @see java.util.Map$Entry#getString()
		 */
		public Object getValue() {
			return value;
		}

		/**
		 * @see java.util.Map$Entry#setValue(V)
		 */
		public Object setValue(Object value) {
			Object old=value;
			this.value=value;
			return old;
		}
		
		/**
		 * @see java.lang.Object#toString()
		 */
		public String toString() {
			return key+"="+value;
		}
		
	}
	
	class DummyKey implements Collection.Key {

		public char charAt(int index) {return 0;}
		public boolean equalsIgnoreCase(Collection.Key obj) {return false;}
		public int getId() {return 0;}
		public String getLowerString() {return null;}
		public String getString() {return null;}
		public char lowerCharAt(int index) {return 0;}
		public String getUpperString() {return null;}
		public char upperCharAt(int index) {return 0;}
		
	}
}
