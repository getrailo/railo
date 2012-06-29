/**
 * Implements the CFML Function urldecode
 */
package railo.runtime.functions.other;

import java.io.UnsupportedEncodingException;

import railo.commons.net.URLDecoder;
import railo.runtime.PageContext;
import railo.runtime.exp.ExpressionException;
import railo.runtime.ext.function.Function;

public final class URLDecode implements Function {
	public static String call(PageContext pc , String str) throws ExpressionException {
		return call(pc,str,"utf-8");
	}
	public static String call(PageContext pc , String str, String encoding) throws ExpressionException {
		try {
			return java.net.URLDecoder.decode(str,encoding);
		} catch (Throwable t) {
			try {
				return URLDecoder.decode(str,encoding,true);
			} catch (UnsupportedEncodingException uee) {
				throw new ExpressionException(uee.getMessage());
			}
		}
		/*try {
			return URLDecoder.decode(str,encoding);
		} catch (UnsupportedEncodingException e) {
			throw new ExpressionException(e.getMessage());
		}*/
	}
}