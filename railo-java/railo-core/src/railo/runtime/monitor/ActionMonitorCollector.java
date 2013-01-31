package railo.runtime.monitor;

import java.io.IOException;

import railo.runtime.PageContext;
import railo.runtime.config.ConfigServer;
import railo.runtime.config.ConfigWeb;

public interface ActionMonitorCollector {
	public void addMonitor(ConfigServer cs, Object monitor, String name, boolean log) throws IOException;
	public void log(PageContext pc, String type, String label, long executionTime, Object data);
	public void log(ConfigWeb config, String type, String label, long executionTime, Object data);
	public Object getActionMonitor(String name); // FUTURE return ActionMonitor
}
