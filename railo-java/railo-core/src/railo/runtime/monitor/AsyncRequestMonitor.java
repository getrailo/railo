package railo.runtime.monitor;

import java.io.IOException;
import java.util.Map;

import railo.commons.io.log.Log;
import railo.commons.io.log.LogUtil;
import railo.runtime.PageContext;
import railo.runtime.config.ConfigImpl;
import railo.runtime.config.ConfigServer;
import railo.runtime.config.ConfigWeb;
import railo.runtime.engine.ThreadLocalPageContext;
import railo.runtime.exp.PageException;
import railo.runtime.type.Query;

public class AsyncRequestMonitor implements RequestMonitor {
	
	private RequestMonitor monitor;
	private boolean logEnabled;

	public AsyncRequestMonitor(RequestMonitor monitor){
		this.monitor=monitor;
	}
	
	@Override
	public void init(ConfigServer configServer, String name, boolean logEnabled) {
		monitor.init(configServer, name, logEnabled);
		this.logEnabled=logEnabled;
	}

	@Override
	public short getType() {
		return monitor.getType();
	}

	@Override
	public String getName() {
		return monitor.getName();
	}

	@Override
	public Class getClazz() {
		return monitor.getClazz();
	}

	@Override
	public boolean isLogEnabled() {
		return monitor.isLogEnabled();
	}

	@Override
	public Query getData(ConfigWeb config, Map<String, Object> arguments) throws PageException {
		return monitor.getData(config, arguments);
	}

	@Override
	public void log(PageContext pc, boolean error) throws IOException {
		new _Log(monitor,pc,error,logEnabled).start();
	}

	static class _Log extends Thread {
		private RequestMonitor monitor;
		private PageContext pc;
		private boolean error;
		private boolean logEnabled;

		public _Log(RequestMonitor monitor, PageContext pc, boolean error, boolean logEnabled) {
			this.monitor=monitor;
			this.pc=pc;
			this.error=error;
			this.logEnabled=logEnabled;
		}

		public void run(){
			try{
				ThreadLocalPageContext.register(pc);
				try {
					monitor.log(pc, error);
				}
				catch (IOException e) {
					if(logEnabled) {
						Log log=((ConfigImpl)pc.getConfig()).getLog("io");
						if(log!=null) LogUtil.log(log, Log.LEVEL_ERROR, "io", e);
					}
				}
			}
			finally{
				ThreadLocalPageContext.release();
			}
		}
	}
}
