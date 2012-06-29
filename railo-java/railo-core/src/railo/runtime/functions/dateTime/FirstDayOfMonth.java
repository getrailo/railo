/**
 * Implements the CFML Function firstdayofmonth
 */
package railo.runtime.functions.dateTime;

import java.util.Calendar;
import java.util.TimeZone;

import railo.commons.date.JREDateTimeUtil;
import railo.commons.date.TimeZoneUtil;
import railo.runtime.PageContext;
import railo.runtime.exp.ExpressionException;
import railo.runtime.ext.function.Function;
import railo.runtime.type.dt.DateTime;

public final class FirstDayOfMonth implements Function {
	
	public static double call(PageContext pc , DateTime date) {
		return _call(pc, date, pc.getTimeZone());
	}
	
	public static double call(PageContext pc , DateTime date, String strTimezone) throws ExpressionException {
		return _call(pc, date, strTimezone==null?pc.getTimeZone():TimeZoneUtil.toTimeZone(strTimezone));
	}
	
	private static double _call(PageContext pc , DateTime date,TimeZone tz) {
        //synchronized (calendar) {
		Calendar calendar=JREDateTimeUtil.getCalendar();
    	calendar.clear();
    	calendar.setTimeZone(tz);   
		calendar.setTime(date);   
		calendar.set(Calendar.DATE,1);         
		return calendar.get(Calendar.DAY_OF_YEAR);
        //}
	}
}