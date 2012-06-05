package railo.runtime.functions.rest;

import railo.runtime.PageContext;
import railo.runtime.config.ConfigImpl;
import railo.runtime.exp.PageException;
import railo.runtime.exp.SecurityException;

public class RestDeleteApplication {
	public static String call(PageContext pc , String dirPath) throws PageException {
		if(!((ConfigImpl)pc.getConfig()).getRestAllowChanges())
			throw new SecurityException("You cannot delete REST mappings, modifing REST mappings is disabled in the Railo Administrator (/railo-context/admin/web.cfm?action=resources.rest).");
			
		return null;
	}
}
