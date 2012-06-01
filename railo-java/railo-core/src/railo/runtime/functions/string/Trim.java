/**
 * Implements the CFML Function trim
 */
package railo.runtime.functions.string;

import railo.runtime.PageContext;
import railo.runtime.ext.function.Function;

public final class Trim implements Function {
	public static String call(PageContext pc , String string) {
		return string.trim();
	}
}