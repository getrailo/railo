package railo.runtime.functions.query;

import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.functions.BIF;
import railo.runtime.op.Caster;
import railo.runtime.type.Query;
import railo.runtime.type.util.ListUtil;

/**
 * Implements the CFML Function querynew
 */
public final class QueryColumnList extends BIF {

	private static final long serialVersionUID = 2718851377017546192L;

	public static String call(PageContext pc , Query qry) {
        return call(pc,qry,",");
    }
    public static String call(PageContext pc , Query qry, String delimiter) {
        return ListUtil.arrayToList(qry.getColumnNamesAsString(),delimiter);
    }
	
	@Override
	public Object invoke(PageContext pc, Object[] args) throws PageException {
		if(args.length==1)return call(pc,Caster.toQuery(args[0]));
		return call(pc,Caster.toQuery(args[0]),Caster.toString(args[1]));
	}
}