

package coldfusion.server;

import java.util.Map;

import org.apache.axis.client.Stub;

public interface XmlRpcService extends Service {

	public abstract Map getMappings();

	public abstract Map getUsernames();

	public abstract Map getPasswords();

	public abstract void unregisterWebService(String arg0);

	public abstract void refreshWebService(String arg0);

	public abstract void registerWebService(String arg0, String arg1,
			String arg2, String arg3, int arg4, String arg5, String arg6,
			String arg7, String arg8);

	public abstract Stub getWebService(String arg0, String arg1, String arg2,
			int arg3, String arg4, String arg5, String arg6, String arg7,
			String arg8);

	// public abstract ServiceProxy getWebServiceProxy(String arg0, String arg1,String arg2, String arg3);

	// public abstract ServiceProxy getWebServiceProxy(String arg0, String arg1,String arg2, int arg3, String arg4, String arg5, String arg6,String arg7, String arg8);

	public abstract String getClassPath();

	public abstract void setClassPath(String arg0);

}