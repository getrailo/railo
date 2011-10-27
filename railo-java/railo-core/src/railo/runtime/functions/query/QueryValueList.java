/**
 * Implements the Cold Fusion Function valuelist
 */
package railo.runtime.functions.query;

import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.ext.function.Function;
import railo.runtime.op.Caster;
import railo.runtime.type.KeyImpl;
import railo.runtime.type.Query;
import railo.runtime.type.QueryColumn;

public class QueryValueList implements Function {
	public static String call(PageContext pc , Query query, String columnName) throws PageException {
		return call(pc,query,columnName,null);
	}
	public static String call(PageContext pc, Query query, String columnName, String delimeter) throws PageException {
		if(delimeter==null)delimeter=",";
		QueryColumn column = query.getColumn(KeyImpl.init(columnName));
	    
		StringBuffer sb=new StringBuffer();
		int size=column.size();
		for(int i=1;i<=size;i++) {
			if(i>1)sb.append(delimeter);
			sb.append(Caster.toString(column.get(i)));
		}
		return sb.toString();		
	}
}