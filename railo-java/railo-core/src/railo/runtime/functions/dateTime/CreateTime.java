/**
 * Implements the CFML Function createtime
 */
package railo.runtime.functions.dateTime;

import java.util.TimeZone;

import railo.commons.date.DateTimeUtil;
import railo.commons.date.TimeZoneUtil;
import railo.runtime.PageContext;
import railo.runtime.engine.ThreadLocalPageContext;
import railo.runtime.exp.ExpressionException;
import railo.runtime.ext.function.Function;
import railo.runtime.type.dt.DateTime;
import railo.runtime.type.dt.TimeImpl;

public final class CreateTime implements Function {
	public static DateTime call(PageContext pc , double hour, double minute, double second) {
		return _call(pc, hour, minute, second, 0,pc.getTimeZone());
	}

	public static DateTime call(PageContext pc , double hour, double minute, double second,double millis) {
		return _call(pc, hour, minute, second, millis,pc.getTimeZone());
	}
	public static DateTime call(PageContext pc , double hour, double minute, double second,double millis,String strTimezone) throws ExpressionException {
		return _call(pc, hour, minute, second, millis,strTimezone==null?pc.getTimeZone():TimeZoneUtil.toTimeZone(strTimezone));
	}
	

	private static DateTime _call(PageContext pc , double hour, double minute, double second,double millis,TimeZone tz) {
		// TODO check this looks wrong
		if(tz==null)tz=ThreadLocalPageContext.getTimeZone(pc);
		return new TimeImpl(
				DateTimeUtil.getInstance().toTime(tz,1899,12,30,(int)hour,(int)minute,(int)second,(int)millis,0)
				,false);
	}
}