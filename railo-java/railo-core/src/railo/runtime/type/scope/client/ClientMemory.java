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
package railo.runtime.type.scope.client;

import railo.commons.io.log.Log;
import railo.runtime.PageContext;
import railo.runtime.type.Collection;
import railo.runtime.type.scope.Client;
import railo.runtime.type.scope.storage.MemoryScope;
import railo.runtime.type.scope.storage.StorageScopeMemory;

public class ClientMemory extends StorageScopeMemory implements Client,MemoryScope {

	private static final long serialVersionUID = 5032226519712666589L;


	/**
	 * Constructor of the class
	 * @param pc
	 * @param log 
	 * @param name
	 */
	private ClientMemory(PageContext pc, Log log) {
		super(pc,"client",SCOPE_CLIENT,log);
	}

	/**
	 * Constructor of the class, clone existing
	 * @param other
	 */
	private ClientMemory(ClientMemory other,boolean deepCopy) {
		super(other,deepCopy);
	}
	/**
	 * load a new instance of the class
	 * @param pc
	 * @param log 
	 * @return
	 */
	public static Client getInstance(PageContext pc, Log log) {
		return new ClientMemory(pc,log);
	}
	
	
	@Override
	public Collection duplicate(boolean deepCopy) {
    	return new ClientMemory(this,deepCopy);
	}
}
