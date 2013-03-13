package railo.runtime.functions.file;

import railo.commons.io.res.Resource;
import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.op.Caster;
import railo.runtime.type.dt.DateTime;

public class FileSetLastModified {

	public static String call(PageContext pc, Object oSrc, DateTime date) throws PageException {
		Resource src = Caster.toResource(pc,oSrc,false);
		pc.getConfig().getSecurityManager().checkFileLocation(src);
		src.setLastModified(date.getTime());
		return null;
	}
}
