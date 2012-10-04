package railo.runtime.functions.cache;

import railo.commons.io.cache.Cache;
import railo.runtime.PageContext;
import railo.runtime.config.ConfigImpl;
import railo.runtime.exp.PageException;
import railo.runtime.ext.function.Function;
import railo.runtime.op.Caster;
import railo.runtime.type.dt.TimeSpan;

/**
 * 
 */
public final class CachePut implements Function {

	private static final long serialVersionUID = -8636947330333269874L;

	public static String call(PageContext pc, String key,Object value) throws PageException {
		return _call(pc,key, value, null, null,null);
	}
	public static String call(PageContext pc, String key,Object value,TimeSpan timeSpan) throws PageException {
		return _call(pc,key, value, valueOf(timeSpan), null,null);
	}
	public static String call(PageContext pc, String key,Object value,TimeSpan timeSpan, TimeSpan idleTime) throws PageException {
		return _call(pc,key, value, valueOf(timeSpan), valueOf(idleTime),null);
	}
	public static String call(PageContext pc, String key,Object value,TimeSpan timeSpan, TimeSpan idleTime,String cacheName) throws PageException {
		return _call(pc,key, value, valueOf(timeSpan), valueOf(idleTime),cacheName);
	}
	
	private static String _call(PageContext pc, String key,Object value,Long timeSpan, Long idleTime,String cacheName) throws PageException {
		//if(timeSpan!=null && timeSpan.longValue()==0L) return "";
		//if(idleTime!=null && idleTime.longValue()==0L) return "";
		try {
			Cache cache = Util.getCache(pc,cacheName,ConfigImpl.CACHE_DEFAULT_OBJECT);
			cache.put(Util.key(key), value, idleTime, timeSpan);
		} catch (Exception e) {
			throw Caster.toPageException(e);
		}
		
		return "";
	}
	
	private static Long valueOf(TimeSpan timeSpan) {
		if(timeSpan==null) return null;
		return Long.valueOf(timeSpan.getMillis());
	}
}