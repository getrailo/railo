/**
 * Implements the CFML Function compare
 */
package railo.runtime.functions.string;

import railo.runtime.PageContext;
import railo.runtime.ext.function.Function;

public final class Compare implements Function {
	public static double call(PageContext pc , String str1, String str2) {
		int compare=str1.compareTo(str2);
		if(compare==0) return 0;
		return compare>0?1:-1;
	}
}