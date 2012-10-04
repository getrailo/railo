/**
 * Implements the CFML Function iif
 */
package railo.runtime.functions.dynamicEvaluation;

import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.ext.function.Function;

public final class IIf implements Function {
	public static Object call(PageContext pc , boolean bool, String str1, String str2) throws PageException {
		return bool?Evaluate.call(pc,new Object[]{str1}):Evaluate.call(pc,new Object[]{str2});
	}
}