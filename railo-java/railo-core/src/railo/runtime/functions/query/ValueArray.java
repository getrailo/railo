/**
 * Implements the CFML Function valuelist
 */
package railo.runtime.functions.query;

import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.ext.function.Function;
import railo.runtime.op.Caster;
import railo.runtime.type.Array;
import railo.runtime.type.ArrayImpl;
import railo.runtime.type.QueryColumn;

public class ValueArray implements Function {
	public static Array call(PageContext pc , String strQueryColumn) throws PageException {
	    QueryColumn column =ValueList.toColumn(pc,strQueryColumn);
	    Array arr=new ArrayImpl();
	    int size=column.size();
		for(int i=1;i<=size;i++) {
			arr.append(Caster.toString(column.get(i)));
		}
		return arr;		
	} 
}