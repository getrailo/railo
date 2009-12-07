/**
 * Implements the Cold Fusion Function lsiscurrency
 */
package railo.runtime.functions.international;

import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.ext.function.Function;

public final class LSIsCurrency implements Function {
	public static boolean call(PageContext pc , String string) {
		try {
			LSParseCurrency.call(pc,string);
			return true;
		} catch (PageException e) {
			return false;
		}
	}
	public static boolean call(PageContext pc , String string,String strLocale) {
		try {
			LSParseCurrency.call(pc,string,strLocale);
			return true;
		} catch (PageException e) {
			return false;
		}
	}
}