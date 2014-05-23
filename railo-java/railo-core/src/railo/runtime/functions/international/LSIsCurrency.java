/**
 * Implements the CFML Function lsiscurrency
 */
package railo.runtime.functions.international;

import java.util.Locale;

import railo.runtime.PageContext;
import railo.runtime.ext.function.Function;

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
	public static boolean call(PageContext pc , String string,Locale locale) {
		try {
			LSParseCurrency.toDoubleValue(locale==null?pc.getLocale():locale,string,false);
			return true;
		} catch (Throwable t) {
			return false;
		}
	}
}