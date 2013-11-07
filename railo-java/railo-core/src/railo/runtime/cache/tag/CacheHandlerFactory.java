package railo.runtime.cache.tag;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import railo.print;
import railo.commons.io.cache.Cache;
import railo.commons.io.cache.CacheEntry;
import railo.commons.lang.KeyGenerator;
import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.functions.cache.Util;
import railo.runtime.cache.tag.request.RequestCacheHandler;
import railo.runtime.cache.tag.timespan.TimespanCacheHandler;
import railo.runtime.config.Config;
import railo.runtime.config.ConfigImpl;
import railo.runtime.db.SQL;
import railo.runtime.op.Caster;
import railo.runtime.query.QueryCacheEntry;
import railo.runtime.query.QueryCacheFilter;
import railo.runtime.type.Query;

public class CacheHandlerFactory {

	public static final int TYPE_TIMESPAN=1;
	public static final int TYPE_REQUEST=2;
	
	public static CacheHandlerFactory query=new CacheHandlerFactory();
	
	private final RequestCacheHandler rch=new RequestCacheHandler();
	private Map<Config,TimespanCacheHandler> tschs=new HashMap<Config, TimespanCacheHandler>();
	
	private CacheHandlerFactory(){}
	
	
	/**
	 * based on the cachedWithin Object we  choose the right Cachehandler and return it
	 * @return 
	 */
	public CacheHandler getInstance(Config config,Object cachedWithin){
		if(Caster.toTimespan(cachedWithin,null)!=null) {
			return getTimespanCacheHandler(config);
		}
		String str=Caster.toString(cachedWithin,"").trim();
		if("request".equalsIgnoreCase(str)) return rch;
		
		return null;
	}
	
	public CacheHandler getInstance(Config config,int type){
		if(TYPE_TIMESPAN==type)return getTimespanCacheHandler(config);
		if(TYPE_REQUEST==type) return rch;
		return null;
	}
	
	private CacheHandler getTimespanCacheHandler(Config config) {
		TimespanCacheHandler tsch = tschs.get(config);
		if(tsch==null) {
			tschs.put(config, tsch=new TimespanCacheHandler(ConfigImpl.CACHE_DEFAULT_QUERY, null));
		}
		return tsch;
	}

	public int size(PageContext pc) {
		int size=rch.size(pc);
		size+=getTimespanCacheHandler(pc.getConfig()).size(pc);
		return size;
	}


	public void clear(PageContext pc) {
		rch.clear(pc);
		getTimespanCacheHandler(pc.getConfig()).clear(pc);
	}
	
	public void clear(PageContext pc, CacheHandlerFilter filter) {
		rch.clear(pc,filter);
		getTimespanCacheHandler(pc.getConfig()).clear(pc,filter);
	}
	
	public void clean(PageContext pc) {
		rch.clean(pc);
		getTimespanCacheHandler(pc.getConfig()).clean(pc);
	}

	public static String createId(SQL sql, String datasource, String username,String password) throws PageException{
		try {
			return Util.key(KeyGenerator.createKey(sql.toHashString()+datasource+username+password));
		}
		catch (IOException e) {
			throw Caster.toPageException(e);
		}
	}
}
