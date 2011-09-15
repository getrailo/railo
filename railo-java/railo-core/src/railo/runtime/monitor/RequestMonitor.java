package railo.runtime.monitor;

import java.io.IOException;
import java.util.Map;

import railo.runtime.PageContext;
import railo.runtime.config.ConfigWeb;
import railo.runtime.exp.PageException;
import railo.runtime.type.Query;

public interface RequestMonitor extends Monitor {
	public void log(PageContext pc, boolean error) throws IOException;
	public Query getData(ConfigWeb config,Map<String,Object> arguments) throws PageException;
}
