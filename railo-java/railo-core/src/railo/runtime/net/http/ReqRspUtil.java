package railo.runtime.net.http;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletInputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.xml.sax.InputSource;

import railo.commons.io.IOUtil;
import railo.commons.lang.Pair;
import railo.commons.lang.StringUtil;
import railo.commons.lang.mimetype.MimeType;
import railo.commons.net.HTTPUtil;
import railo.commons.net.URLDecoder;
import railo.commons.net.URLEncoder;
import railo.runtime.PageContext;
import railo.runtime.config.Config;
import railo.runtime.config.ConfigWeb;
import railo.runtime.config.ConfigWebImpl;
import railo.runtime.converter.WDDXConverter;
import railo.runtime.exp.PageException;
import railo.runtime.functions.decision.IsLocalHost;
import railo.runtime.interpreter.CFMLExpressionInterpreter;
import railo.runtime.interpreter.JSONExpressionInterpreter;
import railo.runtime.op.Caster;
import railo.runtime.text.xml.XMLCaster;
import railo.runtime.text.xml.XMLUtil;
import railo.runtime.type.UDF;

public final class ReqRspUtil {

	
	
	public static String get(Pair<String,Object>[] items, String name) {
		for(int i=0;i<items.length;i++) {
			if(items[i].getName().equalsIgnoreCase(name)) 
				return Caster.toString(items[i].getValue(),null);
		}
		return null;
	}
	
	public static Pair<String,Object>[] add(Pair<String,Object>[] items, String name, Object value) {
		Pair<String,Object>[] tmp = new Pair[items.length+1];
		for(int i=0;i<items.length;i++) {
			tmp[i]=items[i];
		}
		tmp[items.length]=new Pair<String,Object>(name,value);
		return tmp;
	}
	
	public static Pair<String,Object>[] set(Pair<String,Object>[] items, String name, Object value) {
		for(int i=0;i<items.length;i++) {
			if(items[i].getName().equalsIgnoreCase(name)) {
				items[i]=new Pair<String,Object>(name,value);
				return items;
			}
		}
		 return add(items, name, value);
	}

	/**
	 * return path to itself
	 * @param req
	 */
	public static String self(HttpServletRequest req) {
		StringBuffer sb=new StringBuffer(req.getServletPath());
		String qs=req.getQueryString();
		if(!StringUtil.isEmpty(qs))sb.append('?').append(qs);
		return sb.toString();
	}

	public static void setContentLength(HttpServletResponse rsp, int length) {
		rsp.setContentLength(length);
	}
	public static void setContentLength(HttpServletResponse rsp, long length) {
		if(length <= Integer.MAX_VALUE){
			setContentLength(rsp,(int)length);
		}
		else{
			rsp.addHeader("Content-Length", Caster.toString(length));
		}
	}

	public static Cookie[] getCookies(Config config,HttpServletRequest req) {
		Cookie[] cookies = req.getCookies();
		String charset = config.getWebCharset();
		
		if(cookies!=null) {
			Cookie cookie;
			String tmp;
			for(int i=0;i<cookies.length;i++){
				cookie=cookies[i];	
				// value (is decoded by the servlet engine with iso-8859-1)
				if(!StringUtil.isAscci(cookie.getValue())) {
					tmp=encode(cookie.getValue(), "iso-8859-1");
					cookie.setValue(decode(tmp, charset,false));
				}
				
			}
		}
		else {
			String str = req.getHeader("Cookie");
			if(str!=null) {
				try{
					String[] arr = railo.runtime.type.List.listToStringArray(str, ';'),tmp;
					java.util.List<Cookie> list=new ArrayList<Cookie>();
					for(int i=0;i<arr.length;i++){
						tmp=railo.runtime.type.List.listToStringArray(arr[i], '=');
						if(tmp.length>0) {
							list.add(new Cookie(dec(tmp[0],charset,false), tmp.length>1?dec(tmp[1],charset,false):""));
						}
					}
					cookies=list.toArray(new Cookie[list.size()]);
					
				}
				catch(Throwable t){}
			}
		}
		return cookies;
	}


	public static void setCharacterEncoding(HttpServletResponse rsp,String charset) {
		try {
			Method setCharacterEncoding = rsp.getClass().getMethod("setCharacterEncoding", new Class[0]);
			setCharacterEncoding.invoke(rsp, new Object[0]);
		} 
		catch (Throwable t) {}
	}

	public static String getQueryString(HttpServletRequest req) {
		//String qs = req.getAttribute("javax.servlet.include.query_string");
		return req.getQueryString();
	}

	public static String getHeader(HttpServletRequest request, String name,String defaultValue) {
		try {
			return request.getHeader(name);
		}
		catch(Throwable t){
			return defaultValue;
		}
	}

	public static String getHeaderIgnoreCase(PageContext pc, String name,String defaultValue) {
		String charset = pc.getConfig().getWebCharset();
		HttpServletRequest req = pc.getHttpServletRequest();
		Enumeration e = req.getHeaderNames();
		String keyDecoded,key;
		while(e.hasMoreElements()) {
			key=e.nextElement().toString();
			keyDecoded=ReqRspUtil.decode(key, charset,false);
			if(name.equalsIgnoreCase(key) || name.equalsIgnoreCase(keyDecoded))
				return ReqRspUtil.decode(req.getHeader(key),charset,false);
		}
		return defaultValue;
	}

	public static List<String> getHeadersIgnoreCase(PageContext pc, String name) {
		String charset = pc.getConfig().getWebCharset();
		HttpServletRequest req = pc.getHttpServletRequest();
		Enumeration e = req.getHeaderNames();
		List<String> rtn=new ArrayList<String>();
		String keyDecoded,key;
		while(e.hasMoreElements()) {
			key=e.nextElement().toString();
			keyDecoded=ReqRspUtil.decode(key, charset,false);
			if(name.equalsIgnoreCase(key) || name.equalsIgnoreCase(keyDecoded))
				rtn.add(ReqRspUtil.decode(req.getHeader(key),charset,false));
		}
		return rtn;
	}

	public static String getScriptName(HttpServletRequest req) {
		return StringUtil.emptyIfNull(req.getContextPath())+StringUtil.emptyIfNull(req.getServletPath());
	}

	private static boolean isHex(char c) {
		return (c>='0' && c<='9') || (c>='a' && c<='f') || (c>='A' && c<='F');
	}
	

	private static String dec(String str, String charset, boolean force) {
		str=str.trim();
		if(StringUtil.startsWith(str, '"') && StringUtil.endsWith(str, '"'))
			str=str.substring(1,str.length()-1);
			
		return decode(str,charset,force);//java.net.URLDecoder.decode(str.trim(), charset);
	}


    public static String decode(String str,String charset, boolean force) {
    	try {
			return URLDecoder.decode(str, charset,force);
		} 
		catch (UnsupportedEncodingException e) {
			return str;
		}
	}
    public static String encode(String str,String charset) {
		try {
			return URLEncoder.encode(str, charset);
		} 
		catch (UnsupportedEncodingException e) {
			return str;
		}
	}
    
    
    
    public static boolean needEncoding(String str, boolean allowPlus){
    	if(StringUtil.isEmpty(str,false)) return false;
    	
    	int len=str.length();
    	char c;
    	for(int i=0;i<len;i++){
    		c=str.charAt(i);
    		if(c >='0' && c <= '9') continue;
    		if(c >='a' && c <= 'z') continue;
    		if(c >='A' && c <= 'Z') continue;
    		
    		// _-.*
    		if(c =='-') continue;
    		if(c =='_') continue;
    		if(c =='.') continue;
    		if(c =='*') continue;
    		if(c =='/') continue;
    		if(allowPlus && c =='+') continue;
    		
    		if(c =='%') {
    			if(i+2>=len) return true;
    			try{
    				Integer.parseInt(str.substring(i+1,i+3),16);
    			}
    			catch(NumberFormatException nfe){
    				return true;
    			}
    			i+=3;
    			continue;
    		}
    		return true;
    	}
    	return false;
    }
    
    public static boolean needDecoding(String str){
    	if(StringUtil.isEmpty(str,false)) return false;
    	
    	boolean need=false;
    	int len=str.length();
    	char c;
    	for(int i=0;i<len;i++){
    		c=str.charAt(i);
    		if(c >='0' && c <= '9') continue;
    		if(c >='a' && c <= 'z') continue;
    		if(c >='A' && c <= 'Z') continue;
    		
    		// _-.*
    		if(c =='-') continue;
    		if(c =='_') continue;
    		if(c =='.') continue;
    		if(c =='*') continue;
    		if(c =='+') {
    			need=true;
    			continue;
    		}
    		
    		if(c =='%') {
    			if(i+2>=len) return false;
    			try{
    				Integer.parseInt(str.substring(i+1,i+3),16);
    			}
    			catch(NumberFormatException nfe){
    				return false;
    			}
    			i+=3;
    			need=true;
    			continue;
    		}
    		return false;
    	}
    	return need;
    }

	public static boolean isThis(HttpServletRequest req, String url) { 
		try {
			return isThis(req, HTTPUtil.toURL(url));
		} 
		catch (Throwable t) {
			return false;
		}
	}

	public static boolean isThis(HttpServletRequest req, URL url) { 
		try {
			// Port
			int reqPort=req.getServerPort();
			int urlPort=url.getPort();
			if(urlPort<=0) urlPort=HTTPUtil.isSecure(url)?443:80;
			if(reqPort<=0) reqPort=req.isSecure()?443:80;
			if(reqPort!=urlPort) return false;
			
			// host
			String reqHost = req.getServerName();
			String urlHost = url.getHost();
			if(reqHost.equalsIgnoreCase(urlHost)) return true;
			if(IsLocalHost.invoke(reqHost) && IsLocalHost.invoke(reqHost)) return true;
			
			InetAddress urlAddr = InetAddress.getByName(urlHost);
			
			InetAddress reqAddr = InetAddress.getByName(reqHost);
			if(reqAddr.getHostName().equalsIgnoreCase(urlAddr.getHostName())) return true;
			if(reqAddr.getHostAddress().equalsIgnoreCase(urlAddr.getHostAddress())) return true;
			
			reqAddr = InetAddress.getByName(req.getRemoteAddr());
			if(reqAddr.getHostName().equalsIgnoreCase(urlAddr.getHostName())) return true;
			if(reqAddr.getHostAddress().equalsIgnoreCase(urlAddr.getHostAddress())) return true;
		}
		catch(Throwable t){}
		return false;
	}
	

    public static LinkedList<MimeType> getAccept(PageContext pc) {
    	LinkedList<MimeType> accept=new LinkedList<MimeType>();
    	java.util.Iterator<String> it = ReqRspUtil.getHeadersIgnoreCase(pc, "accept").iterator();
    	String value;
		while(it.hasNext()){
			value=it.next();
			MimeType[] mtes = MimeType.getInstances(value, ',');
			if(mtes!=null)for(int i=0;i<mtes.length;i++){
				accept.add(mtes[i]);
			}
		}
		return accept;
	}
    
    public static MimeType getContentType(PageContext pc) {
    	java.util.Iterator<String> it = ReqRspUtil.getHeadersIgnoreCase(pc, "content-type").iterator();
    	String value;
    	MimeType rtn=null;
		while(it.hasNext()){
			value=it.next();
			MimeType[] mtes = MimeType.getInstances(value, ',');
			if(mtes!=null)for(int i=0;i<mtes.length;i++){
				rtn= mtes[i];
			}
		}
		if(rtn==null) return MimeType.ALL;
		return rtn;
	}
    
    public static String getContentTypeAsString(PageContext pc,String defaultValue) {
    	MimeType mt = getContentType(pc);
    	if(mt==MimeType.ALL) return defaultValue;
    	return mt.toString();
    }

	/**
	 * returns the body of the request
	 * @param pc
	 * @param deserialized if true railo tries to deserialize the body based on the content-type, for example when the content type is "application/json"
	 * @param defaultValue value returned if there is no body
	 * @return
	 */
	public static Object getRequestBody(PageContext pc,boolean deserialized, Object defaultValue) {
		HttpServletRequest req = pc.getHttpServletRequest();
    	
		MimeType contentType = getContentType(pc);
		String strContentType=contentType==MimeType.ALL?null:contentType.toString();
        String charEncoding = req.getCharacterEncoding();
        Object obj = "";
        
        boolean isBinary =!(
        		strContentType == null || 
        		HTTPUtil.isTextMimeType(contentType) ||
        		strContentType.toLowerCase().startsWith("application/x-www-form-urlencoded"));
        
        if(req.getContentLength() > -1) {
        	ServletInputStream is=null;
            try {
                byte[] data = IOUtil.toBytes(is=req.getInputStream());//new byte[req.getContentLength()];
                
                if(isBinary) return data;
                
                String str;
                if(charEncoding != null && charEncoding.length() > 0)
                    obj = str = new String(data, charEncoding);
                else
                    obj = str = new String(data);
                
                if(deserialized){
                	int format = MimeType.toFormat(contentType, -1);
                	switch(format) {
                	case UDF.RETURN_FORMAT_JSON:
                		try{
                			obj=new JSONExpressionInterpreter().interpret(pc, str);
                		}
                		catch(PageException pe){}
                	break;
                	case UDF.RETURN_FORMAT_SERIALIZE:
                		try{
                			obj=new CFMLExpressionInterpreter().interpret(pc, str);
                		}
                		catch(PageException pe){}
                	break;
                	case UDF.RETURN_FORMAT_WDDX:
                		try{
                			WDDXConverter converter =new WDDXConverter(pc.getTimeZone(),false,true);
                			converter.setTimeZone(pc.getTimeZone());
                			obj = converter.deserialize(str,false);
                		}
                		catch(Exception pe){}
                	break;
                	case UDF.RETURN_FORMAT_XML:
                		try{
                			InputSource xml = XMLUtil.toInputSource(pc,str.trim());
                			InputSource validator =null;
                			obj = XMLCaster.toXMLStruct(XMLUtil.parse(xml,validator,false),true);
                		}
                		catch(Exception pe){}
                	break;
                	}
                }
               
                
                
            }
            catch(Exception e) {
            	return defaultValue;
            }
            finally {
            	IOUtil.closeEL(is);
            }
        }
        else {
        	return defaultValue;
        }
        return obj;
    }

	public static String getRootPath(ServletContext sc) {
		if(sc==null) throw new RuntimeException("cannot terminate webcontext root, because given ServletContet is null");
		String root = sc.getRealPath("/");
		if(root==null) throw new RuntimeException("cannot terminate webcontext root, the ServletContext from class ["+sc.getClass().getName()+"] is returning null for the method call sc.getRealPath(\"/\")");
		return root;
	}
}
