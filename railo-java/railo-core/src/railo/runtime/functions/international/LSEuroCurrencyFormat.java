/**
 * Implements the CFML Function lseurocurrencyformat
 */
package railo.runtime.functions.international;

import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.ext.function.Function;

public final class LSEuroCurrencyFormat implements Function { 

	private static final long serialVersionUID = -9214893090412056842L;
	public static String call(PageContext pc , Object number) throws PageException {
		return LSCurrencyFormat.call(pc,number);
	}
	public static String call(PageContext pc , Object number, String type) throws PageException {
		return LSCurrencyFormat.call(pc,number,type);
	}
	public static String call(PageContext pc , Object number, String type,String locale) throws PageException {
		return LSCurrencyFormat.call(pc,number,type,locale);
	}
}