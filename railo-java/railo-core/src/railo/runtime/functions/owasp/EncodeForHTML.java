package railo.runtime.functions.owasp;

import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.ext.function.Function;

public final class EncodeForHTML implements Function {

	private static final long serialVersionUID = -1508966470286647271L;

	public static String call(PageContext pc , String item) throws PageException  {
		return ESAPIEncode.encode(item, ESAPIEncode.ENC_HTML);
	}
}