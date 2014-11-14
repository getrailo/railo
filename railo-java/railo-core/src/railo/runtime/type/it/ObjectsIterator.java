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
