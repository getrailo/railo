package railo.runtime.functions.xml;

import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.ext.function.Function;
import railo.runtime.net.rpc.AxisUtil;
import railo.runtime.op.Caster;

/**
 * 
 */
public final class GetSOAPRequestHeader implements Function {

	private static final long serialVersionUID = 7870631002414028102L;

	public static Object call(PageContext pc, String namespace, String name) throws PageException {
		return call(pc,namespace,name,false);
	}
	
	public static Object call(PageContext pc, String namespace, String name, boolean asXML) throws PageException {
		try {
			return AxisUtil.getSOAPRequestHeader(pc, namespace, name, asXML);
		}
		catch (Exception e) {
			throw Caster.toPageException(e); 
		}
	}
}