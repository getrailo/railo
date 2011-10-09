package railo.runtime.functions.file;

import railo.commons.io.res.Resource;
import railo.commons.io.res.util.ResourceUtil;
import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.tag.Directory;

public class DirectoryDelete {
	public static String call(PageContext pc , String path) throws PageException {
		return call(pc, path, false);
	}
	public static String call(PageContext pc , String path,boolean recurse) throws PageException {
		Resource dir=ResourceUtil.toResourceNotExisting(pc, path,pc.getConfig().allowRealPath());
		Directory.actionDelete(pc, dir, recurse, null);
	    return null;
	}
}
