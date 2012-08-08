/**
 * Implements the CFML Function isdebugmode
 */
package railo.runtime.functions.decision;

import railo.runtime.PageContext;
import railo.runtime.ext.function.Function;

public final class IsDebugMode implements Function {
	public static boolean call(PageContext pc ) {
		return pc.getConfig().debug();
	}
}