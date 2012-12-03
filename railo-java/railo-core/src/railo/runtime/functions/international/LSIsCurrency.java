/**
 * Implements the CFML Function lsiscurrency
 */
package railo.runtime.functions.international;

import railo.runtime.PageContext;
import railo.runtime.ext.function.Function;
import railo.runtime.i18n.LocaleFactory;

public final class LSIsCurrency implements Function {

	private static final long serialVersionUID = -8659567712610988769L;

	public static boolean call(PageContext pc , String string) {
		try {
			LSParseCurrency.toDoubleValue(pc.getLocale(),string,true);
			return true;
		} catch (Throwable t) {
			return false;
		}		
	}
	public static boolean call(PageContext pc , String string,String strLocale) {
		try {
			LSParseCurrency.toDoubleValue(LocaleFactory.getLocale(strLocale),string,false);
			return true;
		} catch (Throwable t) {
			return false;
		}
	}
}