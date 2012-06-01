/**
 * Implements the CFML Function isk2serverabroker
 */
package railo.runtime.functions.decision;

import railo.runtime.PageContext;
import railo.runtime.ext.function.Function;
import railo.runtime.tag.util.DeprecatedUtil;

public final class IsK2ServerABroker implements Function {
	public static boolean call(PageContext pc ) {
		DeprecatedUtil.function(pc,"IsK2ServerABroker");
		return false;
	}
}