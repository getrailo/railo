package railo.runtime.functions.s3;

import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.op.Caster;
import railo.runtime.type.Struct;

public class StoreSetMetaData extends S3Function {
	
	public static String call(PageContext pc , String url, Struct metadata) throws PageException {
		try {
			return _call(pc, url,metadata);
		} catch (Exception e) {
			throw Caster.toPageException(e);
		}
	}

	public static String _call(PageContext pc , String url, Struct metadata) {
		//S3Resource res=toS3Resource(pc,url,"StoreGetMetaData");
		
		return null;
	}
	

	
	
}
