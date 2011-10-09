/**
 * Implements the Cold Fusion Function valuelist
 */
package railo.runtime.functions.query;

import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.ext.function.Function;
import railo.runtime.op.Caster;
import railo.runtime.type.Array;
import railo.runtime.type.ArrayImpl;
import railo.runtime.type.KeyImpl;
import railo.runtime.type.Query;
import railo.runtime.type.QueryColumn;

public class QueryValueArray implements Function {
	public static Array call(PageContext pc, Query query, String columnName) throws PageException {
		QueryColumn column = query.getColumn(KeyImpl.init(columnName));
	    Array arr=new ArrayImpl();
	    int size=column.size();
		for(int i=1;i<=size;i++) {
			arr.append(Caster.toString(column.get(i)));
		}
		return arr;		
	} 
}