package coldfusion.server;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletContext;

public interface GraphingService extends Service {

	public abstract Map getSettings();

	public abstract int getCacheType();

	public abstract String getCachePath();

	public abstract int getCacheSize();

	public abstract int getMaxEngines();

	public abstract String generateGraph(String arg0, int arg1, int arg2,
			String arg3, String arg4, String arg5, String arg6, boolean arg7);

	public abstract String generateGraph(String arg0, int arg1, int arg2,
			String arg3, String arg4, String arg5, String arg6);

	public abstract byte[] generateBytes(String arg0, int arg1, int arg2,
			String arg3, String arg4, String arg5) throws IOException;

	public abstract byte[] generateBytes(String arg0, int arg1, int arg2,
			String arg3, String arg4, String arg5, boolean arg6)
			throws IOException;

	public abstract byte[] getGraphData(String arg0, ServletContext arg1,
			boolean arg2) throws IOException;

	public abstract void initializeEngine(ServletContext arg0);

	public abstract void setUpWatermark();

}