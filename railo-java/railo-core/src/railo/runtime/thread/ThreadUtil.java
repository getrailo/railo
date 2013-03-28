package railo.runtime.thread;

import java.io.OutputStream;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import railo.commons.io.DevNullOutputStream;
import railo.commons.lang.Pair;
import railo.runtime.CFMLFactory;
import railo.runtime.CFMLFactoryImpl;
import railo.runtime.PageContext;
import railo.runtime.PageContextImpl;
import railo.runtime.config.Config;
import railo.runtime.config.ConfigImpl;
import railo.runtime.config.ConfigWeb;
import railo.runtime.net.http.HTTPServletRequestWrap;
import railo.runtime.net.http.HttpServletRequestDummy;
import railo.runtime.net.http.HttpServletResponseDummy;
import railo.runtime.type.Struct;

public class ThreadUtil {
	

	public static  PageContextImpl clonePageContext(PageContext pc, OutputStream os, boolean stateless,boolean registerPC,boolean isChild)  {
		// TODO stateless
		CFMLFactoryImpl factory = (CFMLFactoryImpl) ((ConfigImpl)pc.getConfig()).getFactory();
        HttpServletRequest	req=new HTTPServletRequestWrap(cloneHttpServletRequest(pc));
        HttpServletResponse	rsp=createHttpServletResponse(os);
        
        // copy state
        PageContextImpl pci = (PageContextImpl) pc;
		PageContextImpl dest = factory.getPageContextImpl(factory.getServlet(), req, rsp, null, false, -1, false,registerPC, isChild);
		pci.copyStateTo(dest);
		return dest;
	}

	public static  PageContextImpl createPageContext(ConfigWeb config,OutputStream os,String serverName,String requestURI,String queryString,Cookie[] cookies,Pair[] headers,Pair[] parameters,Struct attributes)  {
		CFMLFactory factory = config.getFactory();
        HttpServletRequest	req = new HttpServletRequestDummy(
				config.getRootDirectory(),
				serverName,
				requestURI,
				queryString,
				cookies,
				headers,
				parameters,
				attributes,
				null
			);
		
		
		req=new HTTPServletRequestWrap(req);
        HttpServletResponse	rsp=createHttpServletResponse(os);
        
        return (PageContextImpl) factory.getRailoPageContext(
        		factory.getServlet(), 
        		req, rsp, null, false, -1, false);
		
	}
	

	public static HttpServletRequest cloneHttpServletRequest(PageContext pc) {
		Config config = pc.getConfig();
		HttpServletRequest req = pc.getHttpServletRequest();
		HttpServletRequestDummy dest = HttpServletRequestDummy.clone(config,config.getRootDirectory(),req);
		return dest;
	}
	
	public static HttpServletResponse createHttpServletResponse(OutputStream os) {
		if(os==null) os = DevNullOutputStream.DEV_NULL_OUTPUT_STREAM;
		
		HttpServletResponseDummy dest = new HttpServletResponseDummy(os);
		return dest;
	}

	/**
	 * return priority as a String representation
	 * @param priority Thread priority
	 * @return String defintion of priority (null when input is invalid)
	 */
	public static String toStringPriority(int priority) {
		if(priority==Thread.NORM_PRIORITY) return "NORMAL";
		if(priority==Thread.MAX_PRIORITY) return "HIGH";
		if(priority==Thread.MIN_PRIORITY) return "LOW";
		return null;
	}
	
	/** 
	 * return priority as a int representation
	 * @param priority Thread priority as String definition
	 * @return int defintion of priority (-1 when input is invalid)
	 */
	public static int toIntPriority(String strPriority) {
		strPriority=strPriority.trim().toLowerCase();
		
		if("low".equals(strPriority))		return Thread.MIN_PRIORITY;
		if("min".equals(strPriority))		return Thread.MIN_PRIORITY;
		if("high".equals(strPriority))		return Thread.MAX_PRIORITY;
		if("max".equals(strPriority))		return Thread.MAX_PRIORITY;
		if("normal".equals(strPriority))	return Thread.NORM_PRIORITY;
		if("norm".equals(strPriority))		return Thread.NORM_PRIORITY;
		return -1;
	}
}
