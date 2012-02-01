package railo.runtime.functions.owasp;

import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.ext.function.Function;

public final class EncodeForJavaScript implements Function {

	private static final long serialVersionUID = 6729545070819382659L;

	public static String call(PageContext pc , String item)  throws PageException  {
		return ESAPIEncode.encode(item, ESAPIEncode.ENC_JAVA_SCRIPT);
	}
}