/**
 * Implements the CFML Function isobject
 */
package railo.runtime.functions.decision;

import railo.runtime.PageContext;
import railo.runtime.ext.function.Function;
import railo.runtime.op.Decision;

public final class IsObject implements Function {
	public static boolean call(PageContext pc , Object object) {
		//throw new ExpressionException("method isobject not implemented yet");
		return Decision.isObject(object);
	}
}