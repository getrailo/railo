package railo.runtime.functions.owasp;

import railo.runtime.PageContext;
import railo.runtime.ext.function.Function;

public class Canonicalize implements Function {

	private static final long serialVersionUID = -4248746351014698481L;

	public static String call(PageContext pc,String input, boolean restrictMultiple, boolean restrictMixed) {
		return ESAPIEncode.canonicalize(input, restrictMultiple,restrictMixed);
	}
}
