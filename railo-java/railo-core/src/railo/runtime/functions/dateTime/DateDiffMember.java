package railo.runtime.functions.dateTime;

import railo.runtime.PageContext;
import railo.runtime.exp.ExpressionException;
import railo.runtime.type.dt.DateTime;

public class DateDiffMember {
	public synchronized static double call(PageContext pc, DateTime left, DateTime right) throws ExpressionException	{
		return DateDiff.call(pc, "s", left, right);
	}
	public synchronized static double call(PageContext pc, DateTime left, DateTime right,String datePart) throws ExpressionException	{
		return DateDiff.call(pc, datePart, left, right);
	}
}
