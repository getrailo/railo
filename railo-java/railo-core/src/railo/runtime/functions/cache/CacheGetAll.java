package railo.runtime.functions.cache;

import java.util.Iterator;
import java.util.List;

import railo.commons.io.cache.Cache;
import railo.commons.io.cache.CacheEntry;
import railo.runtime.PageContext;
import railo.runtime.cache.util.WildCardFilter;
import railo.runtime.config.ConfigImpl;
import railo.runtime.exp.PageException;
import railo.runtime.ext.function.Function;
import railo.runtime.op.Caster;
import railo.runtime.type.KeyImpl;
import railo.runtime.type.Struct;
import railo.runtime.type.StructImpl;

/**
 * 
 */
public final class CacheGetAll implements Function {
	
	private static final long serialVersionUID = 6395709569356486777L;

	public static Struct call(PageContext pc) throws PageException {
		return call(pc, null,null);
	}
	public static Struct call(PageContext pc,String filter) throws PageException {
		return call(pc, filter,null);
	}
	
	public static Struct call(PageContext pc,String filter, String cacheName) throws PageException {
		try {
			Cache cache = Util.getCache(pc,cacheName,ConfigImpl.CACHE_DEFAULT_OBJECT);
			List<CacheEntry> entries = CacheGetAllIds.isFilter(filter)?cache.entries(new WildCardFilter(filter,true)):cache.entries();
			Iterator<CacheEntry> it=entries.iterator();
			Struct sct = new StructImpl();
			CacheEntry entry;
			while(it.hasNext()){
				entry= it.next();
				sct.setEL(KeyImpl.getInstance(entry.getKey()),entry.getValue());
			}
			return sct;
		} catch (Exception e) {
			throw Caster.toPageException(e);
		}
	}
}