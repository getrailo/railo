package railo.runtime.functions.dateTime;

import java.util.TimeZone;

import railo.commons.date.DateTimeUtil;
import railo.runtime.PageContext;
import railo.runtime.ext.function.Function;
import railo.runtime.type.dt.DateTime;

/**
 * Implements the CFML Function year
 */
public final class Year implements Function {

	public static double call(PageContext pc , DateTime date) {
		return DateTimeUtil.getInstance().getYear(pc.getTimeZone(),date);
	}
	
	public static double call(PageContext pc , DateTime date, TimeZone tz) {
		return DateTimeUtil.getInstance().getYear(tz==null?pc.getTimeZone():tz,date);
	}
}