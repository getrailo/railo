

package coldfusion.server;

import java.util.Map;

public interface DebuggingService extends Service {

	//public abstract Debugger getDebugger();

	public abstract void reset(int arg0);

	public abstract void reset();

	public abstract long getDebuggerStartTime();

	public abstract Map getSettings();

	public abstract String getDebugTemplate();

	public abstract String getXMLTemplate();

	public abstract Map getIplist();

	public abstract boolean getShowdebug();

	public abstract void setShowdebug(boolean arg0);

	public abstract boolean isEnabled();

	public abstract void setEnabled(boolean arg0);

	public abstract boolean isRobustEnabled();

	public abstract void setRobustEnabled(boolean arg0);

	public abstract boolean check(String arg0);

	public abstract boolean check(int arg0);

	public abstract boolean isValidIP(String arg0);

	public abstract boolean isTimerEnabled();

	public abstract boolean isFlashFormCompileErrorsEnabled();

}