package railo.runtime.functions.xml;

import railo.runtime.PageContext;
import railo.runtime.exp.FunctionException;
import railo.runtime.exp.PageException;
import railo.runtime.ext.function.Function;
import railo.runtime.net.rpc.AxisUtil;
import railo.runtime.net.rpc.client.RPCClient;
import railo.runtime.op.Caster;

/**
 * 
 */
public final class GetSOAPRequest implements Function {

	private static final long serialVersionUID = -1743528432690118148L;

	public static Object call(PageContext pc) throws PageException {
		return call(pc, null);
	}
	public static Object call(PageContext pc, Object webservice) throws PageException {
		if(webservice!=null && !(webservice instanceof RPCClient))
			throw new FunctionException(pc, "getSOAPRequest", 1, "webservice", "value must be a webservice Object generated with createObject/<cfobject>");
		
		try {
			return AxisUtil.getSOAPRequest((RPCClient) webservice);
		} 
		catch (Exception e) {
			throw Caster.toPageException(e);
		}
		
	}
}