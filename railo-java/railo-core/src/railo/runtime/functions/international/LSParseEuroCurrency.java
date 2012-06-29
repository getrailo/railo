package railo.runtime.functions.international;

import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.ext.function.Function;

/**
 * Implements the CFML Function lsparseeurocurrency
 */
public final class LSParseEuroCurrency implements Function {
	public static String call(PageContext pc , String string) throws PageException {
		return LSParseCurrency.call(pc,string);
	}
	public static String call(PageContext pc , String string,String strLocale) throws PageException {
		return LSParseCurrency.call(pc,string,strLocale);
	}
}