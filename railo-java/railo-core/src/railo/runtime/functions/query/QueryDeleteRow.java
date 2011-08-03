package railo.runtime.functions.query;

import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.type.Query;

public final class QueryDeleteRow {

    public static boolean call(PageContext pc, Query query) throws PageException {
        return call(pc,query,query.getRowCount());
    }
    
    public static boolean call(PageContext pc, Query query, double row) throws PageException {
        if(row==-9999) row=query.getRowCount();// used for named arguments
    	query.removeRow((int)row);
        return true;
    }
}