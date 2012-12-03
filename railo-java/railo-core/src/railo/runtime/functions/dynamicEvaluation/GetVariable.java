/**
 * Implements the CFML Function setvariable
 */
package railo.runtime.functions.dynamicEvaluation;

import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.ext.function.Function;

public final class GetVariable implements Function {
	public static Object call(PageContext pc , String name) throws PageException {
		return pc.getVariable(name);
	}
}