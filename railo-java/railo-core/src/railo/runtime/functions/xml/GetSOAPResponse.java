package railo.runtime.functions.xml;

import railo.runtime.PageContext;
import railo.runtime.exp.FunctionException;
import railo.runtime.exp.PageException;
import railo.runtime.ext.function.Function;
import railo.runtime.net.rpc.AxisUtil;
import railo.runtime.net.rpc.client.RPCClient;
import railo.runtime.op.Caster;

public final class GetSOAPResponse implements Function {

	private static final long serialVersionUID = 7155984396258463949L;

	public static Object call(PageContext pc, Object webservice) throws PageException {
		if(!(webservice instanceof RPCClient))
			throw new FunctionException(pc, "getSOAPResponse", 1, "webservice", "value must be a webservice Object generated with createObject/<cfobject>");
		try {
			return AxisUtil.getSOAPResponse((RPCClient) webservice);
		} 
		catch (Exception e) {
			throw Caster.toPageException(e);
		}
		
	}
}