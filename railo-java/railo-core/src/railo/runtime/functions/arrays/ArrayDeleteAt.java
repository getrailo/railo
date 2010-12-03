/**
 * Implements the Cold Fusion Function arraydeleteat
 */
package railo.runtime.functions.arrays;

import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.ext.function.Function;
import railo.runtime.type.Array;


public final class ArrayDeleteAt implements Function {
	
	private static final long serialVersionUID = -5900967522809749154L;

	public static boolean call(PageContext pc , Array array, double number) throws PageException {
		//print.out(array);
		return array.removeE((int)number)!=null;
	}
}