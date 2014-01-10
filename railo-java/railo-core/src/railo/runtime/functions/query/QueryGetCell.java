/**
 * Implements the CFML Function querysetcell
 */
package railo.runtime.functions.query;

import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.functions.BIF;
import railo.runtime.op.Caster;
import railo.runtime.type.KeyImpl;
import railo.runtime.type.Query;

public final class QueryGetCell extends BIF {

	private static final long serialVersionUID = -6234552570552045133L;

	public static Object call(PageContext pc , Query query, String columnName) throws PageException {
		return call(pc,query,columnName,query.getRecordcount());
	}
	public static Object call(PageContext pc , Query query, String columnName, double rowNumber) throws PageException {
		if(rowNumber==-9999) rowNumber=query.getRecordcount();// used for named arguments
    	return query.getAt(KeyImpl.init(columnName),(int)rowNumber);
	}
	
	@Override
	public Object invoke(PageContext pc, Object[] args) throws PageException {
		if(args.length==2)return call(pc,Caster.toQuery(args[0]),Caster.toString(args[1]));
		return call(pc,Caster.toQuery(args[0]),Caster.toString(args[1]),Caster.toDoubleValue(args[2]));
	}
}