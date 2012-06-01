package railo.runtime.functions.international;

import java.util.Locale;

import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.ext.function.Function;
import railo.runtime.i18n.LocaleFactory;

/**
 * Implements the CFML Function setlocale
 */
public final class SetLocale implements Function {
	
	private static final long serialVersionUID = -4941933470300726563L;

	public static String call(PageContext pc , String strLocale) throws PageException {
	       	Locale old=pc.getLocale();
	       	pc.setLocale(LocaleFactory.getLocale(strLocale));
	       	return LocaleFactory.toString(old);
			
	}
}