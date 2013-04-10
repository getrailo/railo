package railo.runtime.functions.dateTime;

import railo.runtime.PageContext;
import railo.runtime.exp.ExpressionException;
import railo.runtime.exp.PageException;
import railo.runtime.functions.BIF;
import railo.runtime.op.Caster;
import railo.runtime.type.dt.DateTime;

public class DateDiffMember extends BIF {
	public synchronized static double call(PageContext pc, DateTime left, DateTime right) throws ExpressionException	{
		return DateDiff.call(pc, "s", left, right);
	}
	public synchronized static double call(PageContext pc, DateTime left, DateTime right,String datePart) throws ExpressionException	{
		return DateDiff.call(pc, datePart, left, right);
	}
	
	@Override
	public Object invoke(PageContext pc, Object[] args) throws PageException {
		if(args.length==2)return call(pc,Caster.toDatetime(args[0],pc.getTimeZone()),Caster.toDatetime(args[1],pc.getTimeZone()));
		return call(pc,Caster.toDatetime(args[0],pc.getTimeZone()),Caster.toDatetime(args[1],pc.getTimeZone()),Caster.toString(args[2]));
	}
}
