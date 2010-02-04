/**
 * Implements the Cold Fusion Function lsparsedatetime
 */
package railo.runtime.functions.international;

import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import railo.commons.date.TimeZoneUtil;
import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.ext.function.Function;
import railo.runtime.i18n.LocaleFactory;
import railo.runtime.op.Caster;

public final class LSParseDateTime implements Function {
	public static railo.runtime.type.dt.DateTime call(PageContext pc , Object oDate) throws PageException {
		return _call(pc, oDate, pc.getLocale(),pc.getTimeZone());
	}
	public static railo.runtime.type.dt.DateTime call(PageContext pc , Object oDate,String strLocale) throws PageException {
		return _call(pc, oDate, LocaleFactory.getLocale(strLocale),pc.getTimeZone());
	}
	public static railo.runtime.type.dt.DateTime call(PageContext pc , Object oDate,String strLocale,String strTimezone) throws PageException {
		return _call(pc, oDate, LocaleFactory.getLocale(strLocale),TimeZoneUtil.toTimeZone(strTimezone));
	}
	private static railo.runtime.type.dt.DateTime _call(PageContext pc , Object oDate,Locale locale,TimeZone tz) throws PageException {
		if(oDate instanceof Date) return Caster.toDate(oDate, tz);
		//return Caster.toDateTime(locale,Caster.toString(oDate),tz,locale.equals(Locale.US));
		return Caster.toDateTime(locale,Caster.toString(oDate),tz,false);
	}
}