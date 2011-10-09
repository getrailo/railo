package railo.runtime.functions.file;

import railo.commons.io.res.Resource;
import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.op.Caster;

public class FileExists {

	public static boolean call(PageContext pc , Object obj) throws PageException {
		return call(pc, obj, pc.getConfig().allowRealPath());
	}
	
	public static boolean call(PageContext pc , Object obj, Object oAllowRealPath) throws PageException {
		if(oAllowRealPath==null) return call(pc, obj);
		
		Resource res=Caster.toResource(pc,obj, false,Caster.toBooleanValue(oAllowRealPath));
		if(res==null) return false;
        pc.getConfig().getSecurityManager().checkFileLocation(res);
        return res.isFile() && res.exists();
	}
}
