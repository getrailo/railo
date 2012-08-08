/**
 * Implements the CFML Function lsiscurrency
 */
package railo.runtime.functions.international;

import railo.runtime.PageContext;
import railo.runtime.ext.function.Function;

public final class LSIsNumeric implements Function {

	private static final long serialVersionUID = 4753476752482915194L;
	
	public static boolean call(PageContext pc , String string) {
		return call(pc, string, null);
	}
	
	public static boolean call(PageContext pc , String string,String strLocale) {
		try {
			LSParseNumber.call(pc,string,strLocale);
			return true;
		} catch (Throwable t) {
			return false;
		}
	}
}