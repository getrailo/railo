/**
 * Implements the CFML Function right
 */
package railo.runtime.functions.string;

import railo.runtime.PageContext;
import railo.runtime.exp.ExpressionException;
import railo.runtime.ext.function.Function;

public final class Right implements Function {

	public static String call(PageContext pc, String str, double number) throws ExpressionException {

		int len = (int)number;

		if (len == 0) throw new ExpressionException("parameter 2 of the function right can not be 0");

		if (Math.abs(len) >= str.length()) return str;

		int l=str.length();

		if (len < 0)
			len = l + len;

		return str.substring(l-len,l);
	}
}