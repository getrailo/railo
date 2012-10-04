/**
 * Implements the CFML Function yesnoformat
 */
package railo.runtime.functions.displayFormatting;

import railo.commons.lang.StringUtil;
import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.ext.function.Function;
import railo.runtime.op.Caster;

public final class TrueFalseFormat implements Function {
	public static String call(PageContext pc , Object object) throws PageException {
		if(object instanceof String) {
			Object str = object;
			if(StringUtil.isEmpty(str)) return Caster.toString(false);
		}
	    return Caster.toBooleanValue(object)?"true":"false";
	}
}