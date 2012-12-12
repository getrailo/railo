package railo.runtime.functions.query;

import railo.runtime.PageContext;
import railo.runtime.ext.function.Function;
import railo.runtime.type.Query;
import railo.runtime.type.QueryImpl;

/**
 * Implements the CFML Function querynew
 */
public final class QueryColumnCount implements Function {
    public static double call(PageContext pc , Query qry) {
    	if(qry instanceof QueryImpl)
        	return ((QueryImpl)qry).getColumnCount();
    	return qry.getColumnNames().length;
    }
}