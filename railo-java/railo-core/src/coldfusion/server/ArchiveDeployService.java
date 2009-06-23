

package coldfusion.server;

import java.util.Map;

public interface ArchiveDeployService extends Service {

	public abstract Map getArchives();

	public abstract Map getSettings();

	public abstract Map getArchive(String arg0);

	public abstract String getWorkingDirectory();

	public abstract void setWorkingDirectory(String arg0);

	public abstract void archive(String arg0, String arg1) throws ServiceException;

	//public abstract void deploy(Archive arg0) throws ServiceException;
}