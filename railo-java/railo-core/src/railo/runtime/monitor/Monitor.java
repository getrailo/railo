package railo.runtime.monitor;

import railo.runtime.config.ConfigServer;

public interface Monitor {

	public static final short TYPE_INTERVALL = 1;
	public static final short TYPE_REQUEST = 2;
	

	public void init(ConfigServer configServer, String name, boolean logEnabled);

	public short getType();
	public String getName();
	public Class getClazz();
	public boolean isLogEnabled(); 
}
