package railo.runtime.net.http;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import railo.commons.lang.Pair;
import railo.commons.lang.StringUtil;
import railo.commons.net.URLDecoder;
import railo.commons.net.URLEncoder;
import railo.runtime.config.Config;
import railo.runtime.op.Caster;
import railo.runtime.type.List;

public final class ReqRspUtil {

	public static String get(Pair[] items, String name) {
		for(int i=0;i<items.length;i++) {
			if(items[i].getName().equalsIgnoreCase(name)) 
				return Caster.toString(items[i].getValue(),null);
		}
		return null;
	}
	
	public static Pair[] add(Pair[] items, String name, Object value) {
		Pair[] tmp = new Pair[items.length+1];
		for(int i=0;i<items.length;i++) {
			tmp[i]=items[i];
		}
		tmp[items.length]=new Pair(name,value);
		return tmp;
	}
	
	public static Pair[] set(Pair[] items, String name, Object value) {
		for(int i=0;i<items.length;i++) {
			if(items[i].getName().equalsIgnoreCase(name)) {
				items[i]=new Pair(name,value);
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
					String[] arr = List.listToStringArray(str, ';'),tmp;
					java.util.List<Cookie> list=new ArrayList<Cookie>();
					for(int i=0;i<arr.length;i++){
						tmp=List.listToStringArray(arr[i], '=');
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
			
			
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
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

	public static String getScriptName(HttpServletRequest req) {
		return StringUtil.emptyIfNull(req.getContextPath())+StringUtil.emptyIfNull(req.getServletPath());
	}
	
	/*public static boolean isURLEncoded(String str) {
		if(StringUtil.isEmpty(str,true)) return false;
		if(!ReqRspUtil.isSubAscci(str,false)) return false;
		int index,last=0;
		boolean rtn=false;
		while((index=str.indexOf('%',last))!=-1){
			if(index+2>=str.length()) return false;
			if(!isHex(str.charAt(index+1)) || !isHex(str.charAt(index+2))) return false;
			last=index+1;
			rtn=true;
		}
		return rtn;
	}*/

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
}
