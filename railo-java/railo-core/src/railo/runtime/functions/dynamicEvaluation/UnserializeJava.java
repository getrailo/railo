/**
 * Implements the CFML Function UnserializeJava
 */
package railo.runtime.functions.dynamicEvaluation;

import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.ext.function.Function;
import railo.runtime.tag.util.DeprecatedUtil;

/**
 * @deprecated use instead EvaluateJava
 */
public final class UnserializeJava implements Function {
	public static Object call(PageContext pc , String string) throws PageException {
		DeprecatedUtil.function(pc,"UnserializeJava","EvaluateJava");
	    return EvaluateJava.call(pc, string);
	}
}