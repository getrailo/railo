package railo.runtime.functions.system;

import railo.runtime.PageContext;
import railo.runtime.exp.ExpressionException;
import railo.runtime.ext.function.Function;
import railo.runtime.tag.util.DeprecatedUtil;

/**
 * Implements the CFML Function getmetricdata
 */
public final class GetMetricData implements Function {
	public static Object call(PageContext pc , String string) throws ExpressionException {
		DeprecatedUtil.function(pc,"getMetricData");
		throw new ExpressionException("function getMetricData is deprecated");
	}
}