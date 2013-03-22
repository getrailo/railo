package railo.runtime.functions.international;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Locale;
import java.util.TimeZone;

import railo.commons.date.TimeZoneUtil;
import railo.commons.i18n.FormatUtil;
import railo.commons.lang.StringUtil;
import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.ext.function.Function;
import railo.runtime.i18n.LocaleFactory;
import railo.runtime.op.Caster;
import railo.runtime.op.date.DateCaster;
import railo.runtime.type.dt.DateTime;
import railo.runtime.type.dt.DateTimeImpl;

/**
 * Implements the CFML Function dateformat
 */
public final class LSTimeFormat implements Function {
	
	/**
	 * @param pc
	 * @param o
	 * @return
	 * @throws PageException
	 */
	public static String call(PageContext pc , Object o) throws PageException {
		return _call(pc, o, "short", pc.getLocale(),pc.getTimeZone());
	}
	
	public static String call(PageContext pc , Object o, String mask) throws PageException {
		return _call(pc, o, mask, pc.getLocale(),pc.getTimeZone());
	}
	public static String call(PageContext pc , Object o, String mask,String strLocale) throws PageException {
		return _call(pc, o, mask, LocaleFactory.getLocale(strLocale),pc.getTimeZone());
	}
	public static String call(PageContext pc , Object o, String mask,String strLocale,String strTimezone) throws PageException {
		return _call(pc, o, mask, 
				strLocale==null?pc.getLocale():LocaleFactory.getLocale(strLocale),
				strTimezone==null?pc.getTimeZone():TimeZoneUtil.toTimeZone(strTimezone));
	}

	private static String _call(PageContext pc, Object o, String mask,Locale locale,TimeZone tz) throws PageException {
        if(o instanceof String && StringUtil.isEmpty((String)o,true)) return "";
        return new railo.runtime.format.TimeFormat(locale).format(toTimeLS(locale, tz, o),mask,tz);
	}
	
	
	private static DateTime toTimeLS(Locale locale, TimeZone timeZone, Object object) throws PageException {
		if(object instanceof DateTime) return (DateTime) object;
		if(object instanceof CharSequence) {
			String str=Caster.toString(object);
			
			DateFormat[] formats=FormatUtil.getTimeFormats(locale,timeZone,true);
			for(int i=0;i<formats.length;i++) {
				try {
					return new DateTimeImpl(formats[i].parse(str).getTime(),false);
				} 
				catch (ParseException e) {}
			}
			
		}
		return DateCaster.toDateAdvanced(object,timeZone);
	}
	
	
}