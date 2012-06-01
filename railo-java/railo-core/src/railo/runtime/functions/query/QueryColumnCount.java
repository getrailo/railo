package railo.runtime.functions.query;

import railo.runtime.PageContext;
import railo.runtime.ext.function.Function;
import railo.runtime.type.Query;

/**
 * Implements the CFML Function querynew
 */
public final class QueryColumnCount implements Function {
    public static double call(PageContext pc , Query qry) {
        return qry.getColumns().length;
    }
}