/**
 * Implements the CFML Function second
 */
package railo.runtime.functions.dateTime;

import java.util.TimeZone;

import railo.commons.date.DateTimeUtil;
import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.functions.BIF;
import railo.runtime.op.Caster;
import railo.runtime.type.dt.DateTime;

public final class MilliSecond extends BIF {

	private static final long serialVersionUID = -7897327551736295220L;

	public static double call(PageContext pc , DateTime date) {
		return _call(pc, date, pc.getTimeZone());
	}
	
	public static double call(PageContext pc , DateTime date, TimeZone tz) {
		return _call(pc, date, tz==null?pc.getTimeZone():tz);
	}
	
	private static double _call(PageContext pc , DateTime date,TimeZone tz) {
		return DateTimeUtil.getInstance().getMilliSecond(tz, date);
	}
	
	@Override
	public Object invoke(PageContext pc, Object[] args) throws PageException {
		if(args.length==1)return call(pc,Caster.toDatetime(args[0],pc.getTimeZone()));
		return call(pc,Caster.toDatetime(args[0],pc.getTimeZone()),Caster.toTimeZone(args[1]));
	}
}