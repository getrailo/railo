

package coldfusion.server;

import java.util.Map;

public interface VerityService extends Service {

	public abstract String getCollectiondir();

	public abstract long getDocumentCount();

	public abstract Map getCollectionInfo(String arg0);

	public abstract void registerCollection(String arg0, String arg1,
			String arg2);

}