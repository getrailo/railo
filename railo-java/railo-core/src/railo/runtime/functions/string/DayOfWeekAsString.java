/**
 * Implements the CFML Function dayofweekasstring
 */
package railo.runtime.functions.string;

import java.util.Date;
import java.util.Locale;

import railo.commons.date.TimeZoneConstants;
import railo.commons.i18n.DateFormatPool;
import railo.runtime.PageContext;
import railo.runtime.exp.ExpressionException;
import railo.runtime.exp.FunctionException;
import railo.runtime.ext.function.Function;
import railo.runtime.i18n.LocaleFactory;

public final class DayOfWeekAsString implements Function {
	private static final int DAY=1000*60*60*24;
	
	
	private static Date[] dates=new Date[]{
			new Date(0+(3*DAY)),
			new Date(0+(4*DAY)),
			new Date(0+(5*DAY)),
			new Date(0+(6*DAY)),
			new Date(0),
			new Date(0+(1*DAY)),
			new Date(0+(2*DAY))
	};
	
	public static String call(PageContext pc , double dow) throws ExpressionException {
		return call(pc,dow, pc.getLocale(),true);
	}
	public static String call(PageContext pc , double dow, String strLocale) throws ExpressionException {
		return call(pc,dow, strLocale==null?pc.getLocale():LocaleFactory.getLocale(strLocale),true);
	}
	protected static String call(PageContext pc , double dow, Locale locale,boolean _long) throws ExpressionException {
		
		int dayOfWeek=(int)dow;
		if(dayOfWeek>=1 && dayOfWeek<=7) {
		    return DateFormatPool.format(locale,TimeZoneConstants.GMT0,_long?"EEEE":"EEE",dates[dayOfWeek-1]);
		}
		throw new FunctionException(
				pc,
				_long?"DayOfWeekAsString":"DayOfWeekShortAsString",
				1,"dayOfWeek",
				"must be between 1 and 7 now ["+dayOfWeek+"]");
		//throw new ExpressionException("invalid dayOfWeek definition in function DayOfWeekAsString, must be between 1 and 7 now ["+dayOfWeek+"]");
	}
}