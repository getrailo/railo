/**
 * Implements the CFML Function gettickcount
 */
package railo.runtime.functions.other;

import railo.commons.lang.StringUtil;
import railo.runtime.PageContext;
import railo.runtime.exp.FunctionException;
import railo.runtime.ext.function.Function;

public final class GetTickCount implements Function {

	private static final long serialVersionUID = 678332662578928144L;

	public static double UNIT_NANO=1;
	public static double UNIT_MILLI=2;
	public static double UNIT_MICRO=4;
	public static double UNIT_SECOND=8;
	
	public static double call(PageContext pc) {
		return System.currentTimeMillis();
	}
	public static double call(PageContext pc,String unit) throws FunctionException {
		unit=unit.trim();
		if (!StringUtil.isEmpty( unit )) {
            char c = unit.charAt( 0 );

            if ( c == 'n' || c == 'N' )
                return System.nanoTime();
            else if ( c == 'm' || c == 'M' ) {
            	if("micro".equalsIgnoreCase(unit)) return System.nanoTime()/1000;
        		return System.currentTimeMillis();
            }
            else if ( c == 's' || c == 'S' )
                return System.currentTimeMillis()/1000;
        }

		throw new FunctionException(pc, "GetTickCount", 1, "unit", "invalid value ["+unit+"], valid values are (nano, micro, milli, second)");
	}

	
	// this function is only called when the evaluator validates the unit defintion on compilation time
	public static double call(PageContext pc,double unit) {
		if(UNIT_NANO==unit) return System.nanoTime();
		if(UNIT_MICRO==unit) return System.nanoTime()/1000;
		if(UNIT_MILLI==unit) return System.currentTimeMillis();
		return System.currentTimeMillis()/1000;
	}
}