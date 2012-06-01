/**
 * Implements the CFML Function preservesinglequotes
 */
package railo.runtime.functions.other;

import railo.runtime.PageContext;
import railo.runtime.ext.function.Function;

public final class PreserveSingleQuotes implements Function {
	public static String call(PageContext pc , String string) {
		//print.ln("PreserveSingleQuotes:"+string);
		return string;
	}
}