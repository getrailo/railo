package railo.runtime.functions.dateTime;

import railo.runtime.PageContext;
import railo.runtime.exp.ExpressionException;
import railo.runtime.exp.PageException;
import railo.runtime.functions.BIF;
import railo.runtime.op.Caster;
import railo.runtime.type.dt.DateTime;

public class DateAddMember extends BIF {

	private static final long serialVersionUID = 2435230088985760512L;

	public static DateTime call(PageContext pc , DateTime date,String datepart, double number) throws ExpressionException {
		return DateAdd.call(pc, datepart, number, date);
	}
	
	@Override
	public Object invoke(PageContext pc, Object[] args) throws PageException {
		return call(pc,Caster.toDatetime(args[0],pc.getTimeZone()),Caster.toString(args[1]),Caster.toDoubleValue(args[2]));
	}
}
