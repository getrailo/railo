/**
 * Implements the CFML Function getlocale
 */
package railo.runtime.functions.international;

import railo.runtime.PageContext;
import railo.runtime.ext.function.Function;
import railo.runtime.i18n.LocaleFactory;

public final class GetLocale implements Function {
	public static String call(PageContext pc ) {
		return LocaleFactory.toString(pc.getLocale());
	}
	
}