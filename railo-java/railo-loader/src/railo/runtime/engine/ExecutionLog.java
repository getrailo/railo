package railo.runtime.engine;

import java.util.Map;

import railo.runtime.PageContext;

public interface ExecutionLog {
	public void init(PageContext pc,Map<String,String> arguments);
	public void start();
	public void endline(int line);
}
