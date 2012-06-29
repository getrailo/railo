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