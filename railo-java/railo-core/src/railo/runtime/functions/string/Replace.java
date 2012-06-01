/**
 * Implements the CFML Function replace
 */
package railo.runtime.functions.string;

import railo.commons.lang.StringUtil;
import railo.runtime.PageContext;
import railo.runtime.exp.ExpressionException;
import railo.runtime.ext.function.Function;

public final class Replace implements Function {
	
	public static String call(PageContext pc , String str, String sub1, String sub2) {
		return StringUtil.replace(str,sub1,sub2,true);
	}

	public static String call(PageContext pc , String str, String sub1, String sub2, String scope) throws ExpressionException {
		if(sub1.length()==0)
			throw new ExpressionException("the string length of Parameter 2 of function replace which is now ["+sub1.length()+"] must be greater than 0");
		return StringUtil.replace(str,sub1,sub2,!scope.equalsIgnoreCase("all"));
	}
}