package railo.runtime.functions.query;

import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.ext.function.Function;
import railo.runtime.type.List;
import railo.runtime.type.QueryImpl;

/**
 * Implements the Cold Fusion Function querynew
 */
public final class QueryNew implements Function {
	public static railo.runtime.type.Query call(PageContext pc , String columnlist) {
	    return new QueryImpl(List.listToArrayTrim(columnlist,","),0,"query");
	}
	public static railo.runtime.type.Query call(PageContext pc , String columnlist, String columntypelist) throws PageException {
	    return new QueryImpl(List.listToArrayTrim(columnlist,","),List.listToArrayTrim(columntypelist,","),0,"query");
	}
}