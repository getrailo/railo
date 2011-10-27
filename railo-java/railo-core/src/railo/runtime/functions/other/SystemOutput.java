/**
 * Implements the Cold Fusion Function writeoutput
 */
package railo.runtime.functions.other;


import java.io.PrintStream;

import railo.commons.lang.ExceptionUtil;
import railo.commons.lang.StringUtil;
import railo.runtime.PageContext;
import railo.runtime.ext.function.Function;

public final class SystemOutput implements Function {
    public static boolean call(PageContext pc , String string) {
        return call(pc, string, false,false);
    }
    public static boolean call(PageContext pc , String string, boolean addNewLine) {
        return call(pc, string, addNewLine, false);
    }
    public static boolean call(PageContext pc , String string, boolean addNewLine,boolean doErrorStream) {
    	PrintStream stream = System.out;
    	//string+=":"+Thread.currentThread().getId();
    	if(doErrorStream) stream = System.err;
        if(string!=null && StringUtil.indexOfIgnoreCase(string,"<print-stack-trace>")!=-1){
        	String st = ExceptionUtil.getStacktrace(new Exception("Stack trace"), false);
        	string=StringUtil.replace(string, "<print-stack-trace>", "\n"+st+"\n", true).trim();
        }
        if(addNewLine)stream.println(string);
        else stream.print(string);
        
    	return true;
    }
}