package railo.runtime.functions.file;

import java.io.IOException;

import railo.commons.io.ModeUtil;
import railo.commons.io.res.Resource;
import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.op.Caster;

public class FileSetAccessMode {

	public static String call(PageContext pc, Object oSrc, String strMode) throws PageException {
		Resource src = Caster.toResource(pc,oSrc,false);
		pc.getConfig().getSecurityManager().checkFileLocation(src);
		try {
			src.setMode(ModeUtil.toOctalMode(strMode));
		} 
		catch (IOException e) {
			throw Caster.toPageException(e);
		}
		return null;
	}
}
