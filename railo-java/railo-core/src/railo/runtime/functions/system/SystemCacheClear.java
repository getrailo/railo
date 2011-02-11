/**
 * Implements the Cold Fusion Function gettemplatepath
 */
package railo.runtime.functions.system;

import railo.commons.lang.ExceptionUtil;
import railo.commons.lang.StringUtil;
import railo.runtime.PageContext;
import railo.runtime.config.ConfigWebImpl;
import railo.runtime.exp.FunctionException;
import railo.runtime.ext.function.Function;
import railo.runtime.functions.component.ComponentCacheClear;
import railo.runtime.functions.other.CTCacheClear;
import railo.runtime.query.QueryCacheSupport;

public final class SystemCacheClear implements Function {
	
	private static final long serialVersionUID = 2151674703665027213L;

	public static String call(PageContext pc) throws FunctionException {
		return call(pc,null);
	}
	public static String call(PageContext pc, String cacheName) throws FunctionException {
		
		if(StringUtil.isEmpty(cacheName,true) || "all".equals(cacheName=cacheName.trim().toLowerCase())) {
			PagePoolClear.call(pc);
			ComponentCacheClear.call(pc);
			CTCacheClear.call(pc);
			queryCache(pc);
		}
		else if("template".equals(cacheName) || "page".equals(cacheName)) {
			PagePoolClear.call(pc);
		}
		else if("component".equals(cacheName) || "cfc".equals(cacheName)) {
			ComponentCacheClear.call(pc);
		}
		else if("customtag".equals(cacheName) || "ct".equals(cacheName)) {
			CTCacheClear.call(pc);
		}
		else if("query".equals(cacheName) || "object".equals(cacheName)) {
			queryCache(pc);
		}
		else if("tag".equals(cacheName)) {
			tagCache(pc);
		}
		else if("function".equals(cacheName)) {
			functionCache(pc);
		}
		else
			throw new FunctionException(pc, "cacheClear", 1, "cacheName", 
					ExceptionUtil.similarKeyMessage(new String[]{"all","template","component","customtag","query","tag","function"}, cacheName, "cache name", "cache names"));
		
		
		return null;
	}
	
	private static void queryCache(PageContext pc) {
		QueryCacheSupport qc = ((QueryCacheSupport)pc.getQueryCache());
		qc.clear();
	}

	private static void tagCache(PageContext pc) {
		ConfigWebImpl config=(ConfigWebImpl) pc.getConfig();
		config.clearFunctionCache();
		PagePoolClear.clear(config.getServerTagMapping());
		PagePoolClear.clear(config.getTagMapping());
	}
	
	private static void functionCache(PageContext pc) {
		ConfigWebImpl config=(ConfigWebImpl) pc.getConfig();
		config.clearFunctionCache();
		PagePoolClear.clear(config.getServerFunctionMapping());
		PagePoolClear.clear(config.getFunctionMapping());
		
	}
}