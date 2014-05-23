package railo.runtime.functions.international;

import java.util.Locale;

import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.ext.function.Function;

/**
 * Implements the CFML Function lsparseeurocurrency
 */
public final class LSParseEuroCurrency implements Function {

	private static final long serialVersionUID = -4153683932862857234L;
	
	public static String call(PageContext pc , String string) throws PageException {
		return LSParseCurrency.call(pc,string);
	}
	public static String call(PageContext pc , String string, Locale locale) throws PageException {
		return LSParseCurrency.call(pc,string,locale);
	}
}