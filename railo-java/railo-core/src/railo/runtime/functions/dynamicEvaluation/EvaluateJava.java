/**
 * Implements the CFML Function UnserializeJava
 */
package railo.runtime.functions.dynamicEvaluation;

import railo.runtime.PageContext;
import railo.runtime.converter.JavaConverter;
import railo.runtime.exp.PageException;
import railo.runtime.ext.function.Function;
import railo.runtime.op.Caster;

public final class EvaluateJava implements Function {
	public static Object call(PageContext pc , String string) throws PageException {
	    try {
            return JavaConverter.deserialize(string);
        } catch (Exception e) {
            throw Caster.toPageException(e);
        }
	}
}