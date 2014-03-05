/**
 * Implements the CFML Function tostring
 */
package railo.runtime.functions.string;

import java.nio.charset.Charset;

import railo.commons.io.CharsetUtil;
import railo.commons.lang.StringUtil;
import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.ext.function.Function;
import railo.runtime.net.http.ReqRspUtil;
import railo.runtime.op.Caster;

public final class ToString implements Function {
	public static String call(PageContext pc ) {
		return "";
	}
	public static String call(PageContext pc , Object object) throws PageException {
		return call(pc,object,null);
	}
	public static String call(PageContext pc , Object object, String encoding) throws PageException {
		Charset charset;
		if(StringUtil.isEmpty(encoding)) {
			charset = ReqRspUtil.getCharacterEncoding(pc,pc.getResponse());
		}
		else
			charset = CharsetUtil.toCharset(encoding);
		
		if(object instanceof byte[]){
			if(charset!=null)
				return new String((byte[])object,charset);
			return new String((byte[])object);
		}
		return Caster.toString(object);
	}
}