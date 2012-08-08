package railo.runtime.functions.cache;

import java.io.IOException;

import railo.commons.io.cache.Cache;
import railo.commons.io.cache.CacheEntry;
import railo.runtime.PageContext;
import railo.runtime.config.ConfigImpl;
import railo.runtime.exp.PageException;
import railo.runtime.ext.function.Function;
import railo.runtime.op.Caster;
import railo.runtime.type.Collection;
import railo.runtime.type.KeyImpl;
import railo.runtime.type.Struct;
import railo.runtime.type.StructImpl;

/**
 * 
 */
public final class CacheGetMetadata implements Function {
	
	private static final long serialVersionUID = -470089623854482521L;
	
	private static final Collection.Key CACHE_HITCOUNT = KeyImpl.intern("cache_hitcount");
	private static final Collection.Key CACHE_MISSCOUNT = KeyImpl.intern("cache_misscount");
	private static final Collection.Key CACHE_CUSTOM = KeyImpl.intern("cache_custom");
	private static final Collection.Key CUSTOM = KeyImpl.intern("custom");
	private static final Collection.Key CREATED_TIME = KeyImpl.intern("createdtime");
	private static final Collection.Key HIT_COUNT = KeyImpl.intern("hitcount");
	private static final Collection.Key IDLE_TIME = KeyImpl.intern("idletime");
	private static final Collection.Key LAST_HIT = KeyImpl.intern("lasthit");
	private static final Collection.Key LAST_UPDATED = KeyImpl.intern("lastupdated");
	private static final Collection.Key TIME_SPAN = KeyImpl.intern("timespan");

	public static Struct call(PageContext pc, String id) throws PageException {
		return call(pc, id,null);
	}
	
	public static Struct call(PageContext pc, String id, String cacheName) throws PageException {
		try {
			Cache cache = Util.getCache(pc,cacheName,ConfigImpl.CACHE_DEFAULT_OBJECT);
			CacheEntry entry = cache.getCacheEntry(Util.key(id));
			
			Struct info=new StructImpl();
			info.set(CACHE_HITCOUNT, new Double(cache.hitCount()));
			info.set(CACHE_MISSCOUNT, new Double(cache.missCount()));
			info.set(CACHE_CUSTOM, cache.getCustomInfo());
			info.set(CUSTOM, entry.getCustomInfo());
			
			info.set(CREATED_TIME, entry.created());
			info.set(HIT_COUNT, new Double(entry.hitCount()));
			info.set(IDLE_TIME, new Double(entry.idleTimeSpan()));
			info.set(LAST_HIT, entry.lastHit());
			info.set(LAST_UPDATED, entry.lastModified());
			info.set(KeyImpl.SIZE, new Double(entry.size()));
			info.set(TIME_SPAN, new Double(entry.liveTimeSpan()));
			return info;
		} catch (IOException e) {
			throw Caster.toPageException(e);
		}
	}
}