package railo.runtime.query;

import java.io.Serializable;

import railo.runtime.type.Sizeable;


public abstract class QueryCacheSupport implements QueryCache,Sizeable,Serializable {
	
	public static QueryCacheSupport getInstance(){
		return new CacheQueryCache();
		//return new MemoryQueryCache();
	}
}
