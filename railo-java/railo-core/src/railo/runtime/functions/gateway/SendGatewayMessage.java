package railo.runtime.functions.gateway;

import railo.runtime.PageContext;
import railo.runtime.exp.ExpressionException;
import railo.runtime.exp.FunctionNotSupported;
import railo.runtime.ext.function.Function;

/**
 * 
 */
public final class SendGatewayMessage implements Function {
	
//	 TODO impl. function SendGatewayMessage
	public static String call(PageContext pc, String gatewayID, Object data) throws ExpressionException {
		throw new FunctionNotSupported("SendGatewayMessage");
	}
}