package railo.runtime.engine;

import java.util.Map;

import railo.runtime.PageContext;

public class ExecutionLogFactory {
	
	private Class clazz;
	private Map<String, String> arguments;
	private ExecutionLog executionLog;

	public ExecutionLogFactory(Class clazz, Map<String, String> arguments){
		this.clazz=clazz;
		this.arguments=arguments;
	} 
	
	public ExecutionLog getInstance(PageContext pc){
		ExecutionLog el = executionLog;
		if(el==null) {
			
			try {
				el=executionLog = (ExecutionLog) clazz.newInstance();
			} catch (Exception e) {
				el= new ConsoleExecutionLog();
			}
			el.init(pc, arguments);
		}
		return el;
	}
	
	public String toString(){
		return super.toString()+":"+clazz.getName();
	}
}
