package railo.runtime.monitor;

import java.io.IOException;

import railo.commons.io.SystemUtil;
import railo.runtime.config.ConfigServer;
import railo.runtime.config.ConfigWebFactory;
import railo.runtime.config.ConfigWebFactory.MonitorTemp;

public class ActionMonitorFatory {
	public static ActionMonitorCollector getActionMonitorCollector() {
		if(SystemUtil.getLoaderVersion()>4) return new ActionMonitorCollectorImpl();
		return new ActionMonitorCollectorRefImpl();
	}

	public static ActionMonitorCollector getActionMonitorCollector(ConfigServer cs, ConfigWebFactory.MonitorTemp[] temps) throws IOException {
		// try to load with interface
		try{
			ActionMonitorCollector collector = new ActionMonitorCollectorImpl();
			addMonitors(collector,cs,temps);
			return collector;
		}
		catch(Throwable t){t.printStackTrace();
			ActionMonitorCollector collector = new ActionMonitorCollectorRefImpl();
			addMonitors(collector,cs,temps);
			return collector;
		}
	}

	private static void addMonitors(ActionMonitorCollector collector, ConfigServer cs, MonitorTemp[] temps) throws IOException {
		MonitorTemp temp;
		for(int i=0;i<temps.length;i++){
			temp=temps[i];
			collector.addMonitor(cs, temp.obj, temp.name, temp.log);
		}
	}
}
