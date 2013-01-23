package railo.runtime.functions.cache;

import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.ext.function.Function;

public class CacheRemoveAll implements Function {

	private static final long serialVersionUID = -3444983104369826751L;
	
	public static double call(PageContext pc) throws PageException {
		return CacheClear.call(pc);
		
	}
	public static double call(PageContext pc, String cacheName) throws PageException {
		return CacheClear.call(pc,null,cacheName);
	}
}