

package coldfusion.server;

import java.util.Map;

public interface Service {

	public static final int UNINITALIZED=1;
	public static final int STARTING=2;
	public static final int STARTED=4;
	public static final int STOPPING=8;
	public static final int STOOPED=16;
	
	public abstract void start() throws ServiceException;

	public abstract void stop() throws ServiceException;

	public abstract void restart() throws ServiceException;

	public abstract int getStatus();

	public abstract ServiceMetaData getMetaData();

	public abstract Object getProperty(String arg0);

	public abstract void setProperty(String arg0, Object arg1);

	public abstract Map getResourceBundle();

}