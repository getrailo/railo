/**
 * Implements the Cold Fusion Function writeoutput
 */
package railo.runtime.functions.other;


import railo.runtime.PageContext;
import railo.runtime.ext.function.Function;

public final class SystemOutput implements Function {
    public static boolean call(PageContext pc , String string) {
        return call(pc, string, false);
    }
    public static boolean call(PageContext pc , String string, boolean addNewLine) {
        if(addNewLine)System.out.println(string);
        else System.out.print(string);
        return true;
    }
}