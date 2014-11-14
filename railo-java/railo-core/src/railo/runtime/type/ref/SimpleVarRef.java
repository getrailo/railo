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
package railo.runtime.type.ref;

import railo.runtime.PageContext;
import railo.runtime.PageContextImpl;
import railo.runtime.exp.PageException;
import railo.runtime.type.Collection;

public class SimpleVarRef implements Reference {

	//private PageContextImpl pc;

	public SimpleVarRef(PageContextImpl pc, String key) {
		//this.pc=pc;
	}
	
	public Object get(PageContext pc) throws PageException {
		// TODO Auto-generated method stub
		return null;
	}

	public Object get(PageContext pc, Object defaultValue) {
		// TODO Auto-generated method stub
		return null;
	}

	public Collection.Key getKey() throws PageException {
		// TODO Auto-generated method stub
		return null;
	}
	public String getKeyAsString() throws PageException {
		// TODO Auto-generated method stub
		return null;
	}

	public Object getParent() {
		// TODO Auto-generated method stub
		return null;
	}

	public Object remove(PageContext pc) throws PageException {
		// TODO Auto-generated method stub
		return null;
	}

	public Object removeEL(PageContext pc) {
		// TODO Auto-generated method stub
		return null;
	}

	public Object set(PageContext pc, Object value) throws PageException {
		// TODO Auto-generated method stub
		return null;
	}

	public Object setEL(PageContext pc, Object value) {
		// TODO Auto-generated method stub
		return null;
	}

	public Object touch(PageContext pc) throws PageException {
		// TODO Auto-generated method stub
		return null;
	}

	public Object touchEL(PageContext pc) {
		// TODO Auto-generated method stub
		return null;
	}

}
