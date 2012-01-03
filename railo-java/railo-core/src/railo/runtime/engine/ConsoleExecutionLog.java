package railo.runtime.engine;

import java.io.PrintWriter;
import java.util.Map;

import railo.commons.lang.SystemOut;
import railo.runtime.PageContext;

public class ConsoleExecutionLog extends ExecutionLogSupport {
	
	private PrintWriter pw;
	private PageContext pc;
	
	protected void _init(PageContext pc,Map<String,String> arguments) {
		this.pc=pc;
		
		if(pw==null) {
			// stream type
			String type=arguments.get("stream-type");
			if(type!=null && type.trim().equalsIgnoreCase("error"))
				pw=new PrintWriter(System.err);
			else
				pw=new PrintWriter(System.out);
			
		}
	}
	
	@Override
	protected void _log(int startLine, int endLine, long startTime, long endTime) {
		long diff=endTime-startTime;
		SystemOut.print(pw, pc.getId()+":"+pc.getCurrentPageSource().getDisplayPath()+":"+lines(startLine,endLine)+" > "+timeLongToString(diff));	
	}
	
	protected void _release() {}
	
	private static String lines(int startLine, int endLine) {
		if(startLine==endLine) return startLine+"";
		return startLine+":"+endLine;
	}

}
