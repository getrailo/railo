/**
 * Implements the CFML Function gettickcount
 */
package railo.runtime.functions.other;

import railo.runtime.PageContext;
import railo.runtime.exp.FunctionException;
import railo.runtime.ext.function.Function;

public final class GetTickCount implements Function {
	public static double call(PageContext pc) {
		return System.currentTimeMillis();
	}
	public static double call(PageContext pc,String unit) throws FunctionException {
		unit=unit.trim();
		if("nano".equalsIgnoreCase(unit))
			return System.nanoTime();
		else if("milli".equalsIgnoreCase(unit))
			return System.currentTimeMillis();
		else if("second".equalsIgnoreCase(unit))
			return System.currentTimeMillis()/1000;
		else 
			throw new FunctionException(pc, "GetTickCount", 1, "type", "invalid value ["+unit+"], valid values are (nano,milli,second)");
	}
}