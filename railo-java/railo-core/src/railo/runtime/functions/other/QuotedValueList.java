/**
 * Implements the Cold Fusion Function quotedvaluelist
 */
package railo.runtime.functions.other;

import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.functions.query.ValueList;
import railo.runtime.op.Caster;
import railo.runtime.type.QueryColumn;

public final class QuotedValueList extends ValueList {
	public static String call(PageContext pc , String strQueryColumn) throws PageException {
		return call(pc,strQueryColumn,",");
	}
	public static String call(PageContext pc , String strQueryColumn, String delimeter) throws PageException {
		
		QueryColumn column = toColumn(pc,strQueryColumn);
		int size=column.size();
		StringBuffer sb=new StringBuffer();
		
		for(int i=1;i<=size;i++) {
			if(i>1)sb.append(delimeter);
			sb.append("'"+Caster.toString(column.get(i))+"'");
		}
		return sb.toString();
	}
}