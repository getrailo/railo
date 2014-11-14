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
import java.util.Iterator;

import railo.commons.io.cache.Cache;
import railo.runtime.PageContext;
import railo.runtime.config.ConfigImpl;
import railo.runtime.exp.ApplicationException;
import railo.runtime.exp.PageException;
import railo.runtime.ext.function.Function;
import railo.runtime.op.Caster;
import railo.runtime.op.Decision;
import railo.runtime.type.Array;
import railo.runtime.type.util.ListUtil;

/**
 * 
 */
public final class CacheRemove implements Function {
	
	private static final long serialVersionUID = -5823359978885018762L;
	
	public static String call(PageContext pc, Object ids) throws PageException {
		return call(pc, ids, false,null);
	}
	public static String call(PageContext pc, Object ids, boolean throwOnError) throws PageException {
		return call(pc, ids, throwOnError, null);
	}
	
	
	public static String call(PageContext pc, Object ids, boolean throwOnError, String cacheName) throws PageException {
		Array arr = toArray(ids);//
		Iterator it = arr.valueIterator();
		String id;
		Cache cache;
		try {
			cache = Util.getCache(pc,cacheName,ConfigImpl.CACHE_DEFAULT_OBJECT);
		} catch (IOException e) {
			throw Caster.toPageException(e);
		}
		StringBuilder sb=null;
		try{
			while(it.hasNext()){
				id= Util.key(Caster.toString(it.next()));
				if(!cache.remove(id) && throwOnError){
					if(sb==null)sb=new StringBuilder();
					else sb.append(',');
					sb.append(id);
				}		
			}
		} 
		catch (IOException e) {}
		if(throwOnError && sb!=null)
			throw new ApplicationException("can not remove the elements with the following id(s) ["+sb+"]");
		return null;
	}
	private static Array toArray(Object oIds) throws PageException {
		if(Decision.isArray(oIds)){
			return Caster.toArray(oIds);
		}
		return ListUtil.listToArray(Caster.toString(oIds), ',');
	}
	
}