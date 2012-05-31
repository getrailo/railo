package railo.runtime.config;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

import railo.loader.engine.CFMLEngine;
import railo.runtime.exp.PageException;
import railo.runtime.monitor.IntervallMonitor;
import railo.runtime.monitor.RequestMonitor;
import railo.runtime.security.SecurityManager;

/**
 * Config for the server
 */
public interface ConfigServer extends Config {

    /**
     * @return returns all config webs
     */
    public abstract ConfigWeb[] getConfigWebs();

    /**
     * @param realpath
     * @return returns config web matching given realpath
     */
    public abstract ConfigWeb getConfigWeb(String realpath);

    /**
     * @return Returns the contextes.
     */
    public abstract Map getJSPFactoriesAsMap();

    /**
     * @param id
     * @return returns SecurityManager matching config
     */
    public abstract SecurityManager getSecurityManager(String id);

    /**
     * is there a individual security manager for given id
     * @param id for the security manager
     * @return returns SecurityManager matching config
     */
    public abstract boolean hasIndividualSecurityManager(String id);

    /**
     * @return Returns the securityManager.
     */
    public abstract SecurityManager getDefaultSecurityManager();

    /**
     * @return Returns the engine.
     */
    public abstract CFMLEngine getCFMLEngine();

    /**
     * @param updateType The updateType to set.
     */
    public abstract void setUpdateType(String updateType);

    /**
     * @param updateLocation The updateLocation to set.
     */
    public abstract void setUpdateLocation(URL updateLocation);

    /**
     * @param strUpdateLocation The updateLocation to set.
     * @throws MalformedURLException 
     */
    public abstract void setUpdateLocation(String strUpdateLocation)
            throws MalformedURLException;

    /**
     * @param strUpdateLocation The updateLocation to set.
     * @param defaultValue 
     */
    public abstract void setUpdateLocation(String strUpdateLocation,
            URL defaultValue);
    
	/**
	 * @return the configListener
	 */
	public ConfigListener getConfigListener();

	/**
	 * @param configListener the configListener to set
	 */
	public void setConfigListener(ConfigListener configListener);

	public RemoteClient[] getRemoteClients();
	
	public RequestMonitor[] getRequestMonitors();
	
	public RequestMonitor getRequestMonitor(String name) throws PageException;
	
	public IntervallMonitor[] getIntervallMonitors();

	public IntervallMonitor getIntervallMonitor(String name) throws PageException;


}