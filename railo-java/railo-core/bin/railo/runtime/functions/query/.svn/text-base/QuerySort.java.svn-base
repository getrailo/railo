/**
 * Implements the Cold Fusion Function querysetcell
 */
package railo.runtime.functions.query;

import railo.runtime.PageContext;
import railo.runtime.exp.DatabaseException;
import railo.runtime.exp.PageException;
import railo.runtime.ext.function.Function;
import railo.runtime.type.Query;

public final class QuerySort implements Function {
	public static boolean call(PageContext pc , Query query, String columnName) throws PageException {
		return call(pc,query,columnName,"asc");
	}
	public static boolean call(PageContext pc , Query query, String columnName, String direction) throws PageException {
		direction=direction.trim().toLowerCase();
		int dir=0;
		if(direction.equals("asc"))dir=Query.ORDER_ASC;
		else if(direction.equals("desc"))dir=Query.ORDER_DESC;
		else {		
			throw new DatabaseException("argument direction of function querySort must be \"asc\" or \"desc\", now \""+direction+"\"",null,null,null);
		}
		query.sort(columnName,dir);
		return true;		
	}
}