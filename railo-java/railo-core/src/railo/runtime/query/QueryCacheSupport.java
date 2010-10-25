package railo.runtime.query;

import java.io.Serializable;

import railo.runtime.config.Config;
import railo.runtime.config.ConfigWeb;
import railo.runtime.type.Sizeable;


public abstract class QueryCacheSupport implements QueryCache,Sizeable,Serializable {
	// FUTURE add to interface
	public abstract int size();
	// FUTURE add to interface
	public  abstract void clear(QueryCacheFilter filter);
	
	public static QueryCacheSupport getInstance(Config config){
		return new CacheQueryCache(config);
		//return new MemoryQueryCache();
	}
	public abstract void setConfigWeb(ConfigWeb config);
}
