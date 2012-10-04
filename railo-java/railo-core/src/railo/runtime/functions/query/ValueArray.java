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
	
	private static final long serialVersionUID = -1810991362001086246L;

	public static Array call(PageContext pc , QueryColumn column) throws PageException {
		Array arr=new ArrayImpl();
	    int size=column.size();
		for(int i=1;i<=size;i++) {
			arr.append(Caster.toString(column.get(i)));
		}
		return arr;	
	}
	
	public static Array call(PageContext pc , String strQueryColumn) throws PageException {
	    return call(pc, ValueList.toColumn(pc,strQueryColumn));
	} 
}