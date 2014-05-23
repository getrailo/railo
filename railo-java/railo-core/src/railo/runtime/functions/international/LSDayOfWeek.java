/**
 * Implements the CFML Function dayofweek
 */
package railo.runtime.functions.international;

import java.util.Locale;
import java.util.TimeZone;

import railo.commons.date.DateTimeUtil;
import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.functions.BIF;
import railo.runtime.op.Caster;
import railo.runtime.type.dt.DateTime;

public final class LSDayOfWeek extends BIF {
	
	private static final long serialVersionUID = -9002250869621547151L;

	public static double call(PageContext pc , DateTime date) {
		return _call(pc, date, pc.getLocale(),pc.getTimeZone());
	}
	
	public static double call(PageContext pc , DateTime date, Locale locale) {
		return _call(pc, date, locale,pc.getTimeZone());
	}
	
	public static double call(PageContext pc , DateTime date, Locale locale, TimeZone tz) {
		return _call(pc, date, 
				locale==null?pc.getLocale():locale, 
				tz==null?pc.getTimeZone():tz);
	}
	
	@Override
	public Object invoke(PageContext pc, Object[] args) throws PageException {
		if(args.length==1)return call(pc,Caster.toDatetime(args[0],pc.getTimeZone()));
		if(args.length==2)return call(pc,Caster.toDatetime(args[0],pc.getTimeZone()),Caster.toLocale(args[1]));
		return call(pc,Caster.toDatetime(args[0],pc.getTimeZone()),Caster.toLocale(args[1]),Caster.toTimeZone(args[2]));
	}

	private static double _call(PageContext pc , DateTime date,Locale locale,TimeZone tz) {
		return DateTimeUtil.getInstance().getDayOfWeek(locale,tz, date);
	}
}