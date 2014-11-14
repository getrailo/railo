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
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.jsp.JspException;

import railo.loader.util.Util;

public class CLI {
	
	private static boolean useRMI=false;
/**
 * Config
 * 
 * webroot - webroot directory
 * servlet-name - name of the servlet (default:CFMLServlet)
 * server-name - server name (default:localhost)
 * uri - host/scriptname/query
 * cookie - cookies (same pattern as query string)
 * form - form (same pattern as query string)
 */
	
	
	/**
	 * @param args
	 * @throws JspException 
	 */
	public static void main(String[] args) throws ServletException, IOException, JspException {
		Map<String,String> config=toMap(args);
		
		System.setProperty("railo.cli.call", "true");
		
		// webroot
		String strWebroot=config.get("webroot");
		if(Util.isEmpty(strWebroot,true)) throw new IOException("missing webroot configuration");
		File root=new File(strWebroot);
		root.mkdirs();
		
		// servletNane
		String servletName=config.get("servlet-name");
		if(Util.isEmpty(servletName,true))servletName="CFMLServlet";
		if(useRMI){
			CLIFactory factory = new CLIFactory(root,servletName,config);
			factory.setDaemon(false);
			factory.start();
		}
		else {
			CLIInvokerImpl invoker=new CLIInvokerImpl(root, servletName);
			invoker.invoke(config);
		}
		//Map<String,Object> attributes=new HashMap<String, Object>();
		//Map<String, String> initParameters=new HashMap<String, String>();
		//initParameters.put("railo-server-directory", new File(root,"server").getAbsolutePath());
		
		
		//ServletContextImpl servletContext = new ServletContextImpl(root, attributes, initParameters, 1, 0);
		//ServletConfigImpl servletConfig = new ServletConfigImpl(servletContext, servletName);
		//CFMLEngine engine = CFMLEngineFactory.getInstance(servletConfig);
		//engine.cli(config,servletConfig);
		

	}
// java railo-cli.jar -config=.../railo-web.xml.cfm -uri=/susi/index.cfm?test=1 -form=name=susi -cgi=user_agent=urs -output=.../test.txt ...

	private static Map<String, String> toMap(String[] args) {
		int index;
		Map<String, String> config=new HashMap<String, String>();
		String raw,key,value;
		if(args!=null)for(int i=0;i<args.length;i++){
			raw=args[i].trim();
			if(Util.isEmpty(raw, true)) continue;
			if(raw.startsWith("-"))raw=raw.substring(1).trim();
			index=raw.indexOf('=');
			if(index==-1) {
				key=raw;
				value="";
			}
			else {
				key=raw.substring(0,index).trim();
				value=raw.substring(index+1).trim();
			}
			config.put(key.toLowerCase(), value);
		}
		return config;
	}
}
