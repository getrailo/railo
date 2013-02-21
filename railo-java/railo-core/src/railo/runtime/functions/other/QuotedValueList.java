/**
 * Implements the CFML Function quotedvaluelist
 */
package railo.runtime.functions.other;

import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.functions.query.ValueList;
import railo.runtime.op.Caster;
import railo.runtime.type.QueryColumn;

public final class QuotedValueList extends ValueList {

	private static final long serialVersionUID = -6617432857065704955L;

	public static String call(PageContext pc , String strQueryColumn) throws PageException {
		return call(pc, toColumn(pc,strQueryColumn), ",");
	}
	public static String call(PageContext pc , String strQueryColumn, String delimiter) throws PageException {
		return call(pc, toColumn(pc,strQueryColumn), delimiter);
	}

	public static String call(PageContext pc , QueryColumn column) throws PageException {
		return call(pc, column, ",");
	}

	public static String call(PageContext pc , QueryColumn column, String delimiter) throws PageException {
		int size=column.size();
		StringBuilder sb=new StringBuilder();
		
		for(int i=1;i<=size;i++) {
			if(i>1)sb.append(delimiter);
			sb.append("'"+Caster.toString(column.get(i,null))+"'");
		}
		return sb.toString();
	}
}