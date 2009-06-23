

package coldfusion.server;

import java.util.Map;

public interface RegistryService extends Service {

	public abstract void set(String arg0, String arg1, String arg2, String arg3);

	public abstract Map getAll(String arg0, String arg1);

	public abstract Object get(String arg0, String arg1, String arg2);

	//public abstract void delete(String arg0, String arg1) throws RegistryException;

	public abstract void flush();

	public abstract void clearDirtyBit();

	public abstract Map dump();

	public abstract boolean isCrossPlatform();

}