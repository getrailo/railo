/**
 * Implements the CFML Function isuserinrole
 */
package railo.runtime.functions.system;

import railo.runtime.exp.PageException;
import railo.runtime.ext.function.Function;
import railo.runtime.security.Credential;
import railo.runtime.type.util.ListUtil;

public final class GetUserRoles implements Function {
	public static String call(railo.runtime.PageContext pc) throws PageException {
		Credential ru = pc.getRemoteUser();
	    if(ru==null) return "";
	    return ListUtil.arrayToList(ru.getRoles(), ",");
	}
}