package railo.runtime.cache.tag.timespan;

import java.io.IOException;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import railo.commons.io.cache.Cache;
import railo.commons.io.cache.CacheEntry;
import railo.runtime.PageContext;
import railo.runtime.cache.ram.RamCache;
import railo.runtime.cache.tag.CacheHandler;
import railo.runtime.cache.tag.CacheIdentifier;
import railo.runtime.cache.util.CacheKeyFilterAll;
import railo.runtime.config.ConfigImpl;
import railo.runtime.db.SQL;
import railo.runtime.functions.cache.Util;
import railo.runtime.query.QueryCacheEntry;

public class TimespanCacheHandler implements CacheHandler {

	//private final RamCache DEFAULT_CACHE=new RamCache();
	private int defaultCacheType;
	private Cache defaultCache;
	
	public TimespanCacheHandler(int defaultCacheType, Cache defaultCache){
		this.defaultCacheType=defaultCacheType; // 
		this.defaultCache=defaultCache; // new RamCache();
	}

	@Override
	public Object get(PageContext pc, CacheIdentifier id) {
		Object obj= getCache(pc).getValue(id.id(),null);
		if(obj instanceof TimeSpanCacheToken) {
			TimeSpanCacheToken token=(TimeSpanCacheToken) obj;
			return token.value;
		}
		return null;
	}
	
	@Override
	public boolean remove(PageContext pc, CacheIdentifier id) {
		try {
			return getCache(pc).remove(id.id());
		}
		catch (IOException e) {}
		return false;
	}
	

	@Override
	public void set(PageContext pc, CacheIdentifier id, Object value) throws IOException {
		if(!(id instanceof TimespanCacheIdentifier)) 
			throw new IOException("given token cannot be used for this handler");
		TimespanCacheIdentifier _id=(TimespanCacheIdentifier)id;
		
		long timeSpan = _id.getTimeSpan();// ((cacheBefore.getTime()-System.currentTimeMillis())+1);
		getCache(pc).put(_id.id(), new TimeSpanCacheToken(value), Long.valueOf(timeSpan), Long.valueOf(timeSpan));
	}
	
	@Override
	public void clean(PageContext pc) throws IOException {
		
		Cache c = getCache(pc);
		List<CacheEntry> entries = c.entries();
		if(entries.size()<100) return;
		
		Iterator<CacheEntry> it = entries.iterator();
		while(it.hasNext()){
			it.next(); // touch them to makes sure the cache remove them, not really good, cache must do this by itself
		}
	}
	

	@Override
	public void clear(PageContext pc) throws IOException {
		getCache(pc).remove(CacheKeyFilterAll.getInstance());
	}

	@Override
	public int size(PageContext pc) throws IOException {
		return getCache(pc).keys().size();
	}
	

	private Cache getCache(PageContext pc) {
		Cache c = Util.getDefault(pc,defaultCacheType,null);
		if(c==null) {
			if(defaultCache==null)defaultCache=new RamCache();
			return defaultCache;
		}
		return c;
	}

}
