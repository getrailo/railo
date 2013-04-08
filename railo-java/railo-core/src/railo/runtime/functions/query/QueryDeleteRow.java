package railo.runtime.functions.query;

import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.functions.BIF;
import railo.runtime.op.Caster;
import railo.runtime.type.Query;

public final class QueryDeleteRow extends BIF {

	private static final long serialVersionUID = 7610413135885802876L;

	public static boolean call(PageContext pc, Query query) throws PageException {
        return call(pc,query,query.getRowCount());
    }
    
    public static boolean call(PageContext pc, Query query, double row) throws PageException {
        if(row==-9999) row=query.getRowCount();// used for named arguments
    	query.removeRow((int)row);
        return true;
    }
	
	@Override
	public Object invoke(PageContext pc, Object[] args) throws PageException {
		if(args.length==1)return call(pc,Caster.toQuery(args[0]));
		return call(pc,Caster.toQuery(args[0]),Caster.toDoubleValue(args[1]));
	}
}