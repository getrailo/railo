package railo.runtime.functions.query;

import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.type.Query;

public final class QueryDeleteRow {

    public static boolean call(PageContext pc, Query query) throws PageException {
        return call(pc,query,query.getRowCount());
    }
    
    public static boolean call(PageContext pc, Query query, double row) throws PageException {
        query.removeRow((int)row);
        return true;
    }
}