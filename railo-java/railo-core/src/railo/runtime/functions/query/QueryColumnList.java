package railo.runtime.functions.query;

import railo.runtime.PageContext;
import railo.runtime.ext.function.Function;
import railo.runtime.type.Query;
import railo.runtime.type.util.ListUtil;

/**
 * Implements the CFML Function querynew
 */
public final class QueryColumnList implements Function {
    public static String call(PageContext pc , Query qry) {
        return call(pc,qry,",");
    }
    public static String call(PageContext pc , Query qry, String delimiter) {
        return ListUtil.arrayToList(qry.getColumns(),delimiter);
    }
}