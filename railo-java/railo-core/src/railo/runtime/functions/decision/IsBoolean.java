/**
 * Implements the CFML Function isboolean
 */
package railo.runtime.functions.decision;

import railo.runtime.PageContext;
import railo.runtime.ext.function.Function;
import railo.runtime.op.Decision;

public final class IsBoolean implements Function {
	public static boolean call(PageContext pc , Object object) {
		return Decision.isBoolean(object) || Decision.isNumeric(object);
	}
}