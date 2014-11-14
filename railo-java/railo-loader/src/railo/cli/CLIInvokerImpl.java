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
package railo.cli;

import java.io.File;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;

import railo.cli.servlet.ServletConfigImpl;
import railo.cli.servlet.ServletContextImpl;
import railo.loader.engine.CFMLEngine;
import railo.loader.engine.CFMLEngineFactory;

public class CLIInvokerImpl implements CLIInvoker {

	private ServletConfigImpl servletConfig;
	private CFMLEngine engine;
	private long lastAccess;

	public CLIInvokerImpl(File root, String servletName) throws ServletException{
		Map<String,Object> attributes=new HashMap<String, Object>();
		Map<String, String> initParameters=new HashMap<String, String>();
		initParameters.put("railo-server-directory", new File(root,"server").getAbsolutePath());
		
		ServletContextImpl servletContext = new ServletContextImpl(root, attributes, initParameters, 1, 0);
		servletConfig = new ServletConfigImpl(servletContext, servletName);
		engine = CFMLEngineFactory.getInstance(servletConfig);
		
		 
	}
	
	@Override
	public void invoke(Map<String, String> config) throws RemoteException {
		try {
			engine.cli(config,servletConfig);
			lastAccess=System.currentTimeMillis();
		} catch (Throwable t) {
			throw new RemoteException("fail to call CFML Engine", t);
		}
	}

	public long lastAccess() {
		return lastAccess;
	}

}
