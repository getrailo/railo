package railo.runtime.functions.system;

import railo.commons.io.SystemUtil;
import railo.commons.io.res.util.ResourceUtil;
import railo.commons.lang.ExceptionUtil;
import railo.runtime.PageContext;
import railo.runtime.PageContextImpl;
import railo.runtime.ext.function.Function;
import railo.runtime.type.Array;
import railo.runtime.type.ArrayImpl;
import railo.runtime.type.Collection;
import railo.runtime.type.KeyImpl;
import railo.runtime.type.Struct;
import railo.runtime.type.StructImpl;
import railo.runtime.type.UDF;

/**
 * returns the root of this actuell Page Context
 */
public final class CallStackGet implements Function {

	private static final long serialVersionUID = -5853145189662102420L;
	static final Collection.Key LINE_NUMBER = KeyImpl.init("LineNumber");

	public static Array call(PageContext pc) {
		Array arr=new ArrayImpl();
		_getTagContext(pc, arr, new Exception("Stack trace"),LINE_NUMBER);
		return arr;
	}
	
	public static void _getTagContext(PageContext pc, Array tagContext, Throwable t,Collection.Key lineNumberName) {
		//Throwable root = t.getRootCause();
		Throwable cause = t.getCause(); 
		if(cause!=null)_getTagContext(pc, tagContext, cause,lineNumberName);
		StackTraceElement[] traces = t.getStackTrace();
		
		UDF[] udfs = ((PageContextImpl)pc).getUDFs();
		
        int line=0;
		String template;
		Struct item;
		StackTraceElement trace=null;
		String functionName;
		int index=udfs.length-1;
		for(int i=0;i<traces.length;i++) {
			trace=traces[i];
			template=trace.getFileName();
			if(trace.getLineNumber()<=0 || template==null || ResourceUtil.getExtension(template,"").equals("java")) continue;
			if("udfCall".equals(trace.getMethodName()) && index>-1) 
				functionName=udfs[index--].getFunctionName();
			
			else functionName="";
			
			item=new StructImpl();
			line=trace.getLineNumber();
			item.setEL(KeyImpl.FUNCTION,functionName);
			item.setEL(KeyImpl.TEMPLATE,template);
			item.setEL(lineNumberName,new Double(line));
			tagContext.appendEL(item);
		}
	}
}