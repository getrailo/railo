package railo.runtime.functions.cache;

import java.io.IOException;

import railo.runtime.PageContext;
import railo.runtime.config.ConfigImpl;
import railo.runtime.exp.PageException;
import railo.runtime.ext.function.Function;
import railo.runtime.op.Caster;

/**
 * 
 */
public final class CacheKeyExists implements Function {
	
	private static final long serialVersionUID = -5656876871645994195L;

	public static boolean call(PageContext pc, String key) throws PageException {
		return call(pc, key, null);
	}
	
	public static boolean call(PageContext pc, String key,String cacheName) throws PageException {
		try {
			return Util.getCache(pc,cacheName,ConfigImpl.CACHE_DEFAULT_OBJECT).contains(Util.key(key));
		} catch (IOException e) {
			throw Caster.toPageException(e);
		}
	}
	
}