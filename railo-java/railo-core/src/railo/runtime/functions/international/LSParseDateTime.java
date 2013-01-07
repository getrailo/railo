/**
 * Implements the CFML Function lsparsedatetime
 */
package railo.runtime.functions.international;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import railo.commons.date.TimeZoneUtil;
import railo.commons.lang.StringUtil;
import railo.runtime.PageContext;
import railo.runtime.engine.ThreadLocalPageContext;
import railo.runtime.exp.PageException;
import railo.runtime.ext.function.Function;
import railo.runtime.i18n.LocaleFactory;
import railo.runtime.op.Caster;
import railo.runtime.op.date.DateCaster;
import railo.runtime.type.dt.DateTimeImpl;

public final class LSParseDateTime implements Function {
	
	private static final long serialVersionUID = 7808039073301229473L;
	
	public static railo.runtime.type.dt.DateTime call(PageContext pc , Object oDate) throws PageException {
		return _call(pc, oDate, pc.getLocale(),pc.getTimeZone(),null);
	}
	
	public static railo.runtime.type.dt.DateTime call(PageContext pc , Object oDate,String strLocale) throws PageException {
		return _call(pc, oDate, LocaleFactory.getLocale(strLocale),pc.getTimeZone(),null);
	}
	
	public static railo.runtime.type.dt.DateTime call(PageContext pc , Object oDate,String strLocale,String strTimezoneOrFormat) throws PageException {
		if(strTimezoneOrFormat==null) {
			return _call(pc, oDate, LocaleFactory.getLocale(strLocale),pc.getTimeZone(),null);
		}
		Locale locale = strLocale==null?pc.getLocale():LocaleFactory.getLocale(strLocale);
		TimeZone tz = TimeZoneUtil.toTimeZone(strTimezoneOrFormat,null);
		if(tz!=null)
			return _call(pc, oDate, locale,tz,null);
		return _call(pc, oDate, locale,pc.getTimeZone(),strTimezoneOrFormat);
	}

	
	public static railo.runtime.type.dt.DateTime call(PageContext pc , Object oDate,String strLocale,String strTimezone, String strFormat) throws PageException {
		return _call(pc, oDate, 
				strLocale==null?pc.getLocale():LocaleFactory.getLocale(strLocale),
				strTimezone==null?pc.getTimeZone():TimeZoneUtil.toTimeZone(strTimezone),strFormat);
	}
	
	private static railo.runtime.type.dt.DateTime _call(PageContext pc , Object oDate,Locale locale,TimeZone tz, String format) throws PageException {
		if(oDate instanceof Date) return Caster.toDate(oDate, tz);

		String strDate = Caster.toString(oDate);
		
		// regular parse date time
		if(StringUtil.isEmpty(format,true))
			return DateCaster.toDateTime(locale,strDate,tz,locale.equals(Locale.US));
		
		
		// with java based format
		tz=ThreadLocalPageContext.getTimeZone(tz);
    	if(locale==null)locale=pc.getLocale();
		SimpleDateFormat df = new SimpleDateFormat(format, locale);
		df.setTimeZone(tz);
		try {
			return new DateTimeImpl(df.parse(strDate));
		} catch (ParseException e) {
			throw Caster.toPageException(e);
		}
	}
}