/**
 * Implements the CFML Function createdate
 */
package railo.runtime.functions.dateTime;

import java.util.TimeZone;

import railo.commons.date.DateTimeUtil;
import railo.commons.date.TimeZoneUtil;
import railo.runtime.PageContext;
import railo.runtime.exp.ExpressionException;
import railo.runtime.ext.function.Function;
import railo.runtime.type.dt.DateTime;

public final class CreateDate implements Function {
	public static DateTime call(PageContext pc , double year, double month, double day) throws ExpressionException {
		return _call(pc,year,month,day,pc.getTimeZone());
	}
	public static DateTime call(PageContext pc , double year, double month, double day,String strTimezone) throws ExpressionException {
		return _call(pc,year,month,day,strTimezone==null?pc.getTimeZone():TimeZoneUtil.toTimeZone(strTimezone));
	}
	private static DateTime _call(PageContext pc , double year, double month, double day,TimeZone tz) throws ExpressionException {
		return DateTimeUtil.getInstance().toDateTime(tz,(int)year,(int)month,(int)day, 0, 0, 0,0);
	}
}