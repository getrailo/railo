package railo.runtime.functions.s3;

import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.op.Caster;

public class StoreGetMetaData extends S3Function {
	
	public static String call(PageContext pc , String url) throws PageException {
		try {
			return _call(pc, url);
		} catch (Exception e) {
			throw Caster.toPageException(e);
		}
	}

	public static String _call(PageContext pc , String url) {
		//S3Resource res=toS3Resource(pc,url,"StoreGetMetaData");
		
		return null;
	}
	

	
	
}
