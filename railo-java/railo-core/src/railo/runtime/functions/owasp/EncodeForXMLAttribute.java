package railo.runtime.functions.owasp;

import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.ext.function.Function;

public class EncodeForXMLAttribute implements Function {

	private static final long serialVersionUID = 4453529689082237938L;

	public static String call(PageContext pc , String item)  throws PageException  {
		return ESAPIEncode.encode(item, ESAPIEncode.ENC_XML_ATTR);
	}
}