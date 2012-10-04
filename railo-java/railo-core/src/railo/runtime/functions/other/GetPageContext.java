/**
 * Implements the CFML Function getpagecontext
 */
package railo.runtime.functions.other;

import railo.runtime.PageContext;
import railo.runtime.ext.function.Function;

public final class GetPageContext implements Function {
	public static Object call(PageContext pc ) {
		return pc;
	}
}