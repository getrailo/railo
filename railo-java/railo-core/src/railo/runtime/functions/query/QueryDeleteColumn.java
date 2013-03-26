package railo.runtime.functions.query;

import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.functions.BIF;
import railo.runtime.op.Caster;
import railo.runtime.type.Array;
import railo.runtime.type.ArrayImpl;
import railo.runtime.type.KeyImpl;
import railo.runtime.type.Query;
import railo.runtime.type.QueryColumn;
import railo.runtime.type.util.QueryUtil;

public final class QueryDeleteColumn extends BIF {

	private static final long serialVersionUID = 5363459913899891827L;

	public static Array call(PageContext pc, Query query, String strColumn) throws PageException {
        return toArray(query.removeColumn(KeyImpl.init(strColumn)));
    }
    
    public static Array toArray(QueryColumn column) throws PageException {
        Array clone=new ArrayImpl();
        int len=column.size();
        clone.resize(len);
        
        for(int i=1;i<=len;i++) {
            clone.setE(i,QueryUtil.getValue(column,i));
        }
        return clone;
    }
	
	@Override
	public Object invoke(PageContext pc, Object[] args) throws PageException {
		return call(pc,Caster.toQuery(args[0]),Caster.toString(args[1]));
	}
}