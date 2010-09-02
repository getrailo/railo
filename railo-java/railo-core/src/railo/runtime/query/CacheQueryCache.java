package railo.runtime.query;

import java.io.IOException;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import railo.commons.io.cache.Cache;
import railo.commons.io.cache.CacheEntry;
import railo.commons.lang.Md5;
import railo.runtime.cache.ram.RamCache;
import railo.runtime.cache.util.CacheKeyFilterAll;
import railo.runtime.config.Config;
import railo.runtime.config.ConfigImpl;
import railo.runtime.config.ConfigWeb;
import railo.runtime.db.SQL;
import railo.runtime.functions.cache.Util;
import railo.runtime.op.Caster;
import railo.runtime.type.Query;

 class CacheQueryCache extends QueryCacheSupport {
	
	

	private Config config;
	private final RamCache DEFAULT_CACHE=new RamCache();

	public CacheQueryCache(Config config){
		this.config=config;
		
	}

	/**
	 * @see railo.runtime.query.QueryCacheSupport#setConfigWeb(railo.runtime.config.ConfigWeb)
	 */
	public void setConfigWeb(ConfigWeb config) {
		this.config=config;
	}
	
	
	/**
	 * @see railo.runtime.query.QueryCache#clear()
	 */
	public void clear() {
		getCache().remove(CacheKeyFilterAll.getInstance());
	}


	/**
	 * @see railo.runtime.query.QueryCache#clearUnused()
	 */
	public void clearUnused() {
		
		Cache c = getCache();
		List entries = c.entries();
		if(entries.size()<100) return;
		
		Iterator it = entries.iterator();
		CacheEntry entry;
		QueryCacheEntry qce;
		while(it.hasNext()){
			entry=(CacheEntry) it.next();
			qce=(QueryCacheEntry) entry.getValue();
			if(qce.isInCacheRange(null))
				c.remove(entry.getKey());
		}
	}

	
	public Object get(SQL sql, String datasource, String username,String password, Date cacheAfter) {
		String key=key(sql,datasource,username,password);
		Object obj= getCache().getValue(key,null);
		if(obj instanceof QueryCacheEntry) {
			QueryCacheEntry entry=(QueryCacheEntry) obj;
			if(entry.isInCacheRange(cacheAfter)) {
		    	return entry.getValue();
		    }
		    getCache().remove(key);
		}
		return null;
	}

	public Query getQuery(SQL sql, String datasource, String username,String password, Date cacheAfter) {
		Object o=get(sql, datasource, username, password, cacheAfter);
		if(o instanceof Query) return (Query) o;
		return null;
	}

	public void remove(SQL sql, String datasource, String username,String password) {
		getCache().remove(key(sql, datasource, username, password));
	}

	public void set(SQL sql, String datasource, String username,String password, Object value, Date cacheBefore) {
		long timeSpan = ((cacheBefore.getTime()-System.currentTimeMillis())+1);
		getCache().put(key(sql, datasource, username, password), new QueryCacheEntry(cacheBefore,value), null, Long.valueOf(timeSpan));
	}

	private Cache getCache() {
		try {
			Cache c = Util.getDefault(config,ConfigImpl.CACHE_DEFAULT_QUERY,DEFAULT_CACHE);	
			return c;
		} 
		catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

    private String key(SQL sql, String datasource, String username,String password) {
    	try {
    		return Util.key(Md5.getDigestAsString(sql.toHashString()+datasource+username+password));
		} 
    	catch (IOException e) {
			return null;
		}
	}
    
	

	public void clear(QueryCacheFilter filter) {
		Cache c = getCache();
		Iterator it = c.entries().iterator();
    	String key;
    	CacheEntry entry;
    	while(it.hasNext()){
			entry=(CacheEntry) it.next();
    		key=Caster.toString(entry.getKey(),"@@@@@");
    		if(filter.accept(key)){
				c.remove(key);
    		}
    	}
	}


	/**
	 * @see railo.runtime.query.QueryCacheSupport#size()
	 */
	public int size() {
		return getCache().keys().size();
	}


	public long sizeOf() {
		// TODO Auto-generated method stub
		return 0;
	}
}
