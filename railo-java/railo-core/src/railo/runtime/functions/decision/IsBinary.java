/**
 * Implements the CFML Function isbinary
 */
package railo.runtime.functions.decision;

import railo.runtime.PageContext;
import railo.runtime.ext.function.Function;
import railo.runtime.op.Decision;

public final class IsBinary implements Function {
	public static boolean call(PageContext pc , Object object) {
		return Decision.isBinary(object);
	}
}