/**
 * Implements the Cold Fusion Function writeoutput
 */
package railo.runtime.functions.other;


import java.io.PrintStream;

import railo.runtime.PageContext;
import railo.runtime.ext.function.Function;

public final class SystemOutput implements Function {
    public static boolean call(PageContext pc , String string) {
        return call(pc, string, false);
    }
    public static boolean call(PageContext pc , String string, boolean addNewLine) {
        return call(pc, string, addNewLine, false);
    }
    public static boolean call(PageContext pc , String string, boolean addNewLine,boolean doErrorStream) {
    	PrintStream stream = System.out;
    	//string+=":"+Thread.currentThread().getId();
    	if(doErrorStream) stream = System.err;
        
    	if(addNewLine)stream.println(string);
        else stream.print(string);
        return true;
    }
}