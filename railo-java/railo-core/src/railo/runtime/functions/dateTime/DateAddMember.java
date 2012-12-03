package railo.runtime.functions.dateTime;

import railo.runtime.PageContext;
import railo.runtime.exp.ExpressionException;
import railo.runtime.type.dt.DateTime;

public class DateAddMember {
	public static DateTime call(PageContext pc , DateTime date,String datepart, double number) throws ExpressionException {
		return DateAdd.call(pc, datepart, number, date);
	}
}
