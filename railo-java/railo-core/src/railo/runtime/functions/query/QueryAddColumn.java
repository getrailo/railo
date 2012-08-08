/**
 * Implements the CFML Function queryaddcolumn
 */
package railo.runtime.functions.query;

import railo.runtime.PageContext;
import railo.runtime.db.SQLCaster;
import railo.runtime.exp.PageException;
import railo.runtime.ext.function.Function;
import railo.runtime.op.Caster;
import railo.runtime.type.ArrayImpl;
import railo.runtime.type.Query;

public final class QueryAddColumn implements Function {
	
	public static double call(PageContext pc , Query query, String string) throws PageException {
		return call(pc, query, string,new ArrayImpl());
	}
	
	public static double call(PageContext pc , Query query, String string, Object array) throws PageException {
		query.addColumn(string,Caster.toArray(array));
		return query.size();
	}
	
	public static double call(PageContext pc , Query query, String string, Object datatype, Object array) throws PageException {
		if(datatype==null) return call(pc, query, string, array);
		
		query.addColumn(string,Caster.toArray(array),SQLCaster.toIntType(Caster.toString(datatype)));
		return query.size();
	}
}