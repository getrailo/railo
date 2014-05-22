package railo.runtime.functions.international;

import java.util.Locale;

import railo.runtime.PageContext;
import railo.runtime.ext.function.Function;

/**
 * Implements the CFML Function setlocale
 */
public final class SetLocale implements Function {
	
	private static final long serialVersionUID = -4941933470300726563L;

	public static Locale call(PageContext pc , Locale locale) {
	       	Locale old=pc.getLocale();
	       	pc.setLocale(locale);
	       	return old;
			
	}
}