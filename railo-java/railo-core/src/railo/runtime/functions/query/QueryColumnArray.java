package railo.runtime.functions.query;

import railo.runtime.PageContext;
import railo.runtime.ext.function.Function;
import railo.runtime.type.Array;
import railo.runtime.type.ArrayImpl;
import railo.runtime.type.Query;

/**
 * Implements the Cold Fusion Function querynew
 */
public final class QueryColumnArray implements Function {
    public static Array call(PageContext pc , Query qry) {
        return new ArrayImpl(qry.getColumns());
    }
}