/**
 * Implements the Cold Fusion Function listcontains
 */
package railo.runtime.functions.arrays;

import railo.runtime.PageContext;
import railo.runtime.ext.function.Function;
import railo.runtime.type.Array;
import railo.runtime.type.util.ArrayUtil;

public final class ArrayContains implements Function {
	
	private static final long serialVersionUID = -5400552848978801342L;

	public static double call(PageContext pc , Array array, String value) {
		return ArrayUtil.arrayContainsIgnoreEmpty(array,value,false)+1;
	}
	

	
}