/**
 * Implements the Cold Fusion Function gethttprequestdata
 */
package railo.runtime.functions.other;

import java.util.Enumeration;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;

import railo.commons.io.IOUtil;
import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.ext.function.Function;
import railo.runtime.net.http.ReqRspUtil;
import railo.runtime.tag.Http;
import railo.runtime.type.KeyImpl;
import railo.runtime.type.Struct;
import railo.runtime.type.StructImpl;

public final class GetHTTPRequestData implements Function {
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
		sct.set("headers", headers);
		sct.set("protocol",req.getProtocol());
		sct.set("method",req.getMethod());
		sct.set("content",getContent(req));
		return sct;
	}

    private static Object getContent(HttpServletRequest req) {
    	String contentType = req.getContentType();
        String charEncoding = req.getCharacterEncoding();
        Object obj = "";
        
        boolean isBinary =!(
        		contentType == null || Http.isText(contentType) ||
        		 
        		contentType.toLowerCase().startsWith("application/x-www-form-urlencoded"));
        //print.err("err:"+contentType+":"+charEncoding);
        
        if(req.getContentLength() > -1) {
        	ServletInputStream is=null;
            try {
            	
            	
                byte[] data = IOUtil.toBytes(is=req.getInputStream());//new byte[req.getContentLength()];
                
                if(isBinary) {
                	obj = data;
                }
                else if(charEncoding != null && charEncoding.length() > 0)
                    obj = new String(data, charEncoding);
                else
                    obj = new String(data);
            }
            catch(Exception e) {
            	
                obj="";
            }
            finally {
            	IOUtil.closeEL(is);
            }
        }
        else {
        	obj="";
        }
        return obj;
    }

}