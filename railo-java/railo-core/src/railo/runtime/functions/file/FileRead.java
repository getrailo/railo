package railo.runtime.functions.file;

import java.io.IOException;

import railo.commons.io.IOUtil;
import railo.commons.io.res.Resource;
import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.op.Caster;

public class FileRead {

	public static Object call(PageContext pc, Object path) throws PageException {
		return _call(pc,Caster.toResource(path),pc.getConfig().getResourceCharset());
	}
	
	public static Object call(PageContext pc, Object obj, Object charsetOrSize) throws PageException {
		if(obj instanceof FileStreamWrapper) {
			return _call((FileStreamWrapper)obj,Caster.toIntValue(charsetOrSize));
		}
		return _call(pc,Caster.toResource(obj),Caster.toString(charsetOrSize));
	}

	private static Object _call(FileStreamWrapper fs, int size) throws PageException {
		try {
			return fs.read(size);
		} catch (IOException e) {
			throw Caster.toPageException(e);
		}
	}

	private static Object _call(PageContext pc,Resource res, String charset) throws PageException {
		pc.getConfig().getSecurityManager().checkFileLocation(res);
		try {
			return IOUtil.toString(res,charset);
		} catch (IOException e) {
			throw Caster.toPageException(e);
		}
	}
}
