/**
 * Implements the CFML Function isuserinrole
 */
package railo.runtime.functions.decision;

import railo.commons.lang.StringUtil;
import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.ext.function.Function;
import railo.runtime.security.Credential;
import railo.runtime.type.util.ListUtil;

public final class IsUserInAnyRole implements Function {
	public static boolean call(PageContext pc) throws PageException {
		return call(pc, null);
	}
	public static boolean call(PageContext pc, String strRoles) throws PageException {
		if(StringUtil.isEmpty(strRoles)){
			Credential ru = pc.getRemoteUser();
			if(ru==null) return false;
		    return ru.getRoles().length>0;
		}
		
		String[] roles = ListUtil.trimItems(ListUtil.listToStringArray(strRoles, ','));
		for(int i=0;i<roles.length;i++) {
			if(IsUserInRole.call(pc, roles[i])) return true;
		}
		return false;
	}
}