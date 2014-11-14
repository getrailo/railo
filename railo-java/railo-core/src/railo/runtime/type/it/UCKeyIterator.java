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

public class UCKeyIterator implements Iterator {

	
	
	private Iterator it;

	public UCKeyIterator(Iterator it) {
		this.it=it;
	}

	public boolean hasNext() {
		return it.hasNext();
	}

	public Object next() {
		return nextString();
	}
	
	public String nextString() {
		return it.next().toString().toUpperCase();
	}

	public void remove() {
		it.remove();
	}

}
