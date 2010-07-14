/**
 * Implements the Cold Fusion Function directoryexists
 */
package railo.runtime.functions.system;

import railo.commons.io.res.Resource;
import railo.commons.io.res.util.ResourceUtil;
import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.ext.function.Function;

public final class DirectoryExists implements Function {
	public static boolean call(PageContext pc , String string) throws PageException {
		return call(pc, string,pc.getConfig().allowRealPath());
	}
	public static boolean call(PageContext pc , String path,boolean allowRealPath) throws PageException {
		Resource file;
		
		if(allowRealPath) {
			file=ResourceUtil.toResourceNotExisting(pc, path,allowRealPath);
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