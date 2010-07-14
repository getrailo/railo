package railo.runtime.functions.cache;

import java.io.IOException;

import railo.commons.io.cache.Cache;
import railo.runtime.PageContext;
import railo.runtime.config.ConfigImpl;
import railo.runtime.exp.PageException;
import railo.runtime.ext.function.Function;
import railo.runtime.op.Caster;

/**
 * 
 */
public final class CacheGet implements Function {

	public static Object call(PageContext pc, String key) throws PageException {
		CacheGet.checkRestriction(pc);
		return call(pc, key,false, null);
	}
	
	public static Object call(PageContext pc, String key, boolean throwWhenNotExist) throws PageException {
		CacheGet.checkRestriction(pc);
		return call(pc, key,throwWhenNotExist, null);
	}
	
	public static Object call(PageContext pc, String key, boolean throwWhenNotExist,String cacheName) throws PageException {
		try {
			Cache cache = Util.getCache(pc,cacheName,ConfigImpl.CACHE_DEFAULT_OBJECT);
			return throwWhenNotExist?cache.getValue(Util.key(key)):cache.getValue(Util.key(key),null);
		} catch (IOException e) {
			throw Caster.toPageException(e);
		}
	}
	

	public static void checkRestriction(PageContext pc) {
		/*boolean enable = false;
		try {
			enable=Caster.toBooleanValue(pc.serverScope().get("enableCache", Boolean.FALSE), false);
		} 
		catch (PageException e) {}
		//enable=false;
		if(!enable)
			throw new PageRuntimeException(new railo.runtime.exp.SecurityException("this functionality is not supported"));
		*/
	}
}