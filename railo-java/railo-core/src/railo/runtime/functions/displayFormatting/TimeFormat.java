package railo.runtime.functions.displayFormatting;

import java.util.Locale;
import java.util.TimeZone;

import railo.commons.date.TimeZoneUtil;
import railo.commons.lang.StringUtil;
import railo.runtime.PageContext;
import railo.runtime.engine.ThreadLocalPageContext;
import railo.runtime.exp.ExpressionException;
import railo.runtime.ext.function.Function;
import railo.runtime.op.Caster;
import railo.runtime.type.dt.DateTime;

/**
 * Implements the CFML Function dateformat
 */
public final class TimeFormat implements Function {
	
	/**
	 * @param pc
	 * @param object
	 * @return Formated Time Object as String
	 * @throws ExpressionException
	 */
	public static String call(PageContext pc , Object object) throws ExpressionException {
		return _call(pc,object,"hh:mm tt",ThreadLocalPageContext.getTimeZone(pc));
	}
	
	/**
	 * @param pc
	 * @param object
	 * @param mask Characters that show how CFML displays a date:
	 * @return Formated Time Object as String
	 * @throws ExpressionException
	 */
	public static String call(PageContext pc , Object object, String mask) throws ExpressionException {
		return _call(pc,object,mask,ThreadLocalPageContext.getTimeZone(pc));
	}

	public static String call(PageContext pc , Object object, String mask,String strTimezone) throws ExpressionException {
		return _call(pc,object,mask, strTimezone==null?ThreadLocalPageContext.getTimeZone(pc):TimeZoneUtil.toTimeZone(strTimezone));
	}
	
	private static String _call(PageContext pc , Object object, String mask,TimeZone tz) throws ExpressionException {
		Locale locale=Locale.US;//:pc.getConfig().getLocale();
		DateTime datetime = Caster.toDate(object,true,tz,null);
		if(datetime==null) {
		    if(StringUtil.isEmpty(object,true)) return "";
		    throw new ExpressionException("can't convert value "+object+" to a datetime value");
		}
		
		
		return new railo.runtime.format.TimeFormat(locale).format(datetime,mask,tz);
		//return new railo.runtime.text.TimeFormat(locale).format(datetime,mask);
	}
}