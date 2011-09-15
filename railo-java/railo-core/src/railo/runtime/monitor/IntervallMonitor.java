package railo.runtime.monitor;

import java.io.IOException;
import java.util.Map;

import railo.runtime.config.ConfigWeb;
import railo.runtime.exp.PageException;
import railo.runtime.type.Query;

// FUTURE add to interface
public interface IntervallMonitor extends Monitor {
	public void log() throws IOException;
	public Query getData(Map<String,Object> arguments) throws PageException;
}
