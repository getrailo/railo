/**
 * Implements the CFML Function formatbasen
 */
package railo.runtime.functions.displayFormatting;

import java.util.Locale;

import railo.runtime.PageContext;
import railo.runtime.ext.function.Function;

public final class GetLocaleLanguage implements Function {

	private static final long serialVersionUID = -4084704416496042957L;

	public static String call(PageContext pc) {
		return _call(pc,pc.getLocale(), pc.getLocale());
	}
	
	public static String call(PageContext pc , Locale locale) {
		return _call(pc,locale, locale);
	}
	
	public static String call(PageContext pc , Locale locale, Locale dspLocale) {
		return _call(pc,locale, dspLocale);
	}
	
	private static String _call(PageContext pc , Locale locale, Locale dspLocale) {
		if(locale==null) locale=pc.getLocale();
		if(dspLocale==null) dspLocale=locale;
		return locale.getDisplayLanguage(dspLocale);
	}

	
	
}