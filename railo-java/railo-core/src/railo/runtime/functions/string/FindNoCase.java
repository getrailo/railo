/**
 * Implements the Cold Fusion Function findnocase
 */
package railo.runtime.functions.string;

import railo.runtime.PageContext;
import railo.runtime.ext.function.Function;

public final class FindNoCase implements Function {
	public static double call(PageContext pc , String sub, String str) {
		return Find.call(pc,sub.toLowerCase(),str.toLowerCase());
	}
	public static double call(PageContext pc , String sub, String str, double number) {
		return Find.call(pc,sub.toLowerCase(),str.toLowerCase(),number);
	}
}