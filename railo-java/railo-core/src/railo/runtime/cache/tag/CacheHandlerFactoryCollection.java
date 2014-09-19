package railo.runtime.cache.tag;

import railo.runtime.PageContext;
import railo.runtime.config.ConfigImpl;
import railo.runtime.config.ConfigWeb;

public class CacheHandlerFactoryCollection {
	
	public final CacheHandlerFactory query=new CacheHandlerFactory(ConfigImpl.CACHE_DEFAULT_QUERY);
	public final CacheHandlerFactory function=new CacheHandlerFactory(ConfigImpl.CACHE_DEFAULT_FUNCTION);
	public final CacheHandlerFactory include=new CacheHandlerFactory(ConfigImpl.CACHE_DEFAULT_INCLUDE);
	
	private ConfigWeb cw;
	

	public CacheHandlerFactoryCollection(ConfigWeb cw) {
		this.cw=cw;
	}

	public void release(PageContext pc){
		query.rch.clear(pc);
		function.rch.clear(pc);
		include.rch.clear(pc);
	}
}
