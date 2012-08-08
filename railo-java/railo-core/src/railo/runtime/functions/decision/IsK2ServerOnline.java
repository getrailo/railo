/**
 * Implements the CFML Function isk2serveronline
 */
package railo.runtime.functions.decision;

import railo.runtime.PageContext;
import railo.runtime.ext.function.Function;
import railo.runtime.tag.util.DeprecatedUtil;

public final class IsK2ServerOnline implements Function {
	public static boolean call(PageContext pc ) {
		DeprecatedUtil.function(pc,"IsK2ServerOnline");
	    return false;
	}
}