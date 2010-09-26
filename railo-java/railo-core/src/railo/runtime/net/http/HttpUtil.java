package railo.runtime.net.http;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import railo.commons.lang.Pair;
import railo.runtime.type.Struct;
import railo.runtime.type.StructImpl;

public class HttpUtil {

	/**
	 * read all headers from request and return it
	 * @param req
	 * @return
	 */
	public static Pair[] cloneHeaders(HttpServletRequest req) {
		List headers=new ArrayList();
		Enumeration e = req.getHeaderNames(),ee;
		String name;
		while(e.hasMoreElements()){
			name=(String) e.nextElement();
			ee=req.getHeaders(name);
			while(ee.hasMoreElements()){
				headers.add(new Pair(name,ee.nextElement().toString()));
			}
		}
		return (Pair[]) headers.toArray(new Pair[headers.size()]);
	}

	public static Struct getAttributesAsStruct(HttpServletRequest req) {
		Struct attributes=new StructImpl();
		Enumeration e = req.getAttributeNames();
		String name;
		while(e.hasMoreElements()){
			name=(String) e.nextElement();// MUST (hhlhgiug) can throw ConcurrentModificationException
			if(name!=null)attributes.setEL(name, req.getAttribute(name));
		}
		return attributes;
	}

	public static Pair[] getAttributes(HttpServletRequest req) {
		List attributes=new ArrayList();
		Enumeration e = req.getAttributeNames();
		String name;
		while(e.hasMoreElements()){
			name=(String) e.nextElement();
			attributes.add(new Pair(name, req.getAttribute(name)));
		}
		return (Pair[]) attributes.toArray(new Pair[attributes.size()]);
	}

	public static Pair[] cloneParameters(HttpServletRequest req) {
		List parameters=new ArrayList();
		Enumeration e = req.getParameterNames();
		String[] values;
		String name;
		while(e.hasMoreElements()){
			name=(String) e.nextElement();
			values=req.getParameterValues(name);
			for(int i=0;i<values.length;i++){
				parameters.add(new Pair(name,values[i]));
			}
		}
		return (Pair[]) parameters.toArray(new Pair[parameters.size()]);
	}
	
	public static Cookie[] cloneCookies(HttpServletRequest req) {
		Cookie[] src=req.getCookies();
		if(src==null)return new Cookie[0];
		
		Cookie[] dest=new Cookie[src.length];
		for(int i=0;i<src.length;i++) {
			dest[i]=(Cookie) src[i].clone();
		}
		return dest;
	}

}
