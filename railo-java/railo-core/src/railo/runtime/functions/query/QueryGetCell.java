/**
 * Implements the CFML Function querysetcell
 */
package railo.runtime.functions.query;

import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.ext.function.Function;
import railo.runtime.type.Query;

public final class QueryGetCell implements Function {
	public static Object call(PageContext pc , Query query, String columnName) throws PageException {
		return call(pc,query,columnName,query.getRecordcount());
	}
	public static Object call(PageContext pc , Query query, String columnName, double rowNumber) throws PageException {
		if(rowNumber==-9999) rowNumber=query.getRecordcount();// used for named arguments
    	return query.getAt(columnName,(int)rowNumber);
	}
}