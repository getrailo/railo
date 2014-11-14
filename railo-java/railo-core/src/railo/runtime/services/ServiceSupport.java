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
package railo.runtime.services;

import java.util.HashMap;
import java.util.Map;

import railo.runtime.PageContext;
import railo.runtime.config.ConfigImpl;
import railo.runtime.config.ConfigWebUtil;
import railo.runtime.engine.ThreadLocalPageContext;
import railo.runtime.exp.SecurityException;
import coldfusion.server.Service;
import coldfusion.server.ServiceException;
import coldfusion.server.ServiceMetaData;

public class ServiceSupport implements Service {

	public void start() throws ServiceException {}

	public void stop() throws ServiceException {}

	public void restart() throws ServiceException {}

	public int getStatus() {
		return STARTED;
	}

	public ServiceMetaData getMetaData() {
		return new EmptyServiceMetaData();
	}

	public Object getProperty(String key) {return null;}

	public void setProperty(String key, Object value) {}

	public Map getResourceBundle() {
		return new HashMap();
	}	

    protected void checkWriteAccess() throws SecurityException {
    	ConfigWebUtil.checkGeneralWriteAccess(config(),"");
	}
    protected void checkReadAccess() throws SecurityException {
    	ConfigWebUtil.checkGeneralReadAccess(config(),"");
	}

	protected ConfigImpl config() {
		return (ConfigImpl) ThreadLocalPageContext.getConfig();
	}

	protected PageContext pc() {
		return ThreadLocalPageContext.get();
	}
}
