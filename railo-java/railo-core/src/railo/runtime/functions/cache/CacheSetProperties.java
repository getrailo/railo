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
package railo.runtime.functions.cache;

import java.util.ArrayList;

import railo.commons.io.cache.exp.CacheException;
import railo.commons.lang.StringUtil;
import railo.runtime.PageContext;
import railo.runtime.cache.CacheConnection;
import railo.runtime.config.ConfigImpl;
import railo.runtime.exp.PageException;
import railo.runtime.exp.SecurityException;
import railo.runtime.op.Caster;
import railo.runtime.type.Collection.Key;
import railo.runtime.type.KeyImpl;
import railo.runtime.type.Struct;
import railo.runtime.type.util.ListUtil;

public class CacheSetProperties {

	private static final Key OBJECT_TYPE = KeyImpl.intern("objecttype");

	public static Object call(PageContext pc,Struct properties) throws PageException {
		try {
			Object obj=properties.removeEL(OBJECT_TYPE); 
			String objectType=Caster.toString(obj);
			
			CacheConnection[] conns=getCaches(pc,objectType);
			for(int i=0;i<conns.length;i++){
				setProperties(conns[i],properties);
			}
		} catch (CacheException e) {
			throw Caster.toPageException(e);
		}
		
		
		return call(pc, null);
	}

	private static void setProperties(CacheConnection cc, Struct properties) throws SecurityException {
		throw new SecurityException("it is not allowed to change cache connection setting this way, please use the tag cfadmin or the railo administrator frontend instead ");
	}

	private static CacheConnection[] getCaches(PageContext pc,String cacheName) throws CacheException {
		ConfigImpl config=(ConfigImpl) pc.getConfig();
		if(StringUtil.isEmpty(cacheName)){
			
			return new CacheConnection[]{
					config.getCacheDefaultConnection(ConfigImpl.CACHE_DEFAULT_OBJECT),
					config.getCacheDefaultConnection(ConfigImpl.CACHE_DEFAULT_TEMPLATE)
			}
			;
			// MUST which one is first
		}
		
		ArrayList<CacheConnection> list=new ArrayList<CacheConnection>();
		String name;
		String[] names=ListUtil.listToStringArray(cacheName, ',');
		for(int i=0;i<names.length;i++){
			name=names[i].trim().toLowerCase();
			if(name.equalsIgnoreCase("template"))
				list.add(config.getCacheDefaultConnection(ConfigImpl.CACHE_DEFAULT_TEMPLATE));
			else if(name.equalsIgnoreCase("object"))
				list.add(config.getCacheDefaultConnection(ConfigImpl.CACHE_DEFAULT_OBJECT));
			else{
				CacheConnection cc= config.getCacheConnections().get(name);
				if(cc==null) throw new CacheException("there is no cache defined with name ["+name+"]");
				list.add(cc);
			}
		}
		return list.toArray(new CacheConnection[list.size()]);
	}
}
