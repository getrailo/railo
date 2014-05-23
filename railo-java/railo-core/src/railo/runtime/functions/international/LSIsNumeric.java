/**
 * Implements the CFML Function lsiscurrency
 */
package railo.runtime.functions.international;

import java.util.Locale;

import railo.runtime.PageContext;
import railo.runtime.ext.function.Function;

public final class LSIsNumeric implements Function {

	private static final long serialVersionUID = 4753476752482915194L;
	
	public static boolean call(PageContext pc , String string) {
		return call(pc, string, null);
	}
	
	public static boolean call(PageContext pc , String string,Locale locale) {
		try {
			LSParseNumber.call(pc,string,locale);
			return true;
		} catch (Throwable t) {
			return false;
		}
	}
}