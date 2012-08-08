/**
 * Implements the CFML Function isk2serverdoccountexceeded
 */
package railo.runtime.functions.decision;

import railo.runtime.PageContext;
import railo.runtime.ext.function.Function;
import railo.runtime.tag.util.DeprecatedUtil;

public final class IsK2ServerDocCountExceeded implements Function {
	public static boolean call(PageContext pc ) {
		DeprecatedUtil.function(pc,"IsK2ServerDocCountExceeded");
	    return false;
	}
}