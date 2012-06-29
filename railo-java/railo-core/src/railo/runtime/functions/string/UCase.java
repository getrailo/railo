/**
 * Implements the CFML Function ucase
 */
package railo.runtime.functions.string;

import railo.runtime.PageContext;
import railo.runtime.ext.function.Function;

public final class UCase implements Function {
	public static String call(PageContext pc , String string) {
		return string.toUpperCase();
	}
}