/**
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either 
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public 
 * License along with this library.  If not, see <http://www.gnu.org/licenses/>.
 * 
 **/
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
