package railo.runtime.gateway;

import railo.commons.lang.ClassException;
import railo.runtime.config.Config;
import railo.runtime.exp.PageException;
import railo.runtime.type.Struct;

public interface GatewayEntry {


	public static int STARTUP_MODE_AUTOMATIC = 1;
	public static int STARTUP_MODE_MANUAL = 2;
	public static int STARTUP_MODE_DISABLED = 4;
	

	/**
	 * @return the gateway
	 * @throws ClassException 
	 * @throws PageException 
	 */
	public void createGateway(Config config) throws ClassException,PageException;
	
	public GatewayPro getGateway() ;

	
	/**
	 * @return the id
	 */
	public abstract String getId();

	
	//public abstract Class getClazz();

	/**
	 * @return the custom
	 */
	public abstract Struct getCustom();

	/**
	 * @return the readOnly
	 */
	public abstract boolean isReadOnly();
	

	/**
	 * @return the cfcPath
	 */
	public String getListenerCfcPath();
	
	public String getCfcPath();

	/**
	 * @return the startupMode
	 */
	public int getStartupMode();


	public String getClassName();
	

}