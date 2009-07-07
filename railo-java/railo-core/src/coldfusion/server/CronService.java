package coldfusion.server;

import java.util.Date;
import java.util.List;


public interface CronService extends Service {

	public abstract void updateTask(String arg0, String arg1, String arg2,
			String arg3, String arg4, Date arg5, Date arg6, Date arg7,
			Date arg8, String arg9, boolean arg10, String arg11, String arg12,
			String arg13, String arg14, String arg15, String arg16,
			boolean arg17, String arg18, String arg19) throws ServiceException;

	public abstract List listAll();

	public abstract String list();

	//public abstract CronTabEntry findTask(String arg0);

	public abstract void deleteTask(String arg0) throws ServiceException;

	public abstract void runCall(String arg0) throws ServiceException;

	public abstract void setLogFlag(boolean arg0) throws ServiceException;

	public abstract boolean getLogFlag();

	//public abstract void updateTasks(ConfigMap arg0) throws ServiceException;

	public abstract void saveCronEntries();

}