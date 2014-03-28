/**
 * Implements the CFML Function left
 */
package railo.runtime.functions.string;

import railo.runtime.PageContext;
import railo.runtime.exp.ExpressionException;
import railo.runtime.ext.function.Function;

public final class Left implements Function {

	public static String call(PageContext pc, String str, double number) throws ExpressionException {

		int len = (int)number;

		if (len == 0) throw new ExpressionException("parameter 2 of the function left can not be 0");

		if (Math.abs(len) >= str.length()) return str;

		if (len < 0)
			len = str.length() + len;

		return str.substring(0,len);
	}
}