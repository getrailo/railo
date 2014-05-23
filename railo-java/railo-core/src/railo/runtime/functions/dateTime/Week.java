/**
 * Implements the CFML Function week
 */
package railo.runtime.functions.dateTime;

import java.util.Locale;
import java.util.TimeZone;

import railo.commons.date.DateTimeUtil;
import railo.runtime.PageContext;
import railo.runtime.ext.function.Function;
import railo.runtime.type.dt.DateTime;

public final class Week implements Function {
	
	public static double call(PageContext pc , DateTime date) {
		return _call(pc, date, pc.getTimeZone());
	}
	
	public static double call(PageContext pc , DateTime date, TimeZone tz) {
		return _call(pc, date, tz==null?pc.getTimeZone():tz);
	}
	
	private static double _call(PageContext pc , DateTime date,TimeZone tz) {
		return DateTimeUtil.getInstance().getWeekOfYear(Locale.US,tz, date);
	} 
}