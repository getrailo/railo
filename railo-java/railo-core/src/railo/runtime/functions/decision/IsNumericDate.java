/**
 * Implements the CFML Function isnumericdate
 */
package railo.runtime.functions.decision;

import railo.runtime.PageContext;
import railo.runtime.ext.function.Function;
import railo.runtime.op.Decision;

public final class IsNumericDate implements Function {
	public static boolean call(PageContext pc , Object object) {
	    return Decision.isDateAdvanced(object,true);
	}
}