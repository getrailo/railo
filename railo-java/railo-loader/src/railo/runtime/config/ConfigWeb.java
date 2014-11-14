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

import railo.commons.io.res.Resource;
import railo.runtime.lock.LockManager;

/**
 * Web Context
 */
public interface ConfigWeb extends Config {

    /**
     * @return lockmanager
     */
    public abstract LockManager getLockManager();

	/**
	 * @return return if is allowed to define request timeout via URL
	 */
	public abstract boolean isAllowURLRequestTimeout();

	public abstract String getServerId();

	public String getLabel();

	public abstract Resource getConfigServerDir();
}