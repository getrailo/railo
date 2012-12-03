/**
 * Implements the CFML Function reverse
 */
package railo.runtime.functions.string;

import railo.runtime.PageContext;
import railo.runtime.ext.function.Function;

public final class Reverse implements Function {
	public static String call(PageContext pc , String string) {
		return new StringBuilder(string).reverse().toString();
	}
}