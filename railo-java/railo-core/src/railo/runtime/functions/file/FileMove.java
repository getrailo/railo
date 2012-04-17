package railo.runtime.functions.file;

import java.io.IOException;

import railo.commons.io.res.Resource;
import railo.runtime.PageContext;
import railo.runtime.exp.FunctionException;
import railo.runtime.exp.PageException;
import railo.runtime.op.Caster;

public class FileMove {

	public static String call(PageContext pc, Object oSrc, Object oDst) throws PageException {
		Resource src = Caster.toResource(oSrc,false);
		Resource trg = Caster.toResource(oDst,false);

        pc.getConfig().getSecurityManager().checkFileLocation(src);
        pc.getConfig().getSecurityManager().checkFileLocation(trg);
        
		if(!src.exists()) 
			throw new FunctionException(pc,"FileMove",1,"source",
					"source file ["+src+"] does not exist");
		try {
			src.moveTo(trg);
		} catch (IOException e) {
			throw Caster.toPageException(e);
		}
		return null;
	}
}
