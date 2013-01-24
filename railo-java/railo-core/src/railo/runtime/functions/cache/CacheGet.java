package railo.runtime.functions.cache;

import java.io.IOException;

import railo.commons.io.cache.Cache;
import railo.commons.lang.StringUtil;
import railo.runtime.PageContext;
import railo.runtime.config.ConfigImpl;
import railo.runtime.exp.FunctionException;
import railo.runtime.exp.PageException;
import railo.runtime.ext.function.Function;
import railo.runtime.op.Caster;

/**
 * 
 */
public final class CacheGet implements Function {

	private static final long serialVersionUID = -7164470356423036571L;

	public static Object call(PageContext pc, String key) throws PageException {
		try {
			return _call(pc, key, false, Util.getDefault(pc, ConfigImpl.CACHE_DEFAULT_OBJECT));
		} 
		catch (IOException e) {
			throw Caster.toPageException(e);
		}
	}

	public static Object call(PageContext pc, String key, Object objThrowWhenNotExist) throws PageException {
		// default behavior, second parameter is a boolean
		Boolean throwWhenNotExist=Caster.toBoolean(objThrowWhenNotExist,null);
		if(throwWhenNotExist!=null) {
			try {
				return _call(pc, key, throwWhenNotExist.booleanValue(), Util.getDefault(pc, ConfigImpl.CACHE_DEFAULT_OBJECT));
			} 
			catch (IOException e) {
				throw Caster.toPageException(e);
			}
		}
		
		// compatibility behavior, second parameter is a cacheName 
		if(objThrowWhenNotExist instanceof String) {
			String cacheName=(String)objThrowWhenNotExist;
			if(!StringUtil.isEmpty(cacheName)) {
				try {
					Cache cache = Util.getCache(pc.getConfig(),cacheName,null);
					
					if(cache!=null) 
						return _call(pc, key, false, cache);
				} 
				catch (IOException e) {
					throw Caster.toPageException(e);
				}
			}
		}
		
		// not a boolean or cacheName
		throw new FunctionException(pc, "cacheGet", 2, "ThrowWhenNotExist", "arguments needs to be a boolean value, but also a valid cacheName is supported for compatibility reasons to other engines");
	}
	
	public static Object call(PageContext pc, String key, Object objThrowWhenNotExist,String cacheName) throws PageException {
		
		
		Boolean throwWhenNotExist=Caster.toBoolean(objThrowWhenNotExist,null);
		if(throwWhenNotExist==null)throw new FunctionException(pc, "cacheGet", 2, "ThrowWhenNotExist", "arguments needs to be a boolean value");
		
		try {
			Cache cache = Util.getCache(pc,cacheName,ConfigImpl.CACHE_DEFAULT_OBJECT);
			return _call(pc, key, throwWhenNotExist.booleanValue(), cache);
		} catch (IOException e) {
			throw Caster.toPageException(e);
		}
	}

	private static Object _call(PageContext pc, String key, boolean throwWhenNotExist,Cache cache) throws IOException {
		return throwWhenNotExist?cache.getValue(Util.key(key)):cache.getValue(Util.key(key),null);
	}
}