/**
 * Implements the CFML Function lsisdate
 */
package railo.runtime.functions.international;

import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import railo.commons.date.TimeZoneUtil;
import railo.runtime.PageContext;
import railo.runtime.exp.ExpressionException;
import railo.runtime.ext.function.Function;
import railo.runtime.i18n.LocaleFactory;
import railo.runtime.op.Decision;

public final class LSIsDate implements Function {

	private static final long serialVersionUID = -8517171925554806088L;


	public static boolean call(PageContext pc , Object object) {
		return call(pc, object, pc.getLocale(),pc.getTimeZone());
	}

	public static boolean call(PageContext pc , Object object,String strLocale) throws ExpressionException {
		return call(pc, object, LocaleFactory.getLocale(strLocale),pc.getTimeZone());
	}
	public static boolean call(PageContext pc , Object object,String strLocale,String strTimezone) throws ExpressionException {
		return call(pc, object, 
				strLocale==null?pc.getLocale():LocaleFactory.getLocale(strLocale),
				strTimezone==null?pc.getTimeZone():TimeZoneUtil.toTimeZone(strTimezone));
	}
	
	
	private static boolean call(PageContext pc  , Object object,Locale locale,TimeZone tz) {
		if(object instanceof Date) return true;
		else if(object instanceof String) {
		    String str=object.toString();
		    if(str.length()<2) return false;
		    //print.out(Caster.toDateTime(locale,str,pc.getTimeZone(),null));
			return Decision.isDate(str,locale,tz,locale.equals(Locale.US));
			//return Caster.toDateTime(locale,str,pc.getTimeZone(),null)!=null;
		}
		return false;
	}
	
}