/**
 * Implements the CFML Function getlocale
 */
package railo.runtime.functions.international;

import java.util.Locale;

import railo.runtime.PageContext;
import railo.runtime.ext.function.Function;

public final class GetLocale implements Function {
	public static Locale call(PageContext pc ) {
		return pc.getLocale();
	}
	
}