/**
 * Implements the CFML Function isuserinrole
 */
package railo.runtime.functions.decision;

import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.ext.function.Function;
import railo.runtime.security.Credential;

public final class IsUserLoggedIn implements Function {
	public static boolean call(PageContext pc) throws PageException {
		Credential ru = pc.getRemoteUser();
	    return ru!=null;
	}
}