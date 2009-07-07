/**
 * Implements the Cold Fusion Function datepart
 */
package railo.runtime.functions.dateTime;

import railo.runtime.PageContext;
import railo.runtime.exp.ExpressionException;
import railo.runtime.ext.function.Function;
import railo.runtime.type.dt.DateTime;

public final class DatePart implements Function {
	public static double call(PageContext pc , String datepart, DateTime date) throws ExpressionException {
		datepart=datepart.toLowerCase();
		char first=datepart.length()==1?datepart.charAt(0):(char)0;
		
		if(datepart.equals("yyyy")) return Year.call(pc,date);
		else if(datepart.equals("ww")) return Week.call(pc,date);
		else if(first=='w') return DayOfWeek.call(pc,date);
		else if(first=='q') return Quarter.call(pc,date);
		else if(first=='m') return Month.call(pc,date);
		else if(first=='y') return DayOfYear.call(pc,date);
		else if(first=='d') return Day.call(pc,date);
		else if(first=='h') return Hour.call(pc,date);
		else if(first=='n') return Minute.call(pc,date);
		else if(first=='s') return Second.call(pc,date);
		else if(first=='l') return MilliSecond.call(pc, date);	
		throw new ExpressionException("invalid datepart type ["+datepart+"] for function datePart");
	}
}