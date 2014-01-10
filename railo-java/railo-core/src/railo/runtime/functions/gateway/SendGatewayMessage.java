package railo.runtime.functions.gateway;

import java.io.IOException;

import railo.runtime.PageContext;
import railo.runtime.config.ConfigWebImpl;
import railo.runtime.exp.PageException;
import railo.runtime.ext.function.Function;
import railo.runtime.op.Caster;
import railo.runtime.type.Struct;

/**
 * 
 */
public final class SendGatewayMessage implements Function {
	
	public static String call(PageContext pc, String gatewayID, Struct data) throws PageException {
		//GatewayEngineImpl.checkRestriction();
		try {
			return ((ConfigWebImpl)pc.getConfig()).getGatewayEngine().sendMessage(gatewayID,data);
		} catch (IOException e) {
			throw Caster.toPageException(e);
		}
		
		
	}
}