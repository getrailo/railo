/**
 * Implements the CFML Function structkeyexists
 */
package railo.runtime.functions.arrays;

import railo.runtime.PageContext;
import railo.runtime.ext.function.Function;
import railo.runtime.type.Array;
import railo.runtime.type.Null;

public final class ArrayIndexExists implements Function {
    public static boolean call(PageContext pc , Array array, double index) {
        return array.get((int)index,Null.NULL)!=Null.NULL;
    }
}