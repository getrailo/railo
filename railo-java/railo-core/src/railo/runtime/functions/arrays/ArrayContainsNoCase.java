/**
 * Implements the Cold Fusion Function listcontainsnocase
 */
package railo.runtime.functions.arrays;

import railo.runtime.PageContext;
import railo.runtime.ext.function.Function;
import railo.runtime.type.Array;
import railo.runtime.type.util.ArrayUtil;

public final class ArrayContainsNoCase implements Function {

	private static final long serialVersionUID = 4394078979692450076L;

	public static double call(PageContext pc , Array array, String value) {
		return ArrayUtil.arrayContainsIgnoreEmpty(array,value,true)+1;
	}
}