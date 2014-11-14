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

import java.io.IOException;

import railo.commons.io.cache.Cache;
import railo.runtime.PageContext;
import railo.runtime.config.ConfigImpl;
import railo.runtime.exp.ApplicationException;
import railo.runtime.exp.PageException;
import railo.runtime.ext.function.Function;
import railo.runtime.op.Caster;

/**
 * 
 */
public final class CacheDelete implements Function {
	
	private static final long serialVersionUID = 4148677299207997607L;

	public static String call(PageContext pc, String id) throws PageException {
		return call(pc, id, false,null);
	}
	public static String call(PageContext pc, String id, boolean throwOnError) throws PageException {
		return call(pc, id, throwOnError, null);
	}
	
	public static String call(PageContext pc, String id, boolean throwOnError, String cacheName) throws PageException {
		try {
			Cache cache = Util.getCache(pc,cacheName,ConfigImpl.CACHE_DEFAULT_OBJECT);
			if(!cache.remove(Util.key(id)) && throwOnError){
				throw new ApplicationException("can not remove the element with the following id ["+id+"]");
			}	
		} 
		catch (IOException e) {
			throw Caster.toPageException(e);
		}
		return null;
	}
	
}