package railo.runtime.functions.cache;

import java.util.Iterator;
import java.util.List;

import railo.commons.io.cache.Cache;
import railo.runtime.PageContext;
import railo.runtime.cache.util.WildCardFilter;
import railo.runtime.config.ConfigImpl;
import railo.runtime.exp.PageException;
import railo.runtime.ext.function.Function;
import railo.runtime.op.Caster;
import railo.runtime.type.Array;
import railo.runtime.type.ArrayImpl;

/**
 * 
 */
public final class CacheGetAllIds implements Function {
	
	private static final long serialVersionUID = 4831944874663718056L;


	public static Array call(PageContext pc) throws PageException {
		return call(pc, null,null);
	}
	

	public static Array call(PageContext pc, String filter) throws PageException {
		return call(pc, filter, null);
	}
	
	public static Array call(PageContext pc, String filter, String cacheName) throws PageException {
		try {
			Cache cache = Util.getCache(pc,cacheName,ConfigImpl.CACHE_DEFAULT_OBJECT);
			
			List<String> keys = isFilter(filter)?cache.keys(new WildCardFilter(filter,true)):cache.keys();
			Iterator<String> it = keys.iterator();
			Array arr = new ArrayImpl();
			while(it.hasNext()){
				arr.append(it.next());
			}
			return arr;
		} catch (Exception e) {
			throw Caster.toPageException(e);
		}
	}


	protected static boolean isFilter(String filter) {
		return filter!=null && filter.length()>0 && !filter.equals("*");
	}
}