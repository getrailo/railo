package railo.runtime.type.trace;

import railo.commons.io.res.util.ResourceUtil;
import railo.runtime.PageContext;
import railo.runtime.debug.Debugger;
import railo.runtime.engine.ThreadLocalPageContext;
import railo.runtime.type.Array;
import railo.runtime.type.Query;
import railo.runtime.type.Struct;
import railo.runtime.type.UDFPlus;
import railo.runtime.type.dt.DateTime;

public class TraceObjectSupport implements TraceObject {


	
	protected Object o;
	protected  Debugger debugger;
	
	protected int type;
	protected String category;
	protected String text;

	
	public TraceObjectSupport(Debugger debugger,Object o,int type,String category,String text) {
		this.o=o;
		//this.log=log;
		this.type=type;
		this.category=category;
		this.text=text;
		this.debugger=debugger;
	}
	
	


	@Override
	
	public String toString() {
		
		return o.toString();
	}

	public boolean equals(Object obj) {
		
		return o.equals(obj);
	}
	

	protected void log() {
		try{log(debugger,type,category,text,null,null);}catch(Throwable t){}
	}
	
	protected void log(Object varName) {
		try{log(debugger,type,category,text,varName.toString(),null);}catch(Throwable t){}
	}
	
	protected void log(Object varName,Object varValue) {
		try{log(debugger,type,category,text,varName.toString(),varValue.toString());}catch(Throwable t){}
	}

	public synchronized static void log(Debugger debugger,int type,String category,String text,String varName,String varValue) {
		
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
					template.endsWith("Dump.cfc") || template.endsWith("dump.cfm"))// MUST bad impl 
				continue;
			line=trace.getLineNumber();
			break;
		}
		//print.e(t);
		if(line==0) return;
		
		/*String type=traces[2].getMethodName();
		
		int row=qry.addRow();
		qry.setAtEL(LABEL, row, label);
		qry.setAtEL(ACTION, row, type(type));
		qry.setAtEL(PARAMS, row, addional);
		*/
		String action=type(traces[2].getMethodName());
		if(debugger!=null)debugger.addTrace(type, category, text, template,line,action, varName, varValue);
			
	}	
	

	protected static String type(String type) {
		if(type.equals("appendEL")) return "append";
		if(type.equals("setEL")) return "set";
		if(type.equals("removeEL")) return "remove";
		if(type.equals("keys")) return "list";
		if(type.equals("toDumpData")) return "dump";
		
		
		return type;
	}
	


	protected PageContext pc() {
		return ThreadLocalPageContext.get();
	}
	
	public static TraceObject toTraceObject(Debugger debugger,Object obj, int type, String category, String text) {
		if(obj instanceof TraceObject)
			return (TraceObject) obj;
		else if(obj instanceof UDFPlus)
			return new TOUDF(debugger,(UDFPlus) obj,type,category,text);
		else if(obj instanceof Query)
			return new TOQuery(debugger,(Query) obj,type,category,text);
		else if(obj instanceof Array)
			return new TOArray(debugger,(Array) obj,type,category,text);
		else if(obj instanceof Struct)
			return new TOStruct(debugger,(Struct) obj,type,category,text);
		else if(obj instanceof DateTime)
			return new TODateTime(debugger,(DateTime) obj,type,category,text);
		
		
		return new TOObjects(debugger,obj,type,category,text);
	}
	
}
