package railo.runtime.functions.xml;

import railo.runtime.PageContext;
import railo.runtime.exp.FunctionException;
import railo.runtime.exp.PageException;
import railo.runtime.ext.function.Function;
import railo.runtime.net.rpc.AxisUtil;
import railo.runtime.net.rpc.client.WSClient;
import railo.runtime.op.Caster;

/**
 * 
 */
public final class GetSOAPResponseHeader implements Function {

	private static final long serialVersionUID = 4667361359302875802L;
	
	public static Object call(PageContext pc, Object webservice,String namespace, String name) throws PageException {
		return call(pc,webservice,namespace,name,false);
	}
	public static Object call(PageContext pc, Object webservice, String namespace, String name, boolean asXML) throws PageException {
		if(!(webservice instanceof WSClient))
			throw new FunctionException(pc, "getSOAPResponse", 1, "webservice", "value must be a webservice Object generated with createObject/<cfobject>");
		try {
			return AxisUtil.getSOAPResponseHeader(pc, (WSClient) webservice, namespace, name, asXML);
		} catch (Exception e) {
			throw Caster.toPageException(e);
		}
	}
}