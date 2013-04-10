/**
 * Implements the CFML Function arraydeleteat
 */
package railo.runtime.functions.arrays;

import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.functions.BIF;
import railo.runtime.op.Caster;
import railo.runtime.type.Array;


public final class ArrayDelete extends BIF {

	private static final long serialVersionUID = 1120923916196967210L;
	
	public static boolean call(PageContext pc , Array array, Object value) throws PageException {
		return call(pc, array, value,null);
	}
	public static boolean call(PageContext pc , Array array, Object value, String scope) throws PageException {
		boolean onlyFirst=!"all".equalsIgnoreCase(scope);
		double pos;
		if((pos=ArrayFindNoCase.call(pc, array, value))>0){
			array.removeE((int)pos);
			if(onlyFirst) return true;
		}
		else return false;
		
		while((pos=ArrayFindNoCase.call(pc, array, value))>0){
			array.removeE((int)pos);
		}
		
		return true;
	}
	
	@Override
	public Object invoke(PageContext pc, Object[] args) throws PageException {
		if(args.length==2)return call(pc,Caster.toArray(args[0]),args[1]);
		return call(pc,Caster.toArray(args[0]),args[1],Caster.toString(args[2]));
	}
	
}