package railo.runtime.functions.owasp;

import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.ext.function.Function;

public class EncodeForDN implements Function{

	private static final long serialVersionUID = -3276058916547934830L;

	public static String call(PageContext pc , String item) throws PageException  {
		return ESAPIEncode.encode(item, ESAPIEncode.ENC_DN);
	}

}