package railo.runtime.functions.international;

import java.util.Locale;

import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.ext.function.Function;
import railo.runtime.i18n.LocaleFactory;

/**
 * Implements the Cold Fusion Function setlocale
 */
public final class SetLocale implements Function {
	public static String call(PageContext pc , String strLocale) throws PageException {
	       	Locale old=pc.getLocale();
            pc.setLocale(strLocale);
	       	return LocaleFactory.toString(old);
			
	}
	
	
}