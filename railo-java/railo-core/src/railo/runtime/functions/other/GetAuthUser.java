/**
 * Implements the CFML Function getauthuser
 */
package railo.runtime.functions.other;

import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.ext.function.Function;
import railo.runtime.security.Credential;

public final class GetAuthUser implements Function {
	public static String call(PageContext pc ) throws PageException {
	    Credential remoteUser = pc.getRemoteUser();
	    if(remoteUser==null) {
            String user=pc. getHttpServletRequest().getRemoteUser();
            if(user!=null) {
                return user;
            }
            return "";
        }
        return remoteUser.getUsername();
        
	}
}