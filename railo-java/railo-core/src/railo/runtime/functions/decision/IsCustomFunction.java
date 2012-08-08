/**
 * Implements the CFML Function iscustomfunction
 */
package railo.runtime.functions.decision;

import railo.runtime.PageContext;
import railo.runtime.ext.function.Function;
import railo.runtime.op.Decision;
import railo.runtime.type.ObjectWrap;

public final class IsCustomFunction implements Function {

	private static final long serialVersionUID = 1578909692090122692L;

	public static boolean call(PageContext pc , Object object) {
		if(object instanceof ObjectWrap) {
        	return call(pc,((ObjectWrap)object).getEmbededObject(null));
        }
		return Decision.isUserDefinedFunction(object) && !Decision.isClosure(object);
	}
}