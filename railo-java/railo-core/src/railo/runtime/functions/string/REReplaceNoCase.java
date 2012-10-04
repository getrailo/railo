/**
 * Implements the CFML Function rereplacenocase
 */
package railo.runtime.functions.string;

import org.apache.oro.text.regex.MalformedPatternException;

import railo.runtime.PageContext;
import railo.runtime.exp.ExpressionException;
import railo.runtime.exp.FunctionException;
import railo.runtime.ext.function.Function;
import railo.runtime.regex.Perl5Util;

public final class REReplaceNoCase implements Function {

	public static String call(PageContext pc , String string, String regExp, String replace) throws ExpressionException {
        try {
            return Perl5Util.replace(string,regExp,replace,false,false);
        } 
        catch (MalformedPatternException e) {
            throw new FunctionException(pc,"reReplaceNoCase",2,"regularExpression",e.getMessage());
        }
	}
    
	public static String call(PageContext pc , String string, String regExp, String replace, String scope) throws ExpressionException {
		try {
			if(scope.equalsIgnoreCase("all"))return Perl5Util.replace(string,regExp,replace,false,true);
			return Perl5Util.replace(string,regExp,replace,false,false);
		} 
		catch (MalformedPatternException e) {
			throw new FunctionException(pc,"reReplaceNoCase",2,"regularExpression",e.getMessage());
		}
	}
}