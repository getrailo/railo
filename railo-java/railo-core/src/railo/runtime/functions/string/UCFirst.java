/**
 * Implements the CFML Function asc
 */
package railo.runtime.functions.string;

import railo.commons.lang.StringUtil;
import railo.runtime.PageContext;
import railo.runtime.ext.function.Function;

public final class UCFirst implements Function {
    public static String call(PageContext pc , String string) {
        return StringUtil.ucFirst(string);
    }

    public static String call( PageContext pc, String string, boolean doAll ) {

        if ( !doAll )
            return call( pc, string );

        return StringUtil.capitalize( string, null );
    }
}