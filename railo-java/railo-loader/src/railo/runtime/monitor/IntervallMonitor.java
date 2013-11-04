package railo.runtime.monitor;

import java.io.IOException;
import java.util.Map;

import railo.runtime.exp.PageException;
import railo.runtime.type.Query;

public interface IntervallMonitor extends Monitor {
	// FUTURE public void init(ConfigServer cs);
	public void log() throws IOException;
	public Query getData(Map<String,Object> arguments) throws PageException;
}
