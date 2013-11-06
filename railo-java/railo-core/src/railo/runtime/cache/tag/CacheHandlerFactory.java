package railo.runtime.cache.tag;

import java.util.HashMap;
import java.util.Map;

import railo.runtime.cache.tag.request.RequestCacheHandler;
import railo.runtime.cache.tag.timespan.TimespanCacheHandler;
import railo.runtime.config.Config;
import railo.runtime.config.ConfigImpl;
import railo.runtime.op.Caster;
import railo.runtime.type.dt.TimeSpan;

public class CacheHandlerFactory {
	
	private static final RequestCacheHandler rch=new RequestCacheHandler();
	private Map<Config,TimespanCacheHandler> tschs=new HashMap<Config, TimespanCacheHandler>();
	
	/**
	 * based on the cachedWithin Object we  choose the right Cachehandler and return it
	 * @return 
	 */
	public CacheHandler getInstance(Config config,Object cachedWithin){
		if(Caster.toTimespan(cachedWithin,null)!=null) {
			TimespanCacheHandler tsch = tschs.get(config);
			if(tsch==null) {
				tschs.put(config, tsch=new TimespanCacheHandler(ConfigImpl.CACHE_DEFAULT_QUERY, null));
			}
			return tsch;
		}
		String str=Caster.toString(cachedWithin,"").trim();
		if("request".equalsIgnoreCase(str)) return rch;
		
		return null;
	}
}
