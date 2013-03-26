package railo.runtime.functions.query;

import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.functions.BIF;
import railo.runtime.op.Caster;
import railo.runtime.type.Array;
import railo.runtime.type.ArrayImpl;
import railo.runtime.type.Query;

/**
 * Implements the CFML Function querynew
 */
public final class QueryColumnArray extends BIF {
 
	private static final long serialVersionUID = 8166886589713144047L;

	public static Array call(PageContext pc , Query qry) {
        return new ArrayImpl(qry.getColumnNamesAsString());
    }
	
	@Override
	public Object invoke(PageContext pc, Object[] args) throws PageException {
		return call(pc,Caster.toQuery(args[0]));
	}
}