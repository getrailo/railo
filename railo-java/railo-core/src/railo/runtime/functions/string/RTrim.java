/**
 * Implements the CFML Function rtrim
 */
package railo.runtime.functions.string;

import railo.commons.lang.StringUtil;
import railo.runtime.PageContext;
import railo.runtime.ext.function.Function;

public final class RTrim implements Function {
	public static String call(PageContext pc , String str) {
		return StringUtil.rtrim(str,"");
	}
}