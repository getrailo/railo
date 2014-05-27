package railo.runtime.functions.file;

import railo.commons.io.res.Resource;
import railo.commons.io.res.util.ResourceUtil;
import railo.commons.lang.StringUtil;
import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.functions.BIF;
import railo.runtime.op.Caster;

public class GetCanonicalPath extends BIF {

	private static final long serialVersionUID = -7516439220584467382L;

	public static String call(PageContext pc, String path) {
		// we only add a slash if there was already one (for FuseBox), otherwise we cannot know for sure it is a directory (when path not exists ....)
		boolean addEndSep=StringUtil.endsWith(path, '/','\\');
		Resource res = ResourceUtil.toResourceNotExisting(pc, path);
		if(!addEndSep && res.isDirectory()) addEndSep=true;
			
		path=ResourceUtil.getCanonicalPathEL(res);
		if(addEndSep && !StringUtil.endsWith(path, '/','\\')) {
			return path+ResourceUtil.getSeparator(res.getResourceProvider());
		}
		return path;
	}

	@Override
	public Object invoke(PageContext pc, Object[] args) throws PageException {
		return call(pc,Caster.toString(args[0]));
	}
}
