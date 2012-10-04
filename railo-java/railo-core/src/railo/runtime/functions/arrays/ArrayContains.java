/**
 * Implements the CFML Function listcontains
 */
package railo.runtime.functions.arrays;

import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.ext.function.Function;
import railo.runtime.op.Caster;
import railo.runtime.op.Decision;
import railo.runtime.type.Array;
import railo.runtime.type.util.ArrayUtil;

public final class ArrayContains implements Function {
	
	private static final long serialVersionUID = -5400552848978801342L;

	public static double call(PageContext pc , Array array, Object value) throws PageException {
		String str=Caster.toString(value,null);
		if(str!=null) 
			return ArrayUtil.arrayContainsIgnoreEmpty(array,str,false)+1;
		return ArrayFind.call(pc, array, value);
	}
	

	
}