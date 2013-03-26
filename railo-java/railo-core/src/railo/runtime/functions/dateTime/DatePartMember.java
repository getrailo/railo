package railo.runtime.functions.dateTime;

import railo.runtime.PageContext;
import railo.runtime.exp.ExpressionException;
import railo.runtime.exp.PageException;
import railo.runtime.functions.BIF;
import railo.runtime.op.Caster;
import railo.runtime.type.dt.DateTime;

public class DatePartMember extends BIF {

	private static final long serialVersionUID = 4954080153486127616L;

	public static double call(PageContext pc , DateTime date,String datepart) throws ExpressionException {
		return DatePart.call(pc, datepart, date, null);
	}
	
	public static double call(PageContext pc , DateTime date,String datepart, String strTimezone) throws ExpressionException {
		return DatePart.call(pc, datepart, date, strTimezone);
	}
	
	@Override
	public Object invoke(PageContext pc, Object[] args) throws PageException {
		if(args.length==2)return call(pc,Caster.toDatetime(args[0],pc.getTimeZone()),Caster.toString(args[1]));
		return call(pc,Caster.toDatetime(args[0],pc.getTimeZone()),Caster.toString(args[1]),Caster.toString(args[2]));
	}
	
}
