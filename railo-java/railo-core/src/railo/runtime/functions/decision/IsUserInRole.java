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
/**
 * Implements the CFML Function isuserinrole
 */
package railo.runtime.functions.decision;

import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.ext.function.Function;
import railo.runtime.security.Credential;
import railo.runtime.security.CredentialImpl;

public final class IsUserInRole implements Function {
	public static boolean call(PageContext pc , Object object) throws PageException {
	    String[] givenRoles = CredentialImpl.toRole(object);
	    Credential ru = pc.getRemoteUser();
	    if(ru==null) return false;
	    String[] roles = ru.getRoles();
	    for(int i=0;i<roles.length;i++) {
	        for(int y=0;y<givenRoles.length;y++) {
	            if(roles[i].equalsIgnoreCase(givenRoles[y])) return true;
	        }
	    }
	    return false;
	}
}