/**
 * Implements the CFML Function isstruct
 */
package railo.runtime.functions.struct;

import railo.runtime.PageContext;
import railo.runtime.ext.function.Function;
import railo.runtime.op.Decision;

public final class IsStruct implements Function {
	public static boolean call(PageContext pc , Object object) {
		return Decision.isStruct(object);
	}
}