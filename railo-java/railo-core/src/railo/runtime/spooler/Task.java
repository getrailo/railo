package railo.runtime.spooler;

import railo.runtime.config.Config;
import railo.runtime.exp.PageException;

// FUTURE add to public interface
public interface Task {
	
	public Object execute(Config config) throws PageException;

}
