package railo.runtime.functions.query;

import java.util.Iterator;

import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.type.Array;
import railo.runtime.type.ArrayImpl;
import railo.runtime.type.KeyImpl;
import railo.runtime.type.Query;
import railo.runtime.type.QueryColumn;
import railo.runtime.type.UDF;

public class QueryColumnData {
	public static Array call(PageContext pc, Query query, String columnName) throws PageException {
		return call(pc, query, columnName, null);
	}
	public static Array call(PageContext pc, Query query, String columnName,  UDF udf) throws PageException {
		Array arr=new ArrayImpl();
		QueryColumn column = query.getColumn(KeyImpl.init(columnName));
	    Iterator<Object> it = column.valueIterator();
		while(it.hasNext()) {
			if(udf!=null)arr.append(udf.call(pc, new Object[]{it.next()}, true));
			else arr.append(it.next());
		}
		return arr;		
	} 
}
