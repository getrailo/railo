package railo.runtime.functions.xml;

import railo.runtime.PageContext;
import railo.runtime.exp.ExpressionException;
import railo.runtime.exp.FunctionNotSupported;
import railo.runtime.ext.function.Function;

/**
 * 
 */
public final class GetSOAPResponseHeader implements Function {

//	 TODO impl. function GetSOAPResponseHeader
	public static Object call(PageContext pc, Object webservice,String namespace, String name) throws ExpressionException {
		return call(pc,webservice,namespace,name,false);
	}
	public static Object call(PageContext pc, Object webservice, String namespace, String name, boolean asXML) throws ExpressionException {
		throw new FunctionNotSupported("GetSOAPResponseHeader");
	}
}