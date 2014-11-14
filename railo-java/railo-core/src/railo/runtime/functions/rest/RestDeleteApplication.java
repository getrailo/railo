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
package railo.runtime.functions.rest;

import railo.commons.io.res.Resource;
import railo.commons.io.res.util.ResourceUtil;
import railo.commons.lang.StringUtil;
import railo.runtime.PageContext;
import railo.runtime.config.ConfigWebAdmin;
import railo.runtime.config.ConfigWebImpl;
import railo.runtime.exp.FunctionException;
import railo.runtime.exp.PageException;
import railo.runtime.exp.SecurityException;
import railo.runtime.listener.ApplicationContext;
import railo.runtime.listener.ModernApplicationContext;
import railo.runtime.op.Caster;
import railo.runtime.rest.Mapping;
import railo.runtime.rest.RestUtil;
import railo.runtime.type.KeyImpl;

public class RestDeleteApplication {
	public static String call(PageContext pc , String dirPath) throws PageException {
		return call(pc, dirPath,null);
	}

	public static String call(PageContext pc , String dirPath,String webAdminPassword) throws PageException {
		webAdminPassword=getPassword(pc, webAdminPassword);
    	
		Resource dir=RestDeleteApplication.toResource(pc,dirPath);
		ConfigWebImpl config=(ConfigWebImpl) pc.getConfig();

		try {
			ConfigWebAdmin admin = ConfigWebAdmin.newInstance((ConfigWebImpl)pc.getConfig(),webAdminPassword);
			Mapping[] mappings = config.getRestMappings();
			Mapping mapping;
			for(int i=0;i<mappings.length;i++){
				mapping=mappings[i];
				if(RestUtil.isMatch(pc,mapping,dir)){
					admin.removeRestMapping(mapping.getVirtual());
					admin.store();
				}
			}
		} 
    	catch (Exception e) {
			throw Caster.toPageException(e);
		}
    	
    	
		return null;
	}
	
	static String getPassword(PageContext pc,String password) throws SecurityException {
		if(!StringUtil.isEmpty(password, true)) {
			password=password.trim();
		}
		else {
			ApplicationContext ac = pc.getApplicationContext();
			if(ac instanceof ModernApplicationContext) {
				ModernApplicationContext mac=(ModernApplicationContext) ac;
				password = Caster.toString(mac.getCustom(KeyImpl.init("webAdminPassword")),null);
			}
		}
		if(StringUtil.isEmpty(password, true))
			throw new SecurityException("To manipulate a REST mapping you need to define the password for the current Web Administartor, " +
					"you can do this as argument with this function or inside the application.cfc with the variable [this.webAdminPassword].");

		return password;
	}
	
	static Resource toResource(PageContext pc, String dirPath) throws PageException {
		Resource dir = ResourceUtil.toResourceNotExisting(pc.getConfig(), dirPath);
		pc.getConfig().getSecurityManager().checkFileLocation(dir);
		if(!dir.isDirectory())
			throw new FunctionException(pc, "RestInitApplication", 1, "dirPath", "argument value ["+dirPath+"] must contain a existing directory");
		
		return dir;
	}
}
