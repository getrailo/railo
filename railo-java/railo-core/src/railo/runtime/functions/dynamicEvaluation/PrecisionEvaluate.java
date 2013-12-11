package railo.runtime.functions.dynamicEvaluation;

import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.ext.function.Function;
import railo.runtime.op.Caster;
import railo.runtime.op.Decision;

/**
 * Implements the CFML Function evaluate
 */
public final class PrecisionEvaluate implements Function {

	
	public static Object call(PageContext pc , Object[] objs) throws PageException {

		for (int i=objs.length-1; i>=0; i--)
			if (Decision.isNumeric( objs[i] ))
				objs[i] = Caster.toString(objs[i]);

		return Evaluate.call(pc, objs, true);
	}

}