package railo.runtime.functions.international;

import java.util.Locale;
import java.util.TimeZone;

import railo.commons.date.DateTimeUtil;
import railo.runtime.PageContext;
import railo.runtime.ext.function.Function;
import railo.runtime.type.dt.DateTime;

public final class LSWeek implements Function {

	private static final long serialVersionUID = -4184509921145613454L;

	public static double call(PageContext pc , DateTime date) {
		return _call(pc, date, pc.getLocale(), pc.getTimeZone());
	}
	
	public static double call(PageContext pc , DateTime date, Locale locale) {
		return _call(pc, date, locale==null?pc.getLocale():locale,pc.getTimeZone());
	}
	
	public static double call(PageContext pc , DateTime date, Locale locale, TimeZone tz) {
		return _call(pc, date, 
				locale==null?pc.getLocale():locale,
				tz==null?pc.getTimeZone():tz);
	}
	
	private static double _call(PageContext pc , DateTime date,Locale locale,TimeZone tz) {
		return DateTimeUtil.getInstance().getWeekOfYear(locale,tz, date);
	} 
}