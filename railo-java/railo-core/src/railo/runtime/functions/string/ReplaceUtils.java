/**
 * Implements the CFML Function replace
 */
package railo.runtime.functions.string;

import railo.runtime.exp.ExpressionException;
import railo.runtime.exp.PageException;
import railo.runtime.op.Caster;

public final class ReplaceUtils{
	
	public static Object convertToStringIfNumber(Object o) throws ExpressionException {
		if (o instanceof Number) {
			try {
				o = Caster.toString(o);
			}
			catch (PageException e) {
				throw new ExpressionException("When passing three parameters or more, the second parameter must be a String.");
			}
		}
		return o;
	}

}