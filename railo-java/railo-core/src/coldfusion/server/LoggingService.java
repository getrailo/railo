

package coldfusion.server;

import java.util.Map;

public interface LoggingService extends Service {

	//public abstract Logger getLogger(String arg0);

	public abstract Map getSettings();

	public abstract void setSettings(Map arg0);

	//public abstract Logger getArchiveLog(String arg0);

	public abstract void registerWithWatchService();

	public abstract void setEnableWatch(boolean arg0);

}