/**
 * Implements the CFML Function isdate
 */
package railo.runtime.functions.decision;

import railo.runtime.PageContext;
import railo.runtime.ext.function.Function;
import railo.runtime.op.Decision;

public final class IsDate implements Function {
	public static boolean call(PageContext pc , Object object) {
		return Decision.isDateAdvanced(object,false);
	}
	public static boolean call(PageContext pc , Object object,boolean allowLocaleBasedDates) {
		if(allowLocaleBasedDates)
			return Decision.isDateAdvanced(object,false);
		return Decision.isDateSimple(object,false);
	}
}