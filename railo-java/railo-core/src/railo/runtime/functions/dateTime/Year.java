package railo.runtime.functions.dateTime;

import railo.commons.date.DateTimeUtil;
import railo.commons.date.TimeZoneUtil;
import railo.runtime.PageContext;
import railo.runtime.exp.ExpressionException;
import railo.runtime.ext.function.Function;
import railo.runtime.type.dt.DateTime;

/**
 * Implements the CFML Function year
 */
public final class Year implements Function {

	public static double call(PageContext pc , DateTime date) {
		return DateTimeUtil.getInstance().getYear(pc.getTimeZone(),date);
	}
	
	public static double call(PageContext pc , DateTime date, String strTimezone) throws ExpressionException {
		return DateTimeUtil.getInstance().getYear(strTimezone==null?pc.getTimeZone():TimeZoneUtil.toTimeZone(strTimezone),date);
	}
}