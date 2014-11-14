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
import railo.commons.io.res.Resource;
import railo.runtime.PageContext;
import railo.runtime.type.Collection;
import railo.runtime.type.Struct;
import railo.runtime.type.scope.Client;
import railo.runtime.type.scope.storage.StorageScopeFile;

public class ClientFile extends StorageScopeFile implements Client {

	
	/**
	 * Constructor of the class
	 * @param pc
	 * @param name
	 * @param sct
	 */
	private ClientFile(PageContext pc,Resource res,Struct sct) {
		super(pc,res,"client",SCOPE_CLIENT,sct);
	}
	

	/**
	 * Constructor of the class, clone existing
	 * @param other
	 */
	private ClientFile(ClientFile other,boolean deepCopy) {
		super(other,deepCopy);
	}

	/**
	 * load new instance of the class
	 * @param name
	 * @param pc
	 * @param log 
	 * @return
	 */
	public static Client getInstance(String name, PageContext pc, Log log) {

		Resource res=_loadResource(pc.getConfig(),SCOPE_CLIENT,name,pc.getCFID());
		Struct data=_loadData(pc,res,log);
		return new ClientFile(pc,res,data);
	}
	
	@Override
	public Collection duplicate(boolean deepCopy) {
    	return new ClientFile(this,deepCopy);
	}
}
