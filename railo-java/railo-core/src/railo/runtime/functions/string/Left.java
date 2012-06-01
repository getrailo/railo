/**
 * Implements the CFML Function left
 */
package railo.runtime.functions.string;

import railo.runtime.PageContext;
import railo.runtime.exp.ExpressionException;
import railo.runtime.ext.function.Function;

public final class Left implements Function {
	public static String call(PageContext pc , String str, double number) throws ExpressionException {
		int len=(int) number;
		if(len<1) throw new ExpressionException("parameter 2 of the function left must be a positive number now ["+len+"]");
		if(len>=str.length()) return str;
		return str.substring(0,len);
	}
}