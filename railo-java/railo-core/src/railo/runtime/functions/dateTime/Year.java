package railo.runtime.functions.dateTime;

import java.util.Calendar;
import java.util.TimeZone;

import railo.commons.date.TimeZoneUtil;
import railo.runtime.PageContext;
import railo.runtime.exp.ExpressionException;
import railo.runtime.ext.function.Function;
import railo.runtime.type.dt.DateTime;

/**
 * Implements the Cold Fusion Function year
 */
public final class Year implements Function {

	private static Calendar calendar;

	public static double call(PageContext pc , DateTime date) {
		return invoke(date, pc.getTimeZone());
	}
	
	public static double call(PageContext pc , DateTime date, String strTimezone) throws ExpressionException {
		return invoke(date, TimeZoneUtil.toTimeZone(strTimezone));
	}
	
	public static int invoke(DateTime date,TimeZone tz) {
        
		if (calendar == null)
        	calendar=Calendar.getInstance();
        synchronized (calendar) {
        	calendar.clear();
        	DateUtil.setTimeZone(calendar,date,tz);      
    		calendar.setTime(date);         
    		return calendar.get(Calendar.YEAR);
        }
	}
}