package railo.runtime.query;

import java.io.Serializable;


public abstract class QueryCacheSupport implements QueryCache,Serializable {
	
	public static QueryCacheSupport getInstance(){
		return new CacheQueryCache();
		//return new MemoryQueryCache();
	}
}
