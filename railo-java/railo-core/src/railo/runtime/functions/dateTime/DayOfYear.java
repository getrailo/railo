/**
 * Implements the CFML Function dayofyear
 */
package railo.runtime.functions.dateTime;

import java.util.Locale;
import java.util.TimeZone;

import railo.commons.date.DateTimeUtil;
import railo.commons.date.TimeZoneUtil;
import railo.runtime.PageContext;
import railo.runtime.exp.ExpressionException;
import railo.runtime.exp.PageException;
import railo.runtime.functions.BIF;
import railo.runtime.op.Caster;
import railo.runtime.type.dt.DateTime;

public final class DayOfYear extends BIF {

	private static final long serialVersionUID = 8136302798735384757L;

	public static double call(PageContext pc , DateTime date) {
		return _call(pc, date, pc.getTimeZone());
	}
	
	public static double call(PageContext pc , DateTime date, String strTimezone) throws ExpressionException {
		return _call(pc, date, strTimezone==null?pc.getTimeZone():TimeZoneUtil.toTimeZone(strTimezone));
	}
	
	private static double _call(PageContext pc , DateTime date,TimeZone tz) {
		return DateTimeUtil.getInstance().getDayOfYear(Locale.US,tz, date);
	}
	
	@Override
	public Object invoke(PageContext pc, Object[] args) throws PageException {
		if(args.length==1)return call(pc,Caster.toDatetime(args[0],pc.getTimeZone()));
		return call(pc,Caster.toDatetime(args[0],pc.getTimeZone()),Caster.toString(args[1]));
	}
}