/**
 * Implements the CFML Function len
 */
package railo.runtime.functions.string;

import railo.runtime.PageContext;
import railo.runtime.ext.function.Function;

public final class StringLen implements Function {
	public static double call(PageContext pc , String string) {
		return string.length();
	}
}