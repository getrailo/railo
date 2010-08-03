/**
 * Implements the Cold Fusion Function arraydeleteat
 */
package railo.runtime.functions.arrays;

import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.ext.function.Function;
import railo.runtime.type.Array;


public final class ArrayDelete implements Function {
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
	
}