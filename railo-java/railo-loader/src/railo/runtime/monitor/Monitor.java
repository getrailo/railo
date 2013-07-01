package railo.runtime.monitor;

import railo.runtime.config.ConfigServer;

public interface Monitor {

	/**
	 * @deprecated use instead TYPE_INTERVAL
	 */
	public static final short TYPE_INTERVALL = 1;
	public static final short TYPE_INTERVAL = 1;
	public static final short TYPE_REQUEST = 2;
	public static final short TYPE_ACTION = 4;
	

	public void init(ConfigServer configServer, String name, boolean logEnabled);

	public short getType();
	public String getName();
	public Class getClazz();
	public boolean isLogEnabled(); 
}
