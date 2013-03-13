package railo.runtime.functions.file;

import java.io.IOException;

import railo.commons.io.res.Resource;
import railo.runtime.PageContext;
import railo.runtime.exp.FunctionException;
import railo.runtime.exp.PageException;
import railo.runtime.op.Caster;

public class FileDelete {

	public static String call(PageContext pc, Object oSrc) throws PageException {
		Resource src = Caster.toResource(pc,oSrc,false);

        pc.getConfig().getSecurityManager().checkFileLocation(src);
		if(!src.exists()) 
			throw new FunctionException(pc,"FileDelete",1,"source",
					"source file ["+src+"] does not exist");
		try {
			src.remove(false);
		} catch (IOException e) {
			throw Caster.toPageException(e);
		}
		return null;
	}
}
