package railo.runtime.functions.system;

import railo.runtime.PageContext;
import railo.runtime.ext.function.Function;

/**
 * returns the root of this actuell Page Context
 */
public final class GetContextRoot implements Function {
	
	public static String call(PageContext pc) {
		return pc. getHttpServletRequest().getContextPath();
	}
}