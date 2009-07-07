package railo.runtime.functions.query;

import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.type.Array;
import railo.runtime.type.ArrayImpl;
import railo.runtime.type.Query;
import railo.runtime.type.QueryColumn;

public final class QueryDeleteColumn {

    public static Array call(PageContext pc, Query query, String strColumn) throws PageException {

        return toArray(query.removeColumn(strColumn));
    }
    public static Array toArray(QueryColumn column) throws PageException {
        ArrayImpl clone=new ArrayImpl();
        int len=column.size();
        clone.resize(len);
        
        for(int i=1;i<=len;i++) {
            clone.setE(i,column.get(i));
        }
        return clone;
    }
}