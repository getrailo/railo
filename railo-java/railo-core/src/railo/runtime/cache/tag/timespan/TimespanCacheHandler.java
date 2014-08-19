package railo.runtime.cache.tag.timespan;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import railo.print;
import railo.commons.io.cache.Cache;
import railo.commons.io.cache.CacheEntry;
import railo.runtime.PageContext;
import railo.runtime.cache.ram.RamCache;
import railo.runtime.cache.tag.CacheHandler;
import railo.runtime.cache.tag.CacheHandlerFactory;
import railo.runtime.cache.tag.CacheHandlerFilter;
import railo.runtime.cache.tag.CacheItem;
import railo.runtime.cache.tag.query.QueryCacheItem;
import railo.runtime.cache.util.CacheKeyFilterAll;
import railo.runtime.exp.PageException;
import railo.runtime.functions.cache.Util;
import railo.runtime.op.Caster;
import railo.runtime.op.Decision;
import railo.runtime.type.dt.TimeSpan;

public class TimespanCacheHandler implements CacheHandler {

	//private final RamCache DEFAULT_CACHE=new RamCache();
	private int defaultCacheType;
	private Cache defaultCache;
	
	public TimespanCacheHandler(int defaultCacheType, Cache defaultCache){
		this.defaultCacheType=defaultCacheType; // 
		this.defaultCache=defaultCache; // new RamCache();
	}

	@Override
	public CacheItem get(PageContext pc, String id) {
		return CacheHandlerFactory.toCacheItem(getCache(pc).getValue(id,null),null);
	}
	
	@Override
	public boolean remove(PageContext pc, String id) {
		try {
			return getCache(pc).remove(id);
		}
		catch (IOException e) {}
		return false;
	}
	

	@Override
	public void set(PageContext pc, String id, Object cachedWithin, CacheItem value) throws PageException {
		long timeSpan;
		if(Decision.isDate(cachedWithin, false) && !(cachedWithin instanceof TimeSpan))
			timeSpan=Caster.toDate(cachedWithin, null).getTime()-System.currentTimeMillis();
		else
			timeSpan = Caster.toTimespan(cachedWithin).getMillis();
		
		// ignore timespan smaller or equal to 0
		if(timeSpan<=0) return;
		
		getCache(pc).put(id, value, Long.valueOf(timeSpan), Long.valueOf(timeSpan));
	}
	
	@Override
	public void clean(PageContext pc) {
		try{
		Cache c = getCache(pc);
		List<CacheEntry> entries = c.entries();
		if(entries.size()<100) return;
		
		Iterator<CacheEntry> it = entries.iterator();
		while(it.hasNext()){
			it.next(); // touch them to makes sure the cache remove them, not really good, cache must do this by itself
		}
		}
		catch(IOException ioe){}
	}
	

	@Override
	public void clear(PageContext pc) {
		try {
			getCache(pc).remove(CacheKeyFilterAll.getInstance());
		}
		catch (IOException e) {}
	}
	

	@Override
	public void clear(PageContext pc, CacheHandlerFilter filter) {
		try{
			Cache cache = getCache(pc);
			Iterator<CacheEntry> it = cache.entries().iterator();
			CacheEntry ce;
			Object obj;
			while(it.hasNext()){
				ce = it.next();
				if(filter==null) {
					cache.remove(ce.getKey());
					continue;
				}
				
				obj=ce.getValue();
				if(obj instanceof QueryCacheItem)
					obj=((QueryCacheItem)obj).getQuery();
				if(filter.accept(obj)) 
					cache.remove(ce.getKey());
			}
		}
		catch (IOException e) {}
	}

	@Override
	public int size(PageContext pc) {
		try {
			return getCache(pc).keys().size();
		}
		catch (IOException e) {
			return 0;
		}
	}
	

	private Cache getCache(PageContext pc) {
		Cache c = Util.getDefault(pc,defaultCacheType,null);
		if(c==null) {
			if(defaultCache==null)defaultCache=new RamCache();
			return defaultCache;
		}
		return c;
	}

	@Override
	public String label() throws PageException {
		return "timespan";
	}

}
