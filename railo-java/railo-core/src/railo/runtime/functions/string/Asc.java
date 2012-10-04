/**
 * Implements the CFML Function asc
 */
package railo.runtime.functions.string;

import railo.runtime.PageContext;
import railo.runtime.ext.function.Function;

public final class Asc implements Function {
	public static double call(PageContext pc , String string) {
		if(string.length()==0)return 0;
		return string.toCharArray()[0];
	}
}