/**
 * Implements the CFML Function iswddx
 */
package railo.runtime.functions.decision;

import railo.runtime.PageContext;
import railo.runtime.ext.function.Function;
import railo.runtime.op.Decision;

public final class IsWddx implements Function {
	public static boolean call(PageContext pc , Object o) {
		return Decision.isWddx(o);
	}
}