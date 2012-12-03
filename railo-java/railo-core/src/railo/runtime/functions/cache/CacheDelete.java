package railo.runtime.functions.cache;

import java.io.IOException;

import railo.commons.io.cache.Cache;
import railo.runtime.PageContext;
import railo.runtime.config.ConfigImpl;
import railo.runtime.exp.ApplicationException;
import railo.runtime.exp.PageException;
import railo.runtime.ext.function.Function;
import railo.runtime.op.Caster;

/**
 * 
 */
public final class CacheDelete implements Function {
	
	private static final long serialVersionUID = 4148677299207997607L;

	public static String call(PageContext pc, String id) throws PageException {
		return call(pc, id, false,null);
	}
	public static String call(PageContext pc, String id, boolean throwOnError) throws PageException {
		return call(pc, id, throwOnError, null);
	}
	
	public static String call(PageContext pc, String id, boolean throwOnError, String cacheName) throws PageException {
		try {
			Cache cache = Util.getCache(pc,cacheName,ConfigImpl.CACHE_DEFAULT_OBJECT);
			if(!cache.remove(Util.key(id)) && throwOnError){
				throw new ApplicationException("can not remove the element with the following id ["+id+"]");
			}	
		} 
		catch (IOException e) {
			throw Caster.toPageException(e);
		}
		return null;
	}
	
}