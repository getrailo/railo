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