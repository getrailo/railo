/**
 * Implements the CFML Function createtimespan
 */
package railo.runtime.functions.dateTime;

import railo.runtime.PageContext;
import railo.runtime.ext.function.Function;
import railo.runtime.type.dt.TimeSpan;
import railo.runtime.type.dt.TimeSpanImpl;

public final class CreateTimeSpan implements Function {
	public static TimeSpan call(PageContext pc , double day, double hour, double minute, double second) {
		return new TimeSpanImpl((int)day,(int)hour,(int)minute,(int)second);
	}
	
	public static TimeSpan call(PageContext pc , double day, double hour, double minute, double second,double millisecond) {
		return new TimeSpanImpl((int)day,(int)hour,(int)minute,(int)second,(int)millisecond);
	}
}