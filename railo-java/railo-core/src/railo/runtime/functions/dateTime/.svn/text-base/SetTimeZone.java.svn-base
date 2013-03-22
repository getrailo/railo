package railo.runtime.functions.dateTime;

import java.util.TimeZone;

import railo.commons.date.TimeZoneUtil;
import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.ext.function.Function;

public class SetTimeZone implements Function{
	public static String call(PageContext pc,String strTimezone) throws PageException {
		TimeZone old = pc.getTimeZone();
		pc.setTimeZone(TimeZoneUtil.toTimeZone(strTimezone));
		return TimeZoneUtil.toString(old);
	}
		
}