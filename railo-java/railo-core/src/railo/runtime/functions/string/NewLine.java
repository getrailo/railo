/**
 * Implements the CFML Function chr
 */
package railo.runtime.functions.string;

import railo.runtime.PageContext;
import railo.runtime.ext.function.Function;

public final class NewLine implements Function {
	public static String call(PageContext pc) {
		return "\n";		
	}
}