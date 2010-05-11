/**
 * Implements the Cold Fusion Function dayofyear
 */
package railo.runtime.functions.dateTime;

import java.util.Locale;
import java.util.TimeZone;

import railo.commons.date.DateTimeUtil;
import railo.commons.date.TimeZoneUtil;
import railo.runtime.PageContext;
import railo.runtime.exp.ExpressionException;
import railo.runtime.ext.function.Function;
import railo.runtime.type.dt.DateTime;

public final class DayOfYear implements Function {
	
	public static double call(PageContext pc , DateTime date) {
		return _call(pc, date, pc.getTimeZone());
	}
	
	public static double call(PageContext pc , DateTime date, String strTimezone) throws ExpressionException {
		return _call(pc, date, TimeZoneUtil.toTimeZone(strTimezone));
	}
	
	private static double _call(PageContext pc , DateTime date,TimeZone tz) {
		return DateTimeUtil.getInstance().getDayOfYear(Locale.US,tz, date);
	}
}