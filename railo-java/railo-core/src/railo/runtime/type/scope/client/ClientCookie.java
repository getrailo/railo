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
import railo.commons.lang.StringUtil;
import railo.runtime.PageContext;
import railo.runtime.type.Collection;
import railo.runtime.type.Struct;
import railo.runtime.type.scope.Client;
import railo.runtime.type.scope.storage.StorageScopeCookie;

public final class ClientCookie extends StorageScopeCookie implements Client {

	private static final long serialVersionUID = 4203695198240254464L;
	private static final String TYPE = "CLIENT";

	private ClientCookie(PageContext pc,String cookieName,Struct sct) {
		super(pc,cookieName,"client",SCOPE_CLIENT,sct);
	}

	/**
	 * Constructor of the class, clone existing
	 * @param other
	 */
	private ClientCookie(ClientCookie other,boolean deepCopy) {
		super(other,deepCopy);
	}
	
	
	@Override
	public Collection duplicate(boolean deepCopy) {
    	return new ClientCookie(this,deepCopy);
	}
	
	
	
	/**
	 * load new instance of the class
	 * @param name
	 * @param pc
	 * @param log 
	 * @return
	 */
	public static Client getInstance(String name, PageContext pc, Log log) {
		if(!StringUtil.isEmpty(name))
			name=StringUtil.toUpperCase(StringUtil.toVariableName(name));
		String cookieName="CF_"+TYPE+"_"+name;
		return new ClientCookie(pc,cookieName, _loadData(pc,cookieName,SCOPE_CLIENT,"client",log));
	}
}
