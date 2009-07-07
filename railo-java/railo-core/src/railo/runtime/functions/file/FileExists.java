package railo.runtime.functions.file;

import railo.commons.io.res.Resource;
import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.op.Caster;

public class FileExists {

	public static boolean call(PageContext pc , Object obj) throws PageException {
		return call(pc, obj, pc.getConfig().allowRealPath());
	}
	
	public static boolean call(PageContext pc , Object obj, boolean allowRealPath) throws PageException {
		Resource res;
		/*if(!allowRealPath && obj instanceof String){
			res=pc.getConfig().getResource((String)obj);
			if(res!=null && !res.isAbsolute()) return false;
		}
		else*/
			res=Caster.toResource(pc,obj, false,allowRealPath);
		
		
	    if(res==null) return false;
        pc.getConfig().getSecurityManager().checkFileLocation(res);
        
		return res.isFile() && res.exists();
	}
}
