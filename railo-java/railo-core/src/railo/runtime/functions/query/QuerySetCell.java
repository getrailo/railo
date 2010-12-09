/**
 * Implements the Cold Fusion Function querysetcell
 */
package railo.runtime.functions.query;

import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.ext.function.Function;
import railo.runtime.type.Query;

public final class QuerySetCell implements Function {
	public static boolean call(PageContext pc , Query query, String columnName, Object value) throws PageException {
		return call(pc,query,columnName,value,query.getRecordcount());
	}
	public static boolean call(PageContext pc , Query query, String columnName, Object value, double rowNumber) throws PageException {
		if(rowNumber==-9999) rowNumber=query.getRecordcount();// used for named arguments
    	query.setAt(columnName,(int)rowNumber,value);
		return true;		
	}
}