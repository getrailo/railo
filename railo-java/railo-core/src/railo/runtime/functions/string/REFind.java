/**
 * Implements the CFML Function refind
 */
package railo.runtime.functions.string;

import org.apache.oro.text.regex.MalformedPatternException;

import railo.runtime.PageContext;
import railo.runtime.exp.ExpressionException;
import railo.runtime.exp.FunctionException;
import railo.runtime.ext.function.Function;
import railo.runtime.regex.Perl5Util;

public final class REFind implements Function {
	public static Object call(PageContext pc , String regExpr, String str) throws ExpressionException {
		return call(pc,regExpr,str,1,false);
	}
	public static Object call(PageContext pc , String regExpr, String str, double start) throws ExpressionException {
		return call(pc,regExpr,str,start,false);
	}
	public static Object call(PageContext pc , String regExpr, String str, double start, boolean returnsubexpressions) throws ExpressionException {
		try {
			if(returnsubexpressions)
				return Perl5Util.find(regExpr,str,(int)start,true);
			return new Double(Perl5Util.indexOf(regExpr,str,(int)start,true));
		} catch (MalformedPatternException e) {
			throw new FunctionException(pc,"reFind",1,"regularExpression",e.getMessage());
		}
	}
}