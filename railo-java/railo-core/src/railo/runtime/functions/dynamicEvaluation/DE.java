/**
 * Implements the CFML Function de
 */
package railo.runtime.functions.dynamicEvaluation;

import railo.runtime.PageContext;
import railo.runtime.exp.ExpressionException;
import railo.runtime.ext.function.Function;
import railo.runtime.functions.string.Replace;

public final class DE implements Function {
	public static String call(PageContext pc , String string) throws ExpressionException {
		return new StringBuffer().append('"').append(Replace.call(pc,string,"\"","\"\"","all")).append('"').toString();
		
	}
}