package railo.runtime.functions.query;

import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.functions.BIF;
import railo.runtime.op.Caster;
import railo.runtime.type.Query;
import railo.runtime.type.QueryImpl;

/**
 * Implements the CFML Function querynew
 */
public final class QueryColumnCount extends BIF {

	private static final long serialVersionUID = 7637016307562378310L;

	public static double call(PageContext pc , Query qry) {
    	if(qry instanceof QueryImpl)
        	return ((QueryImpl)qry).getColumnCount();
    	return qry.getColumnNames().length;
    }
	
	@Override
	public Object invoke(PageContext pc, Object[] args) throws PageException {
		return call(pc,Caster.toQuery(args[0]));
	}
}