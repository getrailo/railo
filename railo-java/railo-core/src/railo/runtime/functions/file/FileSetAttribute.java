package railo.runtime.functions.file;

import java.io.IOException;

import railo.commons.io.res.Resource;
import railo.runtime.PageContext;
import railo.runtime.exp.FunctionException;
import railo.runtime.exp.PageException;
import railo.runtime.op.Caster;

public class FileSetAttribute {

	public static String call(PageContext pc, Object oSrc, String attr) throws PageException {
		Resource src = Caster.toResource(pc,oSrc,false);
		pc.getConfig().getSecurityManager().checkFileLocation(src);
		attr=attr.trim().toLowerCase();
		try {
		if("archive".equals(attr)){
			src.setAttribute(Resource.ATTRIBUTE_ARCHIVE, true);
		}
		else if("hidden".equals(attr)){
			src.setAttribute(Resource.ATTRIBUTE_HIDDEN, true);
		}
		else if("system".equals(attr)){
			src.setAttribute(Resource.ATTRIBUTE_SYSTEM, true);
		}
		else if("normal".equals(attr)){
			src.setAttribute(Resource.ATTRIBUTE_ARCHIVE, false);
			src.setAttribute(Resource.ATTRIBUTE_HIDDEN, false);
			src.setAttribute(Resource.ATTRIBUTE_SYSTEM, false);
		}
		else 
			throw new FunctionException(pc,"FileSetAttribute",2,"attribute","invalid value ["+attr+"], valid values are [normal,archive,hidden,system]");
		}
		catch(IOException ioe) {
			throw Caster.toPageException(ioe);
		}
		return null;
	}
}
