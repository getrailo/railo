/**
 * Implements the CFML Function asc
 */
package railo.runtime.functions.string;

import railo.commons.lang.StringUtil;
import railo.runtime.PageContext;
import railo.runtime.ext.function.Function;

public final class UCFirst implements Function {

	private static final long serialVersionUID = 6476775359884698477L;

	public static String call(PageContext pc , String string) {
        return call(pc, string,false);
    }

    public static String call( PageContext pc, String string, boolean doAll ) {
        if ( !doAll ) return StringUtil.ucFirst(string);
        return StringUtil.capitalize( string, null );
    }
}