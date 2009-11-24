/**
 * Implements the Cold Fusion Function dateadd
 */
package railo.runtime.functions.dateTime;

import java.util.Calendar;
import java.util.TimeZone;

import railo.runtime.PageContext;
import railo.runtime.engine.ThreadLocalPageContext;
import railo.runtime.exp.ExpressionException;
import railo.runtime.ext.function.Function;
import railo.runtime.type.dt.DateTime;
import railo.runtime.type.dt.DateTimeImpl;

public final class DateAdd implements Function {
	private static Calendar c;

	public static DateTime invoke(String datepart, double number, DateTime date) throws ExpressionException {
		return call(ThreadLocalPageContext.get() , datepart, number, date);
	}
	
	public static DateTime call(PageContext pc , String datepart, double number, DateTime date) throws ExpressionException {
		return _call(pc,pc.getTimeZone(), datepart, number, date);
	}
	
	public synchronized static DateTime _call(PageContext pc ,TimeZone tz, String datepart, double number, DateTime date) throws ExpressionException {
		datepart=datepart.toLowerCase();
		long l=(long)number;
		int n=(int) l;
		char first=datepart.length()==1?datepart.charAt(0):(char)0;

		if(first=='l') 			return new DateTimeImpl(pc,date.getTime()+l,false);
		else if(first=='s') 	return new DateTimeImpl(pc,date.getTime()+(l*1000),false);
		else if(first=='n')		return new DateTimeImpl(pc,date.getTime()+(l*60000),false);
		else if(first=='h')		return new DateTimeImpl(pc,date.getTime()+(l*3600000),false);
		
		
		
		if (c == null)
        	c=Calendar.getInstance();
        synchronized (c) {
        	//c.clear();
        	c.setTimeZone(tz);
        	c.setTimeInMillis(date.getTime());
			
			if(datepart.equals("yyyy")) {
				c.set(Calendar.YEAR,c.get(Calendar.YEAR)+n);
			}
			else if(datepart.equals("ww")) c.add(Calendar.WEEK_OF_YEAR,n);
			else if(first=='q') c.add(Calendar.MONTH,(n*3));
			else if(first=='m') c.add(Calendar.MONTH,n);
			else if(first=='y') c.add(Calendar.DAY_OF_YEAR,n);
			else if(first=='d') c.add(Calendar.DATE,n);
			else if(first=='w') {
				c.add(Calendar.DAY_OF_YEAR,n);
			}
			
			else {
				throw new ExpressionException("invalid datepart identifier ["+datepart+"] for function dateAdd");
			}
			return new DateTimeImpl(pc,c.getTimeInMillis(),false);
        }
	}
}