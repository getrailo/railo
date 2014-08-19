package railo.runtime.cache.tag;

import railo.runtime.PageContext;
import railo.runtime.config.ConfigImpl;
import railo.runtime.config.ConfigWeb;
import railo.runtime.config.ConfigWebUtil;
import railo.runtime.exp.PageException;
import railo.runtime.type.Query;
import railo.runtime.type.QueryImpl;
import railo.runtime.type.Struct;
import railo.runtime.type.StructImpl;
import railo.runtime.type.dt.DateTimeImpl;

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
