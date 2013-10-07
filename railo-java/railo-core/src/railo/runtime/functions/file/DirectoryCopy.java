package railo.runtime.functions.file;

import railo.commons.io.res.Resource;
import railo.commons.io.res.type.s3.S3Constants;
import railo.commons.io.res.util.ResourceAndResourceNameFilter;
import railo.commons.io.res.util.ResourceUtil;
import railo.commons.io.res.util.UDFFilter;
import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.ext.function.Function;
import railo.runtime.tag.Directory;

public class DirectoryCopy implements Function {

	private static final long serialVersionUID = -8591512197642527401L;
	

	public static String call(PageContext pc , String source, String destination) throws PageException {
		return call(pc, source, destination, false, null, true);
	}

	public static String call(PageContext pc , String source, String destination,boolean recurse) throws PageException {
		return call(pc, source, destination, recurse, null, true);
	}
	
	public static String call(PageContext pc , String source, String destination,boolean recurse, Object filter) throws PageException {
		return call(pc, source, destination, recurse, filter, true);
	}
	
	public static String call(PageContext pc , String source, String destination,boolean recurse, Object filter,boolean createPath) throws PageException {
		Resource src = ResourceUtil.toResourceNotExisting(pc ,source);
		ResourceAndResourceNameFilter fi = filter==null?null:UDFFilter.createResourceAndResourceNameFilter(filter);
		Directory.actionCopy(pc, src, destination, null,createPath, null, S3Constants.STORAGE_UNKNOW, fi, recurse, Directory.NAMECONFLICT_DEFAULT);
		return null;
	}
	
}
