package railo.runtime.functions.dateTime;

import railo.runtime.PageContext;
import railo.runtime.exp.ExpressionException;
import railo.runtime.type.dt.DateTime;

public class DatePartMember {
	
	public static double call(PageContext pc , DateTime date,String datepart) throws ExpressionException {
		return DatePart.call(pc, datepart, date, null);
	}
	
	public static double call(PageContext pc , DateTime date,String datepart, String strTimezone) throws ExpressionException {
		return DatePart.call(pc, datepart, date, strTimezone);
	}
	
}
