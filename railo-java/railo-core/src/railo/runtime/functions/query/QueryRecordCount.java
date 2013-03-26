package railo.runtime.functions.query;

import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.functions.BIF;
import railo.runtime.op.Caster;
import railo.runtime.type.Query;

/**
 * Implements the CFML Function querynew
 */
public final class QueryRecordCount extends BIF {

	private static final long serialVersionUID = -5956390806966915503L;

	public static double call(PageContext pc , Query qry) {
        return qry.getRowCount();
    }
	
	@Override
	public Object invoke(PageContext pc, Object[] args) throws PageException {
		return call(pc,Caster.toQuery(args[0]));
	}
}