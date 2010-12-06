/**
 * Implements the Cold Fusion Function dayofweek
 */
package railo.runtime.functions.international;

import java.util.Locale;
import java.util.TimeZone;

import railo.commons.date.DateTimeUtil;
import railo.commons.date.TimeZoneUtil;
import railo.runtime.PageContext;
import railo.runtime.exp.ExpressionException;
import railo.runtime.ext.function.Function;
import railo.runtime.i18n.LocaleFactory;
import railo.runtime.type.dt.DateTime;

public final class LSDayOfWeek implements Function {
	
	private static final long serialVersionUID = -9002250869621547151L;

	public static double call(PageContext pc , DateTime date) {
		return _call(pc, date, pc.getLocale(),pc.getTimeZone());
	}
	
	public static double call(PageContext pc , DateTime date, String strLocale) throws ExpressionException {
		return _call(pc, date, LocaleFactory.getLocale(strLocale),pc.getTimeZone());
	}
	
	public static double call(PageContext pc , DateTime date, String strLocale, String strTimezone) throws ExpressionException {
		return _call(pc, date, 
				strLocale==null?pc.getLocale():LocaleFactory.getLocale(strLocale), 
				strTimezone==null?pc.getTimeZone():TimeZoneUtil.toTimeZone(strTimezone));
	}

	private static double _call(PageContext pc , DateTime date,Locale locale,TimeZone tz) {
		return DateTimeUtil.getInstance().getDayOfWeek(locale,tz, date);
	}
}