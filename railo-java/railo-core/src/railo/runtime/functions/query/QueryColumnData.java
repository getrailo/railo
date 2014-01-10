package railo.runtime.functions.query;

import java.util.Iterator;

import railo.runtime.PageContext;
import railo.runtime.config.NullSupportHelper;
import railo.runtime.exp.PageException;
import railo.runtime.functions.BIF;
import railo.runtime.op.Caster;
import railo.runtime.type.Array;
import railo.runtime.type.ArrayImpl;
import railo.runtime.type.KeyImpl;
import railo.runtime.type.Query;
import railo.runtime.type.QueryColumn;
import railo.runtime.type.UDF;

public class QueryColumnData extends BIF {

	private static final long serialVersionUID = 3915214686428831274L;

	public static Array call(PageContext pc, Query query, String columnName) throws PageException {
		return call(pc, query, columnName, null);
	}
	public static Array call(PageContext pc, Query query, String columnName,  UDF udf) throws PageException {
		Array arr=new ArrayImpl();
		QueryColumn column = query.getColumn(KeyImpl.init(columnName));
	    Iterator<Object> it = column.valueIterator();
	    Object value;
		while(it.hasNext()) {
			value=it.next();
			if(!NullSupportHelper.full() && value==null) value="";
			if(udf!=null)arr.append(udf.call(pc, new Object[]{value}, true));
			else arr.append(value);
		}
		return arr;		
	} 
	
	@Override
	public Object invoke(PageContext pc, Object[] args) throws PageException {
		if(args.length==2)return call(pc,Caster.toQuery(args[0]),Caster.toString(args[1]));
		return call(pc,Caster.toQuery(args[0]),Caster.toString(args[1]),Caster.toFunction(args[2]));
	}
}
