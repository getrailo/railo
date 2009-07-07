package railo.runtime.functions.gateway;

import railo.runtime.PageContext;
import railo.runtime.exp.ExpressionException;
import railo.runtime.exp.FunctionNotSupported;
import railo.runtime.ext.function.Function;

/**
 * Decodes Binary Data that are encoded as String
 */
public final class GetGatewayHelper implements Function {
	
//	 TODO impl. Function GetGatewayHelper
	public static Object call(PageContext pc, String gatewayID) throws ExpressionException {
		throw new FunctionNotSupported("GetGatewayHelper");
	}
}