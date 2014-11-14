/**
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either 
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public 
 * License along with this library.  If not, see <http://www.gnu.org/licenses/>.
 * 
 **/
package railo.runtime.config;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

import railo.loader.engine.CFMLEngine;
import railo.runtime.CFMLFactory;
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
     * @param relpath
     * @return returns config web matching given relpath
     */
    public abstract ConfigWeb getConfigWeb(String relpath);

    /**
     * @return Returns the contextes.
     */
    public abstract Map<String,CFMLFactory> getJSPFactoriesAsMap();

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

    public abstract CFMLEngine getCFMLEngine();

}