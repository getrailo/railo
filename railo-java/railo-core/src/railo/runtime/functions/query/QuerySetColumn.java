/**
 * Implements the Cold Fusion Function queryaddcolumn
 */
package railo.runtime.functions.query;

import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.ext.function.Function;
import railo.runtime.type.Collection;
import railo.runtime.type.KeyImpl;
import railo.runtime.type.Query;

public final class QuerySetColumn implements Function {
	public static String call(PageContext pc , Query query, String columnName,String newColumnName) throws PageException {
		columnName=columnName.trim();
		newColumnName=newColumnName.trim();
		Collection.Key src=KeyImpl.init(columnName);
		Collection.Key trg=KeyImpl.init(newColumnName);
		
		query.rename(src, trg);
		return null;
	}
}