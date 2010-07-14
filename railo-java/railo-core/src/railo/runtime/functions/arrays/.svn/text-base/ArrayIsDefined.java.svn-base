/**
 * Implements the Cold Fusion Function structkeyexists
 */
package railo.runtime.functions.arrays;


import railo.runtime.PageContext;
import railo.runtime.ext.function.Function;
import railo.runtime.type.Array;

public final class ArrayIsDefined implements Function {
    public static boolean call(PageContext pc , Array array, double index) {
    	return ArrayIndexExists.call(pc, array, index);
    }
}