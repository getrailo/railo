package railo.runtime.type.trace;

import railo.commons.io.log.LogResource;
import railo.commons.io.res.util.ResourceUtil;
import railo.commons.lang.StringUtil;
import railo.runtime.PageContext;
import railo.runtime.engine.ThreadLocalPageContext;
import railo.runtime.type.Array;
import railo.runtime.type.QueryPro;
import railo.runtime.type.Struct;
import railo.runtime.type.UDF;
import railo.runtime.type.dt.DateTime;

public class TraceObjectSupport implements TraceObject {
	
	protected Object o;
	protected LogResource log;
	protected String label;

	public TraceObjectSupport(Object o,LogResource log, String label) {
		this.o=o;
		this.log=log;
		this.label=label;
	}
	
	


	/**
	 * @see railo.runtime.type.QueryImpl#toString()
	 */
	
	public String toString() {
		
		return o.toString();
	}

	public boolean equals(Object obj) {
		
		return o.equals(obj);
	}
	

	protected void log(String addional) {
		log(log,label,addional);
	}

	public static void log(LogResource log, String label,String addional) {
		
		Throwable t=new Exception("Stack trace");
		Throwable cause = t.getCause(); 
		while(cause!=null){
			t=cause;
			cause = t.getCause(); 
		}
		StackTraceElement[] traces = t.getStackTrace();
		
        int line=0;
		String template=null;
		StackTraceElement trace=null;
		for(int i=0;i<traces.length;i++) {
			trace=traces[i];
			template=trace.getFileName();
			if(trace.getLineNumber()<=0 || template==null || ResourceUtil.getExtension(template,"").equals("java") ||
					template.endsWith("Dump.cfc"))// MUST bad impl 
				continue;
			line=trace.getLineNumber();
			break;
		}
		//print.e(t);
		if(line==0) return;
		
		String type=traces[1].getMethodName();
		
		log.info(label, type(type)+(StringUtil.isEmpty(addional)?"":" ["+addional+"]")+" at "+template+":"+line);
	}	
	

	protected static String type(String type) {
		if(type.equals("setEL")) return "set";
		if(type.equals("removeEL")) return "remove";
		if(type.equals("keys")) return "list";
		return type;
	}
	


	protected PageContext pc() {
		return ThreadLocalPageContext.get();
	}
	
	public static TraceObject toTraceObject(Object obj, String label, LogResource log) {
		if(obj instanceof TraceObject)
			return (TraceObject) obj;
		else if(obj instanceof UDF)
			return new TOUDF((UDF) obj, label, log);
		else if(obj instanceof QueryPro)
			return new TOQuery((QueryPro) obj, label, log);
		else if(obj instanceof Array)
			return new TOArray((Array) obj, label, log);
		else if(obj instanceof Struct)
			return new TOStruct((Struct) obj, label, log);
		else if(obj instanceof DateTime)
			return new TODateTime((DateTime) obj, label, log);
		
		
		return new TOObjects(obj, label, log);
	}
	
}
