package railo.runtime.monitor;

import railo.runtime.config.ConfigServer;

public abstract class MonitorWrap implements Monitor {
	private static final Object[] PARAMS_LOG = new Object[0];

	private ConfigServer configServer;
	protected Object monitor;
	private String name;
	private short type;
	private boolean logEnabled;


	public MonitorWrap(Object monitor, short type) {
		this.monitor=monitor;
		this.type=type;
	}

	@Override
	public void init(ConfigServer configServer,String name, boolean logEnabled) {
		this.configServer=configServer;
		this.name=name;
		this.logEnabled=logEnabled;
	}

	@Override
	public short getType() {
		return type;
	}
	
	
	public Object getMonitor() {
		return monitor;
	}

	@Override
	public String getName() {
		return name;
	}
	public boolean isLogEnabled() {
		return logEnabled;
	}
	public Class getClazz() {
		return monitor.getClass();
	}

}
