/**
 * Implements the Cold Fusion Function decimalformat
 */
package railo.runtime.functions.displayFormatting;

import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.ext.function.Function;
import railo.runtime.op.Caster;

public final class DecimalFormat implements Function {
	/*
	 * @param pc
	 * @param object
	 * @return
	 * @throws ExpressionException
	 */
	public static String call(PageContext pc , Object object) throws PageException {
		return Caster.toDecimal(object);
	}
}