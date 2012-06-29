/**
 * Implements the CFML Function isdate
 */
package railo.runtime.functions.decision;

import railo.runtime.PageContext;
import railo.runtime.exp.FunctionNotSupported;
import railo.runtime.ext.function.Function;

public final class IsDDX implements Function {
	public static boolean call(PageContext pc , String strOrPath) throws FunctionNotSupported {
		throw new FunctionNotSupported("IsDDX");
	}
}