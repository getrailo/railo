package railo.runtime.functions.file;

import java.io.IOException;

import railo.commons.io.IOUtil;
import railo.commons.io.res.Resource;
import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.op.Caster;

public class FileReadBinary {

	public static Object call(PageContext pc, Object oSrc) throws PageException {
		Resource src = Caster.toResource(pc,oSrc,false);
		pc.getConfig().getSecurityManager().checkFileLocation(src);
		try {
			return IOUtil.toBytes(src);
		} catch (IOException e) {
			throw Caster.toPageException(e);
		}
	}
}
