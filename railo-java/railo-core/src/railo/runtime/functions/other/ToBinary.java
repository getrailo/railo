/**
 * Implements the CFML Function tobinary
 */
package railo.runtime.functions.other;

import java.nio.charset.Charset;

import railo.commons.io.CharsetUtil;
import railo.commons.lang.StringUtil;
import railo.runtime.PageContext;
import railo.runtime.PageContextImpl;
import railo.runtime.exp.PageException;
import railo.runtime.ext.function.Function;
import railo.runtime.op.Caster;

public final class ToBinary implements Function {

	private static final long serialVersionUID = 4541724601337401920L;

	public static byte[] call(PageContext pc , Object data) throws PageException {
		return call(pc, data, null);
	}
	public static byte[] call(PageContext pc , Object data, String charset) throws PageException {
		if(!StringUtil.isEmpty(charset)) {
			charset=charset.trim().toLowerCase();
			Charset cs;
			if("web".equalsIgnoreCase(charset))cs=((PageContextImpl)pc).getWebCharset();
			if("resource".equalsIgnoreCase(charset))cs=((PageContextImpl)pc).getResourceCharset();
			else cs=CharsetUtil.toCharset(charset);
				
			String str=Caster.toString(data);
			return str.getBytes(cs);
		}
		return Caster.toBinary(data);
	}
}