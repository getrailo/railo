/**
 * Implements the Cold Fusion Function UnserializeJava
 */
package railo.runtime.functions.dynamicEvaluation;

import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.ext.function.Function;

/**
 * @deprecated use instead EvaluateJava
 */
public final class UnserializeJava implements Function {
	public static Object call(PageContext pc , String string) throws PageException {
	    return EvaluateJava.call(pc, string);
	}
}