package railo.runtime.cache.tag.smart;

import railo.print;
import railo.commons.io.log.log4j.LogAdapter;
import railo.runtime.PageContext;
import railo.runtime.cache.tag.CacheHandler;
import railo.runtime.cache.tag.CacheHandlerFactory;
import railo.runtime.cache.tag.CacheHandlerFilter;
import railo.runtime.cache.tag.request.CacheEntry;
import railo.runtime.config.ConfigImpl;
import railo.runtime.exp.PageException;

public class SmartCacheHandler implements CacheHandler {

	private int cacheType; 

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
		
		print(pc,"SmartCacheHandler.set:"+id+":"+value);
		
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
		((ConfigImpl)pc.getConfig()).getLog("application").error(CacheHandlerFactory.toStringCacheName(cacheType, null),msg);
	}

}
