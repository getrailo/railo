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

package railo.runtime.functions.other;


import railo.runtime.PageContext;
import railo.runtime.config.ConfigImpl;
import railo.runtime.config.ConfigWeb;
import railo.runtime.config.ConfigWebImpl;
import railo.runtime.exp.PageException;
import railo.runtime.ext.function.Function;
import railo.runtime.op.Caster;
import railo.runtime.type.Collection;
import railo.runtime.type.KeyImpl;
import railo.runtime.type.Struct;
import railo.runtime.type.StructImpl;
import railo.runtime.type.util.KeyConstants;

/**
 * Implements the CFML Function createGuid
 */
public final class GetRailoId implements Function {

	private static final Collection.Key SECURITY_KEY = KeyImpl.intern("securityKey");
	private static final Collection.Key ID_PRO = KeyImpl.intern("idPro");

	public static Struct call(PageContext pc ) throws PageException {
		Struct sct=new StructImpl();
	    Struct web=new StructImpl();
	    Struct server=new StructImpl();
	    ConfigWeb config = pc.getConfig();
    	
		web.set(SECURITY_KEY, ((ConfigImpl)config).getSecurityKey());
		web.set(KeyConstants._id, config.getId());
		sct.set(KeyConstants._web, web);
    	
    	if(config instanceof ConfigWebImpl){
    		ConfigWebImpl cwi = (ConfigWebImpl)config;
    		server.set(SECURITY_KEY, cwi.getServerSecurityKey());
    		server.set(KeyConstants._id, cwi.getServerId());
    		server.set(ID_PRO, cwi.getServerIdPro());
    		sct.set(KeyConstants._server, server);
    	}
    	
    	sct.set(KeyConstants._request, Caster.toString(pc.getId()));
    	return  sct;
    }
    
}