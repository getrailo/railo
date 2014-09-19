package railo.runtime.functions.file;

import java.io.File;

import railo.commons.io.res.Resource;
import railo.runtime.PageContext;
import railo.runtime.exp.FunctionException;
import railo.runtime.exp.PageException;
import railo.runtime.op.Caster;

public class GetTotalSpace {
	public static double call(PageContext pc , Object obj) throws PageException {
		Resource res=Caster.toResource(pc,obj, true, pc.getConfig().allowRealPath());
		if(!(res instanceof File)) throw new FunctionException(pc,"getTotalSpace",1,"filepath","this function is only supported for the local filesystem");  
		File file=(File) res;
		return file.getTotalSpace();
	}
}
