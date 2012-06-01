/**
 * Implements the CFML Function createdatetime
 */
package railo.runtime.functions.dateTime;

import java.util.TimeZone;

import railo.commons.date.DateTimeUtil;
import railo.commons.date.TimeZoneUtil;
import railo.runtime.PageContext;
import railo.runtime.exp.ExpressionException;
import railo.runtime.ext.function.Function;
import railo.runtime.type.dt.DateTime;

public final class CreateDateTime implements Function {

	public static DateTime call(PageContext pc , double year, double month, double day, double hour, double minute, double second) throws ExpressionException {
		return _call(pc,year,month,day,hour,minute,second,0,pc.getTimeZone());
	}
	public static DateTime call(PageContext pc , double year, double month, double day, double hour, double minute, double second,double millis) throws ExpressionException {
		return _call(pc,year,month,day,hour,minute,second,millis,pc.getTimeZone());
	}
	public static DateTime call(PageContext pc , double year, double month, double day, double hour, double minute, double second,double millis,String strTimezone) throws ExpressionException {
		return _call(pc,year,month,day,hour,minute,second,millis,strTimezone==null?pc.getTimeZone():TimeZoneUtil.toTimeZone(strTimezone));
	}
	private static DateTime _call(PageContext pc , double year, double month, double day, double hour, double minute, double second,double millis,TimeZone tz) throws ExpressionException {
		return DateTimeUtil.getInstance().toDateTime(tz,(int)year,(int)month,(int)day,(int)hour,(int)minute,(int)second,(int)millis);
	}
}