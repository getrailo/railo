package railo.runtime.monitor;

import railo.runtime.config.ConfigServer;

public interface Monitor {

	public static final short TYPE_INTERVALL = 1;// FUTURE change to INTERVAL
	public static final short TYPE_REQUEST = 2;
	public static final short TYPE_ACTION = 4;// added with Railo 4.1
	

	public void init(ConfigServer configServer, String name, boolean logEnabled);

	public short getType();
	public String getName();
	public Class getClazz();
	public boolean isLogEnabled(); 
}
