/**
 * Implements the CFML Function gettickcount
 */
package railo.runtime.functions.other;

import railo.commons.lang.StringUtil;
import railo.runtime.PageContext;
import railo.runtime.exp.FunctionException;
import railo.runtime.ext.function.Function;

public final class GetTickCount implements Function {

	public static double call(PageContext pc) {
		return System.currentTimeMillis();
	}

	public static double call(PageContext pc,String unit) throws FunctionException {

        if ( !StringUtil.isEmpty( unit ) ) {

            char c = unit.charAt( 0 );

            if ( c == 'n' || c == 'N' )
                return System.nanoTime();
            else if ( c == 'm' || c == 'M' )
                return System.currentTimeMillis();
            else if ( c == 's' || c == 'S' )
                return System.currentTimeMillis()/1000;
        }

		throw new FunctionException(pc, "GetTickCount", 1, "unit", "invalid value ["+unit+"], valid values are (nano, milli, second)");
	}
}