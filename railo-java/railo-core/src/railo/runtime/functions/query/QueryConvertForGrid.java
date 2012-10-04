/**
 * Implements the CFML Function querysetcell
 */
package railo.runtime.functions.query;

import railo.runtime.PageContext;
import railo.runtime.exp.FunctionNotSupported;
import railo.runtime.exp.PageException;
import railo.runtime.ext.function.Function;
import railo.runtime.type.Query;
import railo.runtime.type.Struct;

public final class QueryConvertForGrid implements Function {
	public static Struct call(PageContext pc , Query query, double page,double pageSize) throws PageException {
		throw new FunctionNotSupported("QueryConvertForGrid");		
	}
}