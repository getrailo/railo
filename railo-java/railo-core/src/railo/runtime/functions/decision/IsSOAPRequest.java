package railo.runtime.functions.decision;

import railo.runtime.PageContext;
import railo.runtime.exp.ExpressionException;
import railo.runtime.exp.FunctionNotSupported;
import railo.runtime.ext.function.Function;

/**
 * 
 */
public final class IsSOAPRequest implements Function {
	
//	 TODO impl. function IsSOAPRequest
	public static boolean call(PageContext pc) throws ExpressionException {
		throw new FunctionNotSupported("IsSOAPRequest");
	}
}