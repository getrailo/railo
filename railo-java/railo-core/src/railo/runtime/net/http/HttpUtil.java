package railo.runtime.net.http;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import railo.commons.lang.Pair;
import railo.runtime.PageContext;
import railo.runtime.config.Config;
import railo.runtime.engine.ThreadLocalPageContext;
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

	public static Pair<String,Object>[] getAttributes(HttpServletRequest req) {
		List<Pair<String,Object>> attributes=new ArrayList<Pair<String,Object>>();
		Enumeration e = req.getAttributeNames();
		String name;
		while(e.hasMoreElements()){
			name=(String) e.nextElement();
			attributes.add(new Pair<String,Object>(name, req.getAttribute(name)));
		}
		return attributes.toArray(new Pair[attributes.size()]);
	}

	public static Pair<String,String>[] cloneParameters(HttpServletRequest req) {
		List<Pair<String,String>> parameters=new ArrayList<Pair<String,String>>();
		Enumeration e = req.getParameterNames();
		String[] values;
		String name;
		
		while(e.hasMoreElements()){
			name=(String) e.nextElement();
			values=req.getParameterValues(name);
			if(values==null && ReqRspUtil.needEncoding(name, true))
				values=req.getParameterValues(ReqRspUtil.encode(name, ReqRspUtil.getCharacterEncoding(null,req)));
			if(values==null) {
				PageContext pc = ThreadLocalPageContext.get();
				if(pc!=null && ReqRspUtil.identical(pc.getHttpServletRequest(),req) ) {
					values=HTTPServletRequestWrap.getParameterValues(ThreadLocalPageContext.get(), name);
				}
			}
			if(values!=null)for(int i=0;i<values.length;i++){
				parameters.add(new Pair<String,String>(name,values[i]));
			}
		}
		return parameters.toArray(new Pair[parameters.size()]);
	}
	
	public static Cookie[] cloneCookies(Config config,HttpServletRequest req) {
		Cookie[] src=ReqRspUtil.getCookies(config, req);
		if(src==null)return new Cookie[0];
		
		Cookie[] dest=new Cookie[src.length];
		for(int i=0;i<src.length;i++) {
			dest[i]=(Cookie) src[i].clone();
		}
		return dest;
	}

}
