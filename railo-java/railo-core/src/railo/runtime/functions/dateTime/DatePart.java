/**
 * Implements the CFML Function datepart
 */
package railo.runtime.functions.dateTime;

import java.util.TimeZone;

import railo.runtime.PageContext;
import railo.runtime.exp.ExpressionException;
import railo.runtime.ext.function.Function;
import railo.runtime.type.dt.DateTime;

public final class DatePart implements Function {
	
	private static final long serialVersionUID = -4203375459570986511L;

	public static double call(PageContext pc , String datepart, DateTime date) throws ExpressionException {
		return call(pc, datepart, date, null);
	}
	
	public static double call(PageContext pc , String datepart, DateTime date,TimeZone tz) throws ExpressionException {
		datepart=datepart.toLowerCase();
		char first=datepart.length()==1?datepart.charAt(0):(char)0;
		
		if(datepart.equals("yyyy")) return Year.call(pc,date,tz);
		else if(datepart.equals("ww")) return Week.call(pc,date,tz);
		else if(first=='w') return DayOfWeek.call(pc,date,tz);
		else if(first=='q') return Quarter.call(pc,date,tz);
		else if(first=='m') return Month.call(pc,date,tz);
		else if(first=='y') return DayOfYear.call(pc,date,tz);
		else if(first=='d') return Day.call(pc,date,tz);
		else if(first=='h') return Hour.call(pc,date,tz);
		else if(first=='n') return Minute.call(pc,date,tz);
		else if(first=='s') return Second.call(pc,date,tz);
		else if(first=='l') return MilliSecond.call(pc, date,tz);	
		throw new ExpressionException("invalid datepart type ["+datepart+"] for function datePart");
	}
}