package railo.runtime.functions.s3;

import java.io.IOException;

import railo.commons.io.res.type.s3.S3Resource;
import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.op.Caster;
import railo.runtime.type.Struct;

public class StoreSetMetaData extends S3Function {
	
	public static String call(PageContext pc , String url, Struct metadata) throws PageException {
		try {
			return _call(pc, url,metadata);
		} catch (IOException e) {
			throw Caster.toPageException(e);
		}
	}

	public static String _call(PageContext pc , String url, Struct metadata) throws PageException, IOException {
		S3Resource res=toS3Resource(pc,url,"StoreGetMetaData");
		
		return null;
	}
	

	
	
}
