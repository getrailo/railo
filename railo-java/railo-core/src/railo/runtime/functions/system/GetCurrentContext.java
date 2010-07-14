package railo.runtime.functions.system;

import railo.commons.io.res.util.ResourceUtil;
import railo.runtime.PageContext;
import railo.runtime.ext.function.Function;
import railo.runtime.type.Array;
import railo.runtime.type.ArrayImpl;
import railo.runtime.type.Struct;
import railo.runtime.type.StructImpl;

/**
 * returns the root of this actuell Page Context
 */
public final class GetCurrentContext implements Function {
	
	public static Array call(PageContext pc) {
		Array arr=new ArrayImpl();
		_getTagContext(pc, arr, new Exception("Stack trace"));
		return arr;
	}
	
	private static void _getTagContext(PageContext pc, Array tagContext, Throwable t) {
		//Throwable root = t.getRootCause();
		Throwable cause = t.getCause(); 
		if(cause!=null)_getTagContext(pc, tagContext, cause);
		StackTraceElement[] traces = t.getStackTrace();
		
        int line=0;
		String template;
		Struct item;
		StackTraceElement trace=null;
		for(int i=0;i<traces.length;i++) {
			trace=traces[i];
			template=trace.getFileName();
			if(trace.getLineNumber()<=0 || template==null || ResourceUtil.getExtension(template).equals("java")) continue;
			
			item=new StructImpl();
			line=trace.getLineNumber();
			item.setEL("template",template);
			item.setEL("line",new Double(line));
			tagContext.appendEL(item);
		}
	}
}