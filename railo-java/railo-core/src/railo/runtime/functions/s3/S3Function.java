package railo.runtime.functions.s3;

import railo.commons.io.res.Resource;
import railo.commons.io.res.ResourceProvider;
import railo.commons.io.res.type.s3.S3Resource;
import railo.commons.io.res.util.ResourceUtil;
import railo.runtime.PageContext;
import railo.runtime.exp.ExpressionException;
import railo.runtime.exp.FunctionException;

public class S3Function {


	public static S3Resource toS3Resource(PageContext pc, String url, String functionName) throws ExpressionException {
		Resource res=ResourceUtil.toResourceNotExisting(pc, url);
		ResourceProvider provider = res.getResourceProvider();
		if(!provider.getScheme().equalsIgnoreCase("s3") || !res.exists()) 
			throw new FunctionException(pc,functionName,1,"url","defined url must be a valid existing S3 Resource");
		
		return (S3Resource) res;
	}
}
