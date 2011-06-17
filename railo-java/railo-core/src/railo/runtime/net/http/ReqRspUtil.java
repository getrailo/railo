package railo.runtime.net.http;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import railo.commons.lang.Pair;
import railo.commons.lang.StringUtil;
import railo.runtime.config.Config;
import railo.runtime.op.Caster;
import railo.runtime.type.List;
import railo.runtime.type.scope.CookieImpl;

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
		Cookie[] cookies =req.getCookies();
		if(cookies==null) {
			String str = req.getHeader("Cookie");
			if(str!=null) {
				String charset = config.getWebCharset();
				try{
					String[] arr = List.listToStringArray(str, ';'),tmp;
					java.util.List<Cookie> list=new ArrayList<Cookie>();
					for(int i=0;i<arr.length;i++){
						tmp=List.listToStringArray(arr[i], '=');
						if(tmp.length>0) {
							list.add(new Cookie(dec(tmp[0],charset), tmp.length>1?dec(tmp[1],charset):""));
						}
					}
					cookies=list.toArray(new Cookie[list.size()]);
					
				}
				catch(Throwable t){}
			}
		}
		return cookies;
	}

	private static String dec(String str, String charset) {
		str=str.trim();
		if(StringUtil.startsWith(str, '"') && StringUtil.endsWith(str, '"'))
			str=str.substring(1,str.length()-1);
			
		return CookieImpl.dec(str,charset);//java.net.URLDecoder.decode(str.trim(), charset);
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
}
