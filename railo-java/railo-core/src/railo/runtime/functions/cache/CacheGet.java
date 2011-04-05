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

	private static final long serialVersionUID = -7164470356423036571L;

	public static Object call(PageContext pc, String key) throws PageException {
		return call(pc, key,false, null);
	}
	
	public static Object call(PageContext pc, String key, boolean throwWhenNotExist) throws PageException {
		return call(pc, key,throwWhenNotExist, null);
	}
	
	public static Object call(PageContext pc, String key, boolean throwWhenNotExist,String cacheName) throws PageException {
		try {
			Cache cache = Util.getCache(pc.getConfig(),cacheName,ConfigImpl.CACHE_DEFAULT_OBJECT);
			return throwWhenNotExist?cache.getValue(Util.key(key)):cache.getValue(Util.key(key),null);
		} catch (IOException e) {
			throw Caster.toPageException(e);
		}
	}
}