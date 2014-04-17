/**
 * Implements the CFML Function valuelist
 */
package railo.runtime.functions.query;

import railo.runtime.PageContext;
import railo.runtime.db.SQLCaster;
import railo.runtime.exp.PageException;
import railo.runtime.functions.BIF;
import railo.runtime.op.Caster;
import railo.runtime.type.Array;
import railo.runtime.type.ArrayImpl;
import railo.runtime.type.QueryColumn;

public class ValueArray extends BIF {
	
	private static final long serialVersionUID = -1810991362001086246L;

	public static Array call(PageContext pc , QueryColumn column) throws PageException {
		Array arr=new ArrayImpl();
	    int size=column.size();
	    Object obj;
	    short type = SQLCaster.toCFType(column.getType(), railo.commons.lang.CFTypes.TYPE_UNDEFINED);
	    
		for(int i=1;i<=size;i++) {
			obj=column.get(i,null);
			try{
				obj=Caster.castTo(pc, type, column.getTypeAsString(), obj);
			}
			catch(Throwable t){}
			arr.append(obj);
		}
		return arr;	
	}
	
	public static Array call(PageContext pc , String strQueryColumn) throws PageException {
	    return call(pc, ValueList.toColumn(pc,strQueryColumn));
	} 
	
	@Override
	public Object invoke(PageContext pc, Object[] args) throws PageException {
		if(args[0] instanceof QueryColumn) return call(pc, (QueryColumn)args[0]);
		return call(pc,Caster.toString(args[0]));
	}
}