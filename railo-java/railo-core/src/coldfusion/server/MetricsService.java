package coldfusion.server;

import java.util.Hashtable;

public interface MetricsService extends Service {

	public abstract int getMetric(String arg0);

	public abstract void updateMetric(String arg0, int arg1);

	public abstract Hashtable getPerfmonMetrics();

	public abstract Hashtable getSnapshot();

}