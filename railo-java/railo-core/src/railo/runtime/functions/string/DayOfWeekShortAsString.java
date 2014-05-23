/**
 * Implements the CFML Function dayofweekasstring
 */
package railo.runtime.functions.string;

import java.util.Locale;

import railo.runtime.PageContext;
import railo.runtime.exp.ExpressionException;
import railo.runtime.ext.function.Function;

public final class DayOfWeekShortAsString implements Function {
	
	public static String call(PageContext pc , double dow) throws ExpressionException {
		return DayOfWeekAsString.call(pc,dow, pc.getLocale(),false);
	}
	
	public static String call(PageContext pc , double dow, Locale locale) throws ExpressionException {
		return DayOfWeekAsString.call(pc,dow, locale==null?pc.getLocale():locale,false);
	}
	
}