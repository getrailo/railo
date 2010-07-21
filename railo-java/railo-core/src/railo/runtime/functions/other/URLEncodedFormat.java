/**
 * Implements the Cold Fusion Function urlencodedformat
 */
package railo.runtime.functions.other;

import java.io.UnsupportedEncodingException;

import railo.commons.lang.URLEncoder;
import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.ext.function.Function;
import railo.runtime.op.Caster;

public final class URLEncodedFormat implements Function {
	
	
	
	
	
	
	public static String call(PageContext pc , String str) throws PageException {
		return call(pc,str, "UTF-8",true);
	}
	

	public static String call(PageContext pc , String str, String encoding) throws PageException {
		return call(pc,str, encoding,true);
	}
	
	public static String call(PageContext pc , String str, String encoding,boolean force) throws PageException {
		if(!force && !railo.commons.net.URLEncoder.needEncoding(str))
			return str;
		
		try {
			String enc=java.net.URLEncoder.encode(str, encoding);
			return enc;
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
	
	
	/*public static void main(String[] args) throws PageException {
		print.out("%20%09%2B%25%26%24%2C%5C%7C%2F%3A%3B%3D%3F%40%3C%3E%23%7B%7D%28%29%5B%5D%5E%60%7E%2D%5F%2E%2A%27%E2%88%9A%E2%88%82%E2%88%9A%C2%A7%E2%88%9A%C2%BA%22");
		print.out(call(null," 	+%&$,\\|/:;=?@<>#{}()[]^`~-_.*'öäü\""));
		print.out(call(null,"\\"));
		System.out.print("char("+((int)'ö')+")&");
		System.out.print("char("+((int)'ä')+")&");
		System.out.print("char("+((int)'ü')+")&");
		System.out.print("char("+((int)'é')+")&");
		System.out.print("char("+((int)'à')+")&");
		System.out.print("char("+((int)'è')+")&");
	}*/
	
}