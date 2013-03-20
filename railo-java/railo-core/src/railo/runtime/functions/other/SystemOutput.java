/**
 * Implements the CFML Function writeoutput
 */
package railo.runtime.functions.other;


import java.io.PrintStream;

import railo.commons.lang.ExceptionUtil;
import railo.commons.lang.StringUtil;
import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.ext.function.Function;
import railo.runtime.functions.dynamicEvaluation.Serialize;
import railo.runtime.op.Caster;
import railo.runtime.op.Decision;

public final class SystemOutput implements Function {
    public static boolean call(PageContext pc , Object obj) throws PageException {
        return call(pc, obj, false,false);
    }
    public static boolean call(PageContext pc , Object obj, boolean addNewLine) throws PageException {
        return call(pc, obj, addNewLine, false);
    }
    public static boolean call(PageContext pc , Object obj, boolean addNewLine,boolean doErrorStream) throws PageException {
    	String string;
    	if(Decision.isSimpleValue(obj))string=Caster.toString(obj);
    	else {
    		try{
    			string=Serialize.call(pc, obj);
    		}
    		catch(Throwable t){
    			string=obj.toString();
    		}
    	}
    	PrintStream stream = System.out;
    	//string+=":"+Thread.currentThread().getId();
    	if(doErrorStream) stream = System.err;
    	if(string!=null) {
	    	if(StringUtil.indexOfIgnoreCase(string,"<print-stack-trace>")!=-1){
	        	String st = ExceptionUtil.getStacktrace(new Exception("Stack trace"), false);
	        	string=StringUtil.replace(string, "<print-stack-trace>", "\n"+st+"\n", true).trim();
	        }
	    	if(StringUtil.indexOfIgnoreCase(string,"<hash-code>")!=-1){
	        	String st = obj.hashCode()+"";
	        	string=StringUtil.replace(string, "<hash-code>", st, true).trim();
	        }
    	}
        if(addNewLine)stream.println(string);
        else stream.print(string);
        
    	return true;
    }
}