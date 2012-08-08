package railo.runtime.functions.dynamicEvaluation;

import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.ext.function.Function;

/**
 * Implements the CFML Function evaluate
 */
public final class PrecisionEvaluate implements Function {

	
	public static Object call(PageContext pc , Object[] objs) throws PageException {
		
		return Evaluate.call(pc, objs,true);
	}

}