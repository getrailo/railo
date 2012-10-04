/**
 * Implements the CFML Function decimalformat
 */
package railo.runtime.functions.displayFormatting;

import railo.commons.lang.StringUtil;
import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.ext.function.Function;
import railo.runtime.op.Caster;
import railo.runtime.op.Constants;

public final class DecimalFormat implements Function {
	
	private static final long serialVersionUID = -2287888250117784383L;

	/*
	 * @param pc
	 * @param object
	 * @return
	 * @throws ExpressionException
	 */
	public static String call(PageContext pc , Object object) throws PageException {
		if(StringUtil.isEmpty(object)) object=Constants.DOUBLE_ZERO;
		return Caster.toDecimal(object);
	}
}