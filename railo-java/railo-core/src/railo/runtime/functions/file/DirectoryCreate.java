package railo.runtime.functions.file;

import railo.commons.io.res.Resource;
import railo.commons.io.res.type.s3.S3Constants;
import railo.commons.io.res.util.ResourceUtil;
import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.tag.Directory;

public class DirectoryCreate {
	public static String call(PageContext pc , String path) throws PageException {
		return call(pc, path, true);
	}
	public static String call(PageContext pc , String path, boolean doParent) throws PageException {
		/*Resource file=ResourceUtil.toResourceNotExisting(pc, path,pc.getConfig().allowRealPath());
		pc.getConfig().getSecurityManager().checkFileLocation(file);
		
		try {
			file.createDirectory(doParent);
		} catch (IOException e) {
			throw Caster.toPageException(e);
		}*/
		
		Resource dir=ResourceUtil.toResourceNotExisting(pc, path,pc.getConfig().allowRealPath());
		Directory.actionCreate(pc, dir, null, doParent, -1, S3Constants.ACL_PUBLIC_READ, S3Constants.STORAGE_UNKNOW);
		
	    return null;
	}
}
