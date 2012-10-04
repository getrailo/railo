/**
 * Implements the CFML Function findnocase
 */
package railo.runtime.functions.string;

import railo.commons.lang.StringUtil;
import railo.runtime.PageContext;
import railo.runtime.ext.function.Function;

public final class FindNoCase implements Function {

	public static double call(PageContext pc , String sub, String str) {
		return StringUtil.indexOfIgnoreCase(str, sub)+1;
	}
	
	public static double call(PageContext pc , String sub, String str, double number) {
		return Find.call(pc,sub.toLowerCase(),str.toLowerCase(),number);
	}
}