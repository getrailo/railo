package railo.runtime.monitor;

import java.io.IOException;
import java.util.Map;

import railo.runtime.PageContext;
import railo.runtime.config.ConfigWeb;
import railo.runtime.exp.PageException;
import railo.runtime.type.Query;

// added with Railo 4.1
public interface ActionMonitor extends Monitor {
	
	/**
	 *  logs certain action within a Request
	 * @param pc
	 * @param ar
	 * @throws IOException
	 */
	public void log(PageContext pc, String type, String label, long executionTime, Object data) throws IOException;
	
	/**
	 *  logs certain action outside a Request, like sending mails
	 * @param pc
	 * @param ar
	 * @throws IOException
	 */
	public void log(ConfigWeb config, String type, String label, long executionTime, Object data) throws IOException;


	public Query getData(Map<String,Object> arguments) throws PageException;
}
