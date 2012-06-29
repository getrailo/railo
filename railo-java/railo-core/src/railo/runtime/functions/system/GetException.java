/**
 * Implements the CFML Function getexception
 */
package railo.runtime.functions.system;

import railo.runtime.PageContext;
import railo.runtime.exp.ExpressionException;
import railo.runtime.exp.FunctionNotSupported;
import railo.runtime.ext.function.Function;

public final class GetException implements Function {
	public static java.lang.Throwable call(PageContext pc , Object object) throws ExpressionException {
		throw new FunctionNotSupported("getexception");
	}
}