/**
 * Implements the CFML Function gethttprequestdata
 */
package railo.runtime.functions.other;

import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;

import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.ext.function.Function;
import railo.runtime.net.http.ReqRspUtil;
import railo.runtime.type.KeyImpl;
import railo.runtime.type.Struct;
import railo.runtime.type.StructImpl;
import railo.runtime.type.util.KeyConstants;

public final class GetHTTPRequestData implements Function {

	private static final long serialVersionUID = 1365182999286292317L;

	public static Struct call(PageContext pc ) throws PageException {
		
		Struct sct=new StructImpl();
		Struct headers=new StructImpl();
		HttpServletRequest req = pc.getHttpServletRequest();
		String charset = pc.getConfig().getWebCharset();
		// headers
		Enumeration e = req.getHeaderNames();
		while(e.hasMoreElements()) {
			String key=e.nextElement().toString();
			headers.set(KeyImpl.init(ReqRspUtil.decode(key, charset,false)),ReqRspUtil.decode(req.getHeader(key),charset,false));
		}
		sct.set(KeyConstants._headers, headers);
		sct.set(KeyConstants._protocol,req.getProtocol());
		sct.set(KeyConstants._method,req.getMethod());
		sct.set(KeyConstants._content,ReqRspUtil.getRequestBody(pc,false,""));
		return sct;
	}
}