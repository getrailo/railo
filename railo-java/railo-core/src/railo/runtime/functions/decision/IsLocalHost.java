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
package railo.runtime.functions.decision;

import java.net.InetAddress;
import java.net.UnknownHostException;

import railo.commons.lang.StringUtil;
import railo.runtime.PageContext;
import railo.runtime.ext.function.Function;

/**
 * Implements the CFML Function isleapyear
 */
public final class IsLocalHost implements Function {
	
	private static final long serialVersionUID = 5680807516948697186L;
	
	public static boolean call(PageContext pc , String ip) {
		return invoke(ip);
	}
	public static boolean invoke(String ip) {
		
		if(StringUtil.isEmpty(ip,true)) return false;
		ip=ip.trim().toLowerCase();
		if(
				ip.equalsIgnoreCase("localhost") || 
				ip.equals("127.0.0.1") || 
				ip.equalsIgnoreCase("0:0:0:0:0:0:0:1") || 
				ip.equalsIgnoreCase("0:0:0:0:0:0:0:1%0") || 
				ip.equalsIgnoreCase("::1"))
			return true;

		try {
			InetAddress addr = InetAddress.getByName(ip);
			InetAddress localHost = InetAddress.getLocalHost();
			if(localHost.equals(addr)) return true;
			
			InetAddress localHosts[] = InetAddress.getAllByName(localHost.getHostName());
			
			for(int i=0;i<localHosts.length;i++){
				if(localHosts[i].equals(addr)) return true;
			}
        }
        catch(UnknownHostException e){}
        
        return false;
	}
}