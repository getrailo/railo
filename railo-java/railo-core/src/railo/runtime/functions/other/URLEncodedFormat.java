/**
 * Implements the CFML Function urlencodedformat
 */
package railo.runtime.functions.other;

import java.io.UnsupportedEncodingException;

import railo.commons.lang.StringUtil;
import railo.commons.lang.URLEncoder;
import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.ext.function.Function;
import railo.runtime.net.http.ReqRspUtil;
import railo.runtime.op.Caster;

public final class URLEncodedFormat implements Function {
	
	private static final long serialVersionUID = 5640029138134769481L;

	public static String call(PageContext pc , String str) throws PageException {
		return call(pc,str, "UTF-8",true);
	}
	

	public static String call(PageContext pc , String str, String encoding) throws PageException {
		return call(pc,str, encoding,true);
	}

	public static String call(PageContext pc , String str, String encoding,boolean force) throws PageException {
		return invoke(str, encoding, force);
	}
	
	public static String invoke(String str, String encoding,boolean force) throws PageException {
		if(!force && !ReqRspUtil.needEncoding(str,false))
			return str;
		
		try {
			String enc=railo.commons.net.URLEncoder.encode(str, encoding);
			return StringUtil.replace(StringUtil.replace(StringUtil.replace(StringUtil.replace(StringUtil.replace(enc, "+", "%20", false), "*", "%2A", false), "-", "%2D", false), ".", "%2E", false), "_", "%5F", false);// TODO do better
			//return enc;
		} 
		catch (Throwable t) {
			try {
				return URLEncoder.encode(str, encoding);
			} 
			catch (UnsupportedEncodingException e) {
				throw Caster.toPageException(e);
			}
		}
	}
	
}