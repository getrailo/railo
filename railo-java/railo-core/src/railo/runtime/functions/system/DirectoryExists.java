/**
 * Implements the CFML Function directoryexists
 */
package railo.runtime.functions.system;

import railo.commons.io.res.Resource;
import railo.commons.io.res.util.ResourceUtil;
import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.ext.function.Function;
import railo.runtime.op.Caster;

public final class DirectoryExists implements Function {
	public static boolean call(PageContext pc , String path) throws PageException {
		return call(pc, path,pc.getConfig().allowRelPath());
	}
	public static boolean call(PageContext pc , String path,Object oAllowRelPath) throws PageException {
		Resource file;
		if(oAllowRelPath==null) return call(pc, path);
		boolean allowRelPath = Caster.toBooleanValue(oAllowRelPath);
		if(allowRelPath) {
			file=ResourceUtil.toResourceNotExisting(pc, path,allowRelPath,false);
			// TODO das else braucht es eigentlich nicht mehr
		}
		else {
			// ARP
			file=pc.getConfig().getResource(path);
			if(file!=null && !file.isAbsolute()) return false;
		}
		 
	    pc.getConfig().getSecurityManager().checkFileLocation(file);
	    return file.isDirectory() && file.exists();
	}
}