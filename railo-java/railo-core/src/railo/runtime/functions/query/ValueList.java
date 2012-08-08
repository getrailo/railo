/**
 * Implements the CFML Function valuelist
 */
package railo.runtime.functions.query;

import railo.runtime.PageContext;
import railo.runtime.PageContextImpl;
import railo.runtime.exp.ExpressionException;
import railo.runtime.exp.PageException;
import railo.runtime.ext.function.Function;
import railo.runtime.op.Caster;
import railo.runtime.type.Query;
import railo.runtime.type.QueryColumn;
import railo.runtime.type.ref.VariableReference;
import railo.runtime.type.scope.Scope;

public class ValueList implements Function {
	public static String call(PageContext pc , String strQueryColumn) throws PageException {
		return call(pc,strQueryColumn,",");
	}
	public static String call(PageContext pc , String strQueryColumn, String delimiter) throws PageException {
	    
	    
	    QueryColumn column =toColumn(pc,strQueryColumn);
		StringBuffer sb=new StringBuffer();
		int size=column.size();
		for(int i=1;i<=size;i++) {
			if(i>1)sb.append(delimiter);
			sb.append(Caster.toString(column.get(i)));
		}
		return sb.toString();		
	}
	
	protected static QueryColumn toColumn(PageContext pc,String strQueryColumn) throws PageException {
	    //if(strQueryColumn.indexOf('.')<1)
	    //    throw new ExpressionException("invalid query column definition ["+strQueryColumn+"]");
		VariableReference ref = ((PageContextImpl)pc).getVariableReference(strQueryColumn);
		if(ref.getParent() instanceof Scope)
		    throw new ExpressionException("invalid query column definition ["+strQueryColumn+"]");
		
		
		Query query=Caster.toQuery(ref.getParent());
		return query.getColumn(ref.getKey());
	}
}