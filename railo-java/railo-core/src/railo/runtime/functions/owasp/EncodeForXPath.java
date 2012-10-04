package railo.runtime.functions.owasp;

import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.ext.function.Function;

public class EncodeForXPath implements Function{

	private static final long serialVersionUID = 700921117449046922L;

	public static String call(PageContext pc , String item) throws PageException  {
		return ESAPIEncode.encode(item, ESAPIEncode.ENC_XPATH);
	}

}