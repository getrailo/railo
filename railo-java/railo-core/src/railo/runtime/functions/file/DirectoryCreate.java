package railo.runtime.functions.file;

import railo.commons.io.res.Resource;
import railo.commons.io.res.type.s3.S3Constants;
import railo.commons.io.res.util.ResourceUtil;
import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.tag.Directory;
import railo.runtime.tag.util.FileUtil;


public class DirectoryCreate {

	public static String call(PageContext pc, String path) throws PageException {
		return call(pc, path, true);
	}

	public static String call(PageContext pc , String path, boolean createPath) throws PageException {
		return call(pc, path, createPath, false);
	}

	public static String call(PageContext pc , String path, boolean createPath, boolean ignoreExists) throws PageException {
		Resource dir=ResourceUtil.toResourceNotExisting(pc, path,pc.getConfig().allowRealPath());
		Directory.actionCreate( pc, dir, null, createPath, -1, null, S3Constants.STORAGE_UNKNOW, ignoreExists ? FileUtil.NAMECONFLICT_SKIP : FileUtil.NAMECONFLICT_ERROR );
		return null;
	}
}
