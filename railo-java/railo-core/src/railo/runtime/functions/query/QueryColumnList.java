package railo.runtime.functions.query;

import railo.runtime.PageContext;
import railo.runtime.ext.function.Function;
import railo.runtime.type.List;
import railo.runtime.type.Query;

/**
 * Implements the Cold Fusion Function querynew
 */
public final class QueryColumnList implements Function {
    public static String call(PageContext pc , Query qry) {
        return call(pc,qry,",");
    }
    public static String call(PageContext pc , Query qry, String delimiter) {
        return List.arrayToList(qry.getColumns(),delimiter);
    }
}