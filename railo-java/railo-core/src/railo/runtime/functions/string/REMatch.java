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
import railo.runtime.type.Array;

public final class REMatch implements Function {
	
	public static Array call(PageContext pc , String regExpr, String str) throws ExpressionException {
		try {
			return Perl5Util.match(regExpr, str, 1, true);
		} 
		catch (MalformedPatternException e) {
			throw new FunctionException(pc,"REMatch",1,"regularExpression",e.getMessage());
		}
	}
}