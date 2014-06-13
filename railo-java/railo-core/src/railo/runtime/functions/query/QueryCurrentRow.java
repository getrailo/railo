package railo.runtime.functions.query;

import railo.runtime.PageContext;
import railo.runtime.exp.FunctionException;
import railo.runtime.exp.PageException;
import railo.runtime.functions.BIF;
import railo.runtime.op.Caster;
import railo.runtime.type.Query;

/**
 * Implements the CFML Function querynew
 */
public final class QueryCurrentRow extends BIF {

	private static final long serialVersionUID = 6744860152468692462L;

	public static double call(PageContext pc , Query qry) {
        return qry.getCurrentrow(pc.getId());
    }
	
	@Override
	public Object invoke(PageContext pc, Object[] args) throws PageException {
		if(args.length==1)return call(pc,Caster.toQuery(args[0]));

		throw new FunctionException(pc, "QueryCurrentRow", 1, 1, args.length);
	}
}