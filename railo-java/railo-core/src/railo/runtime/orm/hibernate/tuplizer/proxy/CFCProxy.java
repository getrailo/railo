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
package railo.runtime.orm.hibernate.tuplizer.proxy;

import railo.runtime.Component;
import railo.runtime.ComponentPro;

public class CFCProxy extends ComponentProProxy {
	
	private ComponentPro cfc;

	public CFCProxy(Component cfc){
		this.cfc=(ComponentPro)cfc; 
	}
	
	@Override
	public Component getComponent() {
		return cfc;
	}
	
	@Override
	public ComponentPro getComponentPro() {
		return cfc;
	}
	
	public Object put(Object key, Object value) {
		super.put(key,value); // writes to this scope
		return getComponentScope().put(key, value); // writes to variables scope
	}

}
