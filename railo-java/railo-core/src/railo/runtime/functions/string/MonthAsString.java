/**
 * Implements the CFML Function monthasstring
 */
package railo.runtime.functions.string;

import java.text.DateFormatSymbols;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import railo.commons.date.JREDateTimeUtil;
import railo.runtime.PageContext;
import railo.runtime.exp.ExpressionException;
import railo.runtime.ext.function.Function;

public final class MonthAsString implements Function {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3937165414347541683L;
	//private static final int MONTH=1000*60*60*24*32;
	private static Date[] dates=new Date[12];
	static {
		Calendar cal=JREDateTimeUtil.getThreadCalendar();
		cal.setTimeInMillis(0);
		dates[0]=cal.getTime();
		for(int i=1;i<12;i++) {
			cal.add(Calendar.MONTH,1);
			dates[i]=cal.getTime();
		}
	}

	public static String call(PageContext pc , double month) throws ExpressionException {
		return call(month, pc.getLocale());
	}
	public static String call(PageContext pc , double month, Locale locale) throws ExpressionException {
		return call(month, locale==null?pc.getLocale():locale);
	}
	
	private static String call(double month, Locale locale) throws ExpressionException {
		int m=(int)month;
		if(m>=1 && m<=12) {
			return new DateFormatSymbols(locale).getMonths()[m-1];
		}
		throw new ExpressionException("invalid month definition in function monthAsString, must be between 1 and 12 now ["+month+"]");
		
	}
}