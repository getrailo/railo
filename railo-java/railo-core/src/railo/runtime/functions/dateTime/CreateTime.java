/**
 * Implements the CFML Function createtime
 */
package railo.runtime.functions.dateTime;

import java.util.TimeZone;

import railo.commons.date.DateTimeUtil;
import railo.runtime.PageContext;
import railo.runtime.engine.ThreadLocalPageContext;
import railo.runtime.ext.function.Function;
import railo.runtime.type.dt.DateTime;
import railo.runtime.type.dt.TimeImpl;

public final class CreateTime implements Function {

	private static final long serialVersionUID = -5887770689991548576L;

	public static DateTime call(PageContext pc , double hour, double minute, double second) {
		return _call(pc, hour, minute, second, 0,pc.getTimeZone());
	}

	public static DateTime call(PageContext pc , double hour, double minute, double second,double millis) {
		return _call(pc, hour, minute, second, millis,pc.getTimeZone());
	}
	public static DateTime call(PageContext pc , double hour, double minute, double second,double millis, TimeZone tz) {
		return _call(pc, hour, minute, second, millis,tz==null?pc.getTimeZone():tz);
	}
	

	private static DateTime _call(PageContext pc , double hour, double minute, double second,double millis,TimeZone tz) {
		// TODO check this looks wrong
		if(tz==null)tz=ThreadLocalPageContext.getTimeZone(pc);
		return new TimeImpl(
				DateTimeUtil.getInstance().toTime(tz,1899,12,30,(int)hour,(int)minute,(int)second,(int)millis,0)
				,false);
	}
}