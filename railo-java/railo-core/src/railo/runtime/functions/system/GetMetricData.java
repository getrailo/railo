package railo.runtime.functions.system;

import railo.runtime.PageContext;
import railo.runtime.exp.ExpressionException;
import railo.runtime.ext.function.Function;

/**
 * Implements the Cold Fusion Function getmetricdata
 */
public final class GetMetricData implements Function {
	public static Object call(PageContext pc , String string) throws ExpressionException {
		throw new ExpressionException("function getMetricData is deprecated");
	}
}