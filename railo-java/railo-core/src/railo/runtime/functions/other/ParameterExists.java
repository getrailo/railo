/**
 * Implements the CFML Function parameterexists
 */
package railo.runtime.functions.other;

import railo.runtime.PageContext;
import railo.runtime.ext.function.Function;
import railo.runtime.functions.decision.IsDefined;

public final class ParameterExists implements Function {
	public static boolean call(PageContext pc , String string) {
		return IsDefined.call(pc,string);
	}
}