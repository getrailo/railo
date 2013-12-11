package railo.runtime.cache.tag.smart;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

import railo.print;
import railo.commons.digest.HashUtil;
import railo.commons.io.SystemUtil;
import railo.commons.io.log.log4j.LogAdapter;
import railo.runtime.PageContext;
import railo.runtime.cache.tag.CacheHandler;
import railo.runtime.cache.tag.CacheHandlerFactory;
import railo.runtime.cache.tag.CacheHandlerFilter;
import railo.runtime.cache.tag.request.CacheEntry;
import railo.runtime.cache.tag.udf.UDFArgConverter;
import railo.runtime.config.ConfigImpl;
import railo.runtime.exp.PageException;
import railo.runtime.functions.other.CreateUUID;
import railo.runtime.functions.system.GetCurrentContext;
import railo.runtime.type.Query;

public class SmartCacheHandler implements CacheHandler {

	private int cacheType; 
	public static Map<String,SmartEntry> entries=new HashMap<String,SmartEntry>();
	

	public SmartCacheHandler(int cacheType) {
		this.cacheType=cacheType;
	}

	@Override
	public Object get(PageContext pc, String id) throws PageException {
		print(pc,"SmartCacheHandler.get:"+id);
		return null;
	}

	@Override
	public boolean remove(PageContext pc, String id) {
		print(pc,"SmartCacheHandler.remove:"+id);
		return false;
	}

	@Override
	public void set(PageContext pc, String id, Object cachedwithin, Object value) throws PageException {
		if(value instanceof CacheEntry) {
			CacheEntry ce=(CacheEntry) value;
			value=ce.query;
		}
		
		if(cacheType==ConfigImpl.CACHE_DEFAULT_QUERY) {
			if(value instanceof Query) setQuery(pc,id,(Query)value);
			// TODO handle storedproc
		}
		// TODO else handle all other types
	}

	private void setQuery(PageContext pc, String id, Query qry) {
		SmartEntry se = new QuerySmartEntry(pc,qry,id,cacheType);
		entries.put(se.getId(),se);
		
	}

	@Override
	public void clear(PageContext pc) {
		print(pc,"SmartCacheHandler.clear");
	}

	@Override
	public void clear(PageContext pc, CacheHandlerFilter filter) {
		print(pc,"SmartCacheHandler.clear:"+filter);
	}

	@Override
	public void clean(PageContext pc) {
		print(pc,"SmartCacheHandler.clean");
	}

	@Override
	public int size(PageContext pc) {
		print(pc,"SmartCacheHandler.size");
		return 0;
	}

	private void print(PageContext pc, String msg) {
		//print.e(CacheHandlerFactory.toStringCacheName(cacheType, null)+"->"+msg);
		((ConfigImpl)pc.getConfig()).getLog("application").error(CacheHandlerFactory.toStringCacheName(cacheType, null),msg);
	}

}
