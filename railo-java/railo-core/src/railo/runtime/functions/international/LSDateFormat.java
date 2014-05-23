/**
 * Implements the CFML Function lsdateformat
 */
package railo.runtime.functions.international;

import java.util.Locale;
import java.util.TimeZone;

import railo.commons.lang.StringUtil;
import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.ext.function.Function;
import railo.runtime.op.Caster;
import railo.runtime.op.date.DateCaster;
import railo.runtime.type.dt.DateTime;

public final class LSDateFormat implements Function {

	private static final long serialVersionUID = 4720003854756942610L;
	
	public static String call(PageContext pc , Object object) throws PageException {
		return _call(pc, object, "medium", pc.getLocale(),pc.getTimeZone());
	}
	public static synchronized String call(PageContext pc , Object object, String mask) throws PageException {
		return _call(pc, object, mask, pc.getLocale(),pc.getTimeZone());
	}
	public static synchronized String call(PageContext pc , Object object, String mask, Locale locale) throws PageException {
		return _call(pc, object, mask, locale,pc.getTimeZone());
	}
	public static synchronized String call(PageContext pc , Object object, String mask,Locale locale,TimeZone tz) throws PageException {
		return _call(pc, object, mask, 
				locale==null?pc.getLocale():locale,
				tz==null?pc.getTimeZone():tz);
	}
	
	
	private static synchronized String _call(PageContext pc , Object object, String mask,Locale locale,TimeZone tz) throws PageException {
		if(StringUtil.isEmpty(object)) return "";
		
		return new railo.runtime.format.DateFormat(locale).
			format(toDateLS(pc ,locale,tz, object),mask,tz);
	}

	private static DateTime toDateLS(PageContext pc ,Locale locale, TimeZone timeZone, Object object) throws PageException {
		if(object instanceof DateTime) return (DateTime) object;
		else if(object instanceof CharSequence) {
			DateTime res = DateCaster.toDateTime(locale,Caster.toString(object),timeZone,null,locale.equals(Locale.US));
			if(res!=null)return res;
		}
		return DateCaster.toDateAdvanced(object,timeZone);
	}
}