package railo.runtime.functions.file;

import railo.commons.io.res.util.ResourceUtil;
import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.functions.BIF;
import railo.runtime.op.Caster;

public class GetCanonicalPath extends BIF {

	private static final long serialVersionUID = -7516439220584467382L;

	public static String call(PageContext pc, String path) {
		return ResourceUtil.getCanonicalPathEL(ResourceUtil.toResourceNotExisting(pc, path));
	}

	@Override
	public Object invoke(PageContext pc, Object[] args) throws PageException {
		return call(pc,Caster.toString(args[0]));
	}
}
