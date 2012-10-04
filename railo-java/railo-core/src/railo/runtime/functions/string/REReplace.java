/**
 * Implements the CFML Function rereplace
 */
package railo.runtime.functions.string;

import org.apache.oro.text.regex.MalformedPatternException;

import railo.runtime.PageContext;
import railo.runtime.exp.ExpressionException;
import railo.runtime.exp.FunctionException;
import railo.runtime.ext.function.Function;
import railo.runtime.regex.Perl5Util;

public final class REReplace implements Function {

	public static String call(String string, String regExp, String replace) throws ExpressionException { // MUST is this really needed?
	    try {
			return Perl5Util.replace(string,regExp,replace,true,false);
		} catch (MalformedPatternException e) {
			throw new ExpressionException("reReplace"+"second"+"regularExpression"+e.getMessage());
		}
	}
	public static String call(PageContext pc , String string, String regExp, String replace) throws ExpressionException {
	    try {
			return Perl5Util.replace(string,regExp,replace,true,false);
		} catch (MalformedPatternException e) {
			throw new FunctionException(pc,"reReplace",2,"regularExpression",e.getMessage());
		}
	}
	public static String call(PageContext pc , String string, String regExp, String replace, String scope) throws ExpressionException {
		try {
			if(scope.equalsIgnoreCase("all"))return Perl5Util.replace(string,regExp,replace,true,true);
			return Perl5Util.replace(string,regExp,replace,true,false);
		} catch (MalformedPatternException e) {
		    throw new FunctionException(pc,"reReplace",2,"regularExpression",e.getMessage());
		}
	}

}

