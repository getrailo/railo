/**
 * Implements the CFML Function createodbcdate
 */
package railo.runtime.functions.dateTime;

import java.util.Calendar;
import java.util.TimeZone;

import railo.commons.date.TimeZoneUtil;
import railo.commons.lang.StringUtil;
import railo.runtime.PageContext;
import railo.runtime.PageContextImpl;
import railo.runtime.exp.ExpressionException;
import railo.runtime.ext.function.Function;
import railo.runtime.type.dt.DateImpl;
import railo.runtime.type.dt.DateTime;

public final class CreateODBCDate implements Function {
	public static DateTime call(PageContext pc , DateTime datetime) throws ExpressionException {
		return call(pc, datetime,null);
	}
	public static DateTime call(PageContext pc , DateTime datetime, String strTimezone) throws ExpressionException {
		TimeZone tz;
		if(StringUtil.isEmpty(strTimezone)) tz=((PageContextImpl)pc).getTimeZone();
		else tz=TimeZoneUtil.toTimeZone(strTimezone);
		Calendar c = Calendar.getInstance(tz);
		c.setTime(datetime);
		c.set(Calendar.HOUR, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);
		return new DateImpl(c.getTime());
	}
}