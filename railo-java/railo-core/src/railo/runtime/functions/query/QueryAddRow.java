/**
 * Implements the CFML Function queryaddrow
 */
package railo.runtime.functions.query;

import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.functions.BIF;
import railo.runtime.op.Caster;
import railo.runtime.op.Decision;
import railo.runtime.type.Query;

public final class QueryAddRow extends BIF {

	private static final long serialVersionUID = 1252130736067181453L;

	public static double call(PageContext pc , Query query) {
		query.addRow(1);
		return query.getRecordcount();
	}
	public static double call(PageContext pc , Query query, Object numberOrData) throws PageException {
		if(numberOrData==null) return call(pc, query);
		else if(Decision.isNumeric(numberOrData)) {
			query.addRow(Caster.toIntValue(numberOrData));
		}
		else {
			QueryNew.populate(pc, query, numberOrData);
		}
		return query.getRecordcount();
	}
	
	@Override
	public Object invoke(PageContext pc, Object[] args) throws PageException {
		if(args.length==1)return call(pc,Caster.toQuery(args[0]));
		return call(pc,Caster.toQuery(args[0]),Caster.toString(args[1]));
	}
}