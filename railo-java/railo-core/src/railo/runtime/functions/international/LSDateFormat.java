/**
 * Implements the Cold Fusion Function lsdateformat
 */
package railo.runtime.functions.international;

import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

import railo.commons.date.TimeZoneUtil;
import railo.commons.lang.StringUtil;
import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.ext.function.Function;
import railo.runtime.i18n.LocaleFactory;
import railo.runtime.op.Caster;
import railo.runtime.op.date.DateCaster;
import railo.runtime.type.dt.DateTime;

public final class LSDateFormat implements Function {
	private static Calendar calendar;
	public static String call(PageContext pc , Object object) throws PageException {
		return _call(pc, object, "medium", pc.getLocale(),pc.getTimeZone());
	}
	public static synchronized String call(PageContext pc , Object object, String mask) throws PageException {
		return _call(pc, object, mask, pc.getLocale(),pc.getTimeZone());
	}
	public static synchronized String call(PageContext pc , Object object, String mask,String strLocale) throws PageException {
		return _call(pc, object, mask, LocaleFactory.getLocale(strLocale),pc.getTimeZone());
	}
	public static synchronized String call(PageContext pc , Object object, String mask,String strLocale,String strTimezone) throws PageException {
		return _call(pc, object, mask, LocaleFactory.getLocale(strLocale),TimeZoneUtil.toTimeZone(strTimezone));
	}
	
	
	private static synchronized String _call(PageContext pc , Object object, String mask,Locale locale,TimeZone tz) throws PageException {
		if(StringUtil.isEmpty(object)) return "";
		
		return new railo.runtime.format.DateFormat(locale).
			format(toDateLS(pc ,locale,tz, object),mask,tz);
	}

	private static DateTime toDateLS(PageContext pc ,Locale locale, TimeZone timeZone, Object object) throws PageException {
		//print.out("oh:"+object);
		//DateTime res = Caster.toDateTime(locale,Caster.toString(object),pc.getTimeZone(),null,locale.equals(Locale.US));
		DateTime res = Caster.toDateTime(locale,Caster.toString(object),pc.getTimeZone(),null,false);
		if(res!=null)return res;
		return DateCaster.toDateAdvanced(object,timeZone);
		
		/*try{
			return DateCaster.toDateAdvanced(object,timeZone);
		}
		catch(PageException pe){
			if(object instanceof String) {
				String str=(String) object;
				DateFormat[] formats=FormatUtil.getDateFormats(locale,true);
				for(int i=0;i<formats.length;i++) {
					try {
						long t = formats[i].parse(str).getTime();
						return new DateTimeImpl(fixYear(timeZone,t),false);
					} 
					catch (ParseException e) {
						//
					}
				}
			}
			throw pe;
		}*/
	}
	
	private static long fixYear(TimeZone timezone,long time) {  
		if (calendar == null)
        	calendar=Calendar.getInstance();
        synchronized (calendar) {
        	calendar.clear();
        	calendar.setTimeZone(timezone);
        	calendar.setTimeInMillis(time);         
    		int year = calendar.get(Calendar.YEAR);
            if(year<100) {
                if(year<21)year=year+=2000;
                else year=year+=1900;
                calendar.set(Calendar.YEAR,year);
                return calendar.getTimeInMillis();
            }
        }
       return time;
	}
}