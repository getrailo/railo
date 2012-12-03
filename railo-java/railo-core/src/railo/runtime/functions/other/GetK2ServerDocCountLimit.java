/**
 * Implements the CFML Function getk2serverdoccountlimit
 */
package railo.runtime.functions.other;

import railo.runtime.PageContext;
import railo.runtime.ext.function.Function;
import railo.runtime.tag.util.DeprecatedUtil;

public final class GetK2ServerDocCountLimit implements Function {
	public static double call(PageContext pc ) {
		DeprecatedUtil.function(pc,"GetK2ServerDocCountLimit");
		return 0;
	}
}