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

import railo.commons.io.cache.CacheKeyFilter;
import railo.runtime.PageContext;
import railo.runtime.cache.util.WildCardFilter;
import railo.runtime.config.ConfigImpl;
import railo.runtime.exp.PageException;
import railo.runtime.ext.function.Function;
import railo.runtime.op.Caster;

/**
 * 
 */
public final class CacheClear implements Function,CacheKeyFilter {
	
	private static CacheKeyFilter filter=new CacheClear();

	public static double call(PageContext pc) throws PageException {
		return call(pc,null,null);
		
	}
	public static double call(PageContext pc,String strFilter) throws PageException {
		return call(pc,strFilter,null);
		
	}
	public static double call(PageContext pc,String strFilter, String cacheName) throws PageException {
		try {
			CacheKeyFilter f=filter;
			if(CacheGetAllIds.isFilter(strFilter))
				f=new WildCardFilter(strFilter,true);
			return Util.getCache(pc,cacheName,ConfigImpl.CACHE_DEFAULT_OBJECT).remove(f);
		} catch (Exception e) {
			throw Caster.toPageException(e);
		}
	}

	public boolean accept(String key) {
		return true;
	}

	public String toPattern() {
		return "*";
	}
	
}