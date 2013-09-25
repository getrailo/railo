package railo.runtime.functions.file;

import railo.commons.io.res.Resource;
import railo.commons.io.res.type.s3.S3Constants;
import railo.commons.io.res.util.ResourceUtil;
import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.tag.Directory;

public class DirectoryRename {

	public static String call(PageContext pc , String oldPath,String newPath) throws PageException {
		return call(pc, oldPath, newPath, true);
	}
	public static String call(PageContext pc , String oldPath,String newPath, boolean createPath) throws PageException {
		Resource dir=ResourceUtil.toResourceNotExisting(pc, oldPath,pc.getConfig().allowRealPath());
		Directory.actionRename(pc, dir, newPath, null,createPath, S3Constants.ACL_PUBLIC_READ, S3Constants.STORAGE_UNKNOW);
		return null;
	}
}
