package railo.runtime.functions.xml;

import railo.runtime.PageContext;
import railo.runtime.exp.FunctionException;
import railo.runtime.exp.PageException;
import railo.runtime.ext.function.Function;
import railo.runtime.net.rpc.AxisUtil;
import railo.runtime.net.rpc.client.RPCClient;

public class AddSOAPRequestHeader implements Function {

	private static final long serialVersionUID = 4305004275924545217L;
	public static boolean call(PageContext pc, Object client,String nameSpace, String name, Object value) throws PageException {
		return call(pc, client, nameSpace, name, value,false);
	}
	public static boolean call(PageContext pc, Object client,String nameSpace, String name, Object value, boolean mustUnderstand) throws PageException {
		//if(true)throw new FunctionNotSupported("AddSOAPRequestHeader");
		if(!(client instanceof RPCClient))
			throw new FunctionException(pc, "addSOAPRequestHeader", 1, "webservice", "value must be a webservice Object generated with createObject/<cfobject>");
		
		AxisUtil.addSOAPRequestHeader((RPCClient) client, nameSpace, name, value, mustUnderstand);
		return true;
	}
}
