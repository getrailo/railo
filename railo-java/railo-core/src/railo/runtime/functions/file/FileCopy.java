package railo.runtime.functions.file;

import railo.commons.io.res.Resource;
import railo.runtime.PageContext;
import railo.runtime.exp.FunctionException;
import railo.runtime.exp.PageException;
import railo.runtime.op.Caster;
import railo.runtime.tag.FileTag;

public class FileCopy {

	public static String call(PageContext pc, Object oSrc, Object oDst) throws PageException {
		Resource src = Caster.toResource(oSrc,false);
		if(!src.exists()) 
			throw new FunctionException(pc,"FileCopy",1,"source",
					"source file ["+src+"] does not exists");
		
		FileTag.actionCopy(pc, pc.getConfig().getSecurityManager(), 
				src, Caster.toString(oDst), 
				FileTag.NAMECONFLICT_UNDEFINED, null, null, -1, null);
		
		return null;
	}
}
