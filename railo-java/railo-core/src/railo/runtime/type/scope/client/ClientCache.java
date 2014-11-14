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
import railo.runtime.exp.PageException;
import railo.runtime.type.Collection;
import railo.runtime.type.Struct;
import railo.runtime.type.StructImpl;
import railo.runtime.type.scope.Client;
import railo.runtime.type.scope.storage.StorageScopeCache;

public final class ClientCache extends StorageScopeCache implements Client {
	
	private static final long serialVersionUID = -875719423763891692L;
	
	private ClientCache(PageContext pc,String cacheName, String appName,Struct sct) { 
		super(pc,cacheName,appName,"client",SCOPE_CLIENT,sct);
	}

	/**
	 * Constructor of the class, clone existing
	 * @param other
	 */
	private ClientCache(StorageScopeCache other,boolean deepCopy) {
		super(other,deepCopy);
	}
	

	
	@Override
	public Collection duplicate(boolean deepCopy) {
    	return new ClientCache(this,deepCopy);
	}
	
	/**
	 * load an new instance of the client datasource scope
	 * @param cacheName 
	 * @param appName
	 * @param pc
	 * @param log 
	 * @return client datasource scope
	 * @throws PageException
	 */
	public static Client getInstance(String cacheName, String appName, PageContext pc, Log log) throws PageException {
			Struct _sct = _loadData(pc, cacheName, appName,"client",log);
			//structOk=true;
			if(_sct==null) _sct=new StructImpl();
			
		return new ClientCache(pc,cacheName,appName,_sct);
	}
	

	public static Client getInstance(String cacheName, String appName, PageContext pc, Log log,Client defaultValue) {
		try {
			return getInstance(cacheName, appName, pc,log);
		}
		catch (PageException e) {}
		return defaultValue;
	}

}
