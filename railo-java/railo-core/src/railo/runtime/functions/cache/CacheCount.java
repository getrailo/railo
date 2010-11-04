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
public final class CacheCount implements Function {
	
	private static final long serialVersionUID = 4192649311671009474L;

	public static double call(PageContext pc) throws PageException {
		return call(pc,null);
		
	}
	
	public static double call(PageContext pc, String cacheName) throws PageException {
		try {
			return Util.getCache(pc,cacheName,ConfigImpl.CACHE_DEFAULT_OBJECT).keys().size();
		} catch (IOException e) {
			throw Caster.toPageException(e);
		}
	}
}