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

public final class QuerySetCell extends BIF {

	private static final long serialVersionUID = -5234853923691806118L;

	public static boolean call(PageContext pc , Query query, String columnName, Object value) throws PageException {
		return call(pc,query,columnName,value,query.getRecordcount());
	}
	public static boolean call(PageContext pc , Query query, String columnName, Object value, double rowNumber) throws PageException {
		if(rowNumber==-9999) rowNumber=query.getRecordcount();// used for named arguments
    	query.setAt(KeyImpl.init(columnName),(int)rowNumber,value);
		return true;		
	}
	
	@Override
	public Object invoke(PageContext pc, Object[] args) throws PageException {
		if(args.length==3)return call(pc,Caster.toQuery(args[0]),Caster.toString(args[1]),args[2]);
		return call(pc,Caster.toQuery(args[0]),Caster.toString(args[1]),args[2],Caster.toDoubleValue(args[3]));
	}
}