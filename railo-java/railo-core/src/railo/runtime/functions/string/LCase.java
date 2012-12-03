/**
 * Implements the CFML Function lcase
 */
package railo.runtime.functions.string;

import railo.commons.lang.StringUtil;
import railo.runtime.PageContext;
import railo.runtime.ext.function.Function;

public final class LCase implements Function {
	public static String call(PageContext pc , String string) {
        return StringUtil.toLowerCase(string);
    }
}