package railo.runtime.functions.owasp;

import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.ext.function.Function;

public final class EncodeForHTMLAttribute implements Function {

	private static final long serialVersionUID = 2714067291217940292L;

	public static String call(PageContext pc , String item) throws PageException  {
		return ESAPIEncode.encode(item, ESAPIEncode.ENC_HTML_ATTR);
	}
}