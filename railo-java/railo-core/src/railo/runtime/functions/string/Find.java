/**
 * Implements the CFML Function find
 */
package railo.runtime.functions.string;

import railo.runtime.PageContext;
import railo.runtime.ext.function.Function;

public final class Find implements Function {
	public static double call(PageContext pc , String sub, String str) {
		return str.indexOf(sub)+1;
	}
	public static double call(PageContext pc , String sub, String str, double number) {
		if(sub.length()==0) return (int) number;
		return str.indexOf(sub,(int)number-1)+1;
	}
}