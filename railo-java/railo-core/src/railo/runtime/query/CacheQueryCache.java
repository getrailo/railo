package railo.runtime.query;

import java.io.IOException;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import railo.commons.io.cache.Cache;
import railo.commons.io.cache.CacheEntry;
import railo.commons.lang.KeyGenerator;
import railo.runtime.PageContext;
import railo.runtime.cache.ram.RamCache;
import railo.runtime.cache.util.CacheKeyFilterAll;
import railo.runtime.config.ConfigImpl;
import railo.runtime.db.SQL;
import railo.runtime.functions.cache.Util;
import railo.runtime.type.Query;

 class CacheQueryCache extends QueryCacheSupport {
	
	private static final long serialVersionUID = -2321532150542424070L;
	
	private final RamCache DEFAULT_CACHE=new RamCache();

	public CacheQueryCache(){
	}

	
	@Override
	public void clear(PageContext pc) {
		try {
			getCache(pc).remove(CacheKeyFilterAll.getInstance());
		} catch (IOException e) {}
	}

	@Override
	public void clearUnused(PageContext pc) throws IOException {
		
		Cache c = getCache(pc);
		List<CacheEntry> entries = c.entries();
		if(entries.size()<100) return;
		
		Iterator<CacheEntry> it = entries.iterator();
		while(it.hasNext()){
			it.next(); // touch them to makes sure the cache remove them, not really good, cache must do this by itself
			/*qce=(QueryCacheEntry) entry.getValue();
			if(qce.isInCacheRange(null)) {
				print.o(entry.getKey());
				c.remove(entry.getKey());
			}*/
		}
	}

	@Override
	public Object get(PageContext pc,SQL sql, String datasource, String username,String password, Date cacheAfter) {
		String key=key(sql,datasource,username,password);
		Object obj= getCache(pc).getValue(key,null);
		if(obj instanceof QueryCacheEntry) {
			QueryCacheEntry entry=(QueryCacheEntry) obj;
			if(entry.isCachedAfter(cacheAfter)) {
		    	return entry.getValue();
		    }
		    //getCache().remove(key);
		}
		return null;
	}

	@Override
	public Query getQuery(PageContext pc,SQL sql, String datasource, String username,String password, Date cacheAfter) {
		Object o=get(pc,sql, datasource, username, password, cacheAfter);
		if(o instanceof Query) return (Query) o;
		return null;
	}

	@Override
	public void remove(PageContext pc,SQL sql, String datasource, String username,String password) {
		try {
			getCache(pc).remove(key(sql, datasource, username, password));
		} catch (IOException e) {}
	}

	@Override
	public void set(PageContext pc,SQL sql, String datasource, String username,String password, Object value, Date cacheBefore) {
		long timeSpan = ((cacheBefore.getTime()-System.currentTimeMillis())+1);
		getCache(pc).put(key(sql, datasource, username, password), new QueryCacheEntry(cacheBefore,value), Long.valueOf(timeSpan), Long.valueOf(timeSpan));
	}

	private Cache getCache(PageContext pc) {
		Cache c = Util.getDefault(pc,ConfigImpl.CACHE_DEFAULT_QUERY,DEFAULT_CACHE);	
		return c;
	}

    private String key(SQL sql, String datasource, String username,String password) {
    	try {
    		return Util.key(KeyGenerator.createKey(sql.toHashString()+datasource+username+password));
    		//return Util.key(Md5.getDigestAsString(sql.toHashString()+datasource+username+password));
		} 
    	catch (IOException e) {
			return null;
		}
	}
    
    @Override
    public void clear(PageContext pc,QueryCacheFilter filter) {
    	try {
			_clear(pc,filter);
		} catch (IOException e) {}
    }

	private void _clear(PageContext pc,QueryCacheFilter filter) throws IOException {
		Cache c = getCache(pc);
		Iterator it = c.entries().iterator();
    	String key;
    	CacheEntry entry;
    	QueryCacheEntry ce;
    	Query q;
    	while(it.hasNext()){
			entry=(CacheEntry) it.next();
			if(!(entry.getValue() instanceof QueryCacheEntry)) continue;
			ce=(QueryCacheEntry) entry.getValue();
			if(!(ce.getValue() instanceof Query)) continue;
			q=(Query) ce.getValue();
			key=entry.getKey();
    		if(filter.accept(q.getSql().toString())){
				c.remove(key);
    		}
    	}
	}

	@Override
	public int size(PageContext pc) {
		try {
			return getCache(pc).keys().size();
		} catch (IOException e) {
			return 0;
		}
	}


	public long sizeOf() {
		// TODO Auto-generated method stub
		return 0;
	}
}
