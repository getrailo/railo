/**
 * Implements the Cold Fusion Function dayofweekasstring
 */
package railo.runtime.functions.string;

import railo.runtime.PageContext;
import railo.runtime.exp.ExpressionException;
import railo.runtime.ext.function.Function;
import railo.runtime.i18n.LocaleFactory;

public final class DayOfWeekShortAsString implements Function {
	
	public static String call(PageContext pc , double dow) throws ExpressionException {
		return DayOfWeekAsString.call(pc,dow, pc.getLocale(),false);
	}
	
	public static String call(PageContext pc , double dow, String strLocale) throws ExpressionException {
		return DayOfWeekAsString.call(pc,dow, LocaleFactory.getLocale(strLocale),false);
	}
	
}