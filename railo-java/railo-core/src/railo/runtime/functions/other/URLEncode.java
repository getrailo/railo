package railo.runtime.functions.other;

import railo.runtime.PageContext;
import railo.runtime.exp.PageException;

public class URLEncode {

	
	public static String call(PageContext pc , String str) throws PageException {
		return URLEncodedFormat.call(pc,str, "UTF-8",true);
	}
	

	public static String call(PageContext pc , String str, String encoding) throws PageException {
		return URLEncodedFormat.call(pc,str, encoding,true);
	}
	
	public static String call(PageContext pc , String str, String encoding,boolean force) throws PageException {
		return URLEncodedFormat.call(pc,str, encoding,force);
	}

}
