/**
 * Implements the CFML Function ltrim
 */
package railo.runtime.functions.string;

import railo.commons.lang.StringUtil;
import railo.runtime.PageContext;
import railo.runtime.ext.function.Function;

public final class LTrim implements Function {
	public static String call(PageContext pc , String str) {
		return StringUtil.ltrim(str,"");
	}
}