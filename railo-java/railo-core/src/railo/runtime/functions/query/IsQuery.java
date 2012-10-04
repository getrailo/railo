/**
 * Implements the CFML Function isquery
 */
package railo.runtime.functions.query;

import railo.runtime.PageContext;
import railo.runtime.ext.function.Function;
import railo.runtime.type.Query;

public final class IsQuery implements Function {
	public static boolean call(PageContext pc , Object object) {
		
		return object instanceof Query;
	}
}