/**
 * Implements the CFML Function formatbasen
 */
package railo.runtime.functions.displayFormatting;

import java.util.Locale;

import railo.commons.lang.StringUtil;
import railo.runtime.PageContext;
import railo.runtime.exp.ExpressionException;
import railo.runtime.ext.function.Function;
import railo.runtime.op.Caster;

public final class GetLocaleDisplayName implements Function {
	public static String call(PageContext pc) {
		return _call(pc.getLocale(), pc.getLocale());
	}
	
	public static String call(PageContext pc , String locale) throws ExpressionException {
		Locale l = Caster.toLocale(locale);
		return _call(l, l);
	}
	
	public static String call(PageContext pc , String locale, String dspLocale) throws ExpressionException {
		if(StringUtil.isEmpty(dspLocale))dspLocale=locale;
		return _call(Caster.toLocale(locale), Caster.toLocale(dspLocale));
	}
	
	private static String _call(Locale locale, Locale dspLocale) {
		return locale.getDisplayName(dspLocale);
	}

	
	
}