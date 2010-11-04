package railo.runtime.functions.cache;

import railo.runtime.PageContext;
import railo.runtime.cache.CacheConnection;
import railo.runtime.config.ConfigImpl;
import railo.runtime.exp.ExpressionException;
import railo.runtime.exp.FunctionException;
import railo.runtime.exp.PageException;
import railo.runtime.ext.function.Function;

/**
 * 
 */
public final class CacheGetDefaultCacheName implements Function {

	private static final long serialVersionUID = 6115589794465960484L;

	public static String call(PageContext pc, String strType) throws PageException {
		int type = Util.toType(strType,ConfigImpl.CACHE_DEFAULT_NONE);
		if(type==ConfigImpl.CACHE_DEFAULT_NONE)
			throw new FunctionException(pc,"CacheGetDefaultCacheName",1,"type","invalid type defintion ["+strType+"], valid types are [object,resource,template,query]");
		
		ConfigImpl config=(ConfigImpl) pc.getConfig();
		CacheConnection conn = config.getCacheDefaultConnection(type);
		if(conn==null)
			throw new ExpressionException("there is no default cache defined for type ["+strType+"]");
		
		return conn.getName();
	}
}