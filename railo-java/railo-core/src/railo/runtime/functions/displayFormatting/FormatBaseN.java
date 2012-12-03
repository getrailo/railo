/**
 * Implements the CFML Function formatbasen
 */
package railo.runtime.functions.displayFormatting;

import railo.runtime.PageContext;
import railo.runtime.exp.ExpressionException;
import railo.runtime.exp.FunctionException;
import railo.runtime.ext.function.Function;

public final class FormatBaseN implements Function {
	public static String call(PageContext pc , double number, double radix) throws ExpressionException {
		if(radix<2 || radix>36)
			throw new FunctionException(pc,"formatBaseN",2,"radix","radix must be between 2 an 36");
		return Long.toString((int)number & 0xffffffffL,(int)radix);
	}
    
}