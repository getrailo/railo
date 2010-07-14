package railo.runtime.net.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import railo.commons.io.IOUtil;
import railo.commons.lang.StringUtil;
import railo.runtime.PageContext;
import railo.runtime.engine.ThreadLocalPageContext;
import railo.runtime.type.scope.FormImpl;

/**
 * extends a existing {@link HttpServletRequest} with the possibility to reread the input as many you want.
 */
public final class HTTPServletRequestWrap extends HttpServletRequestWrapper implements Serializable {




	private boolean firstRead=true;
	private byte[] barr;
	private static final int MIN_STORAGE_SIZE=1*1024*1024;
	private static final int MAX_STORAGE_SIZE=50*1024*1024;
	private static final int SPACIG=1024*1024;
	
	private String servlet_path;
	private String request_uri;
	private String context_path;
	private String path_info;
	private String query_string;
	private HttpServletRequest req;

	/**
	 * Constructor of the class
	 * @param req 
	 * @param max how many is possible to re read
	 */
	public HTTPServletRequestWrap(HttpServletRequest req) {
		super(req);
		this.req=pure(req);
		
		if((servlet_path=attr("javax.servlet.include.servlet_path"))!=null){
			request_uri=attr("javax.servlet.include.request_uri");
			context_path=attr("javax.servlet.include.context_path");
			path_info=attr("javax.servlet.include.path_info");
			query_string = attr("javax.servlet.include.query_string");
		}
		
		//forward
		/*else if((servlet_path=attr("javax.servlet.forward.servlet_path"))!=null){
			request_uri=attr("javax.servlet.forward.request_uri");
			context_path=attr("javax.servlet.forward.context_path");
			path_info=attr("javax.servlet.forward.path_info");
			query_string = attr("javax.servlet.forward.query_string");
		}*/
		
		else {
			servlet_path=super.getServletPath();
			request_uri=super.getRequestURI();
			context_path=super.getContextPath();
			path_info=super.getPathInfo();
			query_string = super.getQueryString();
		}
		/*Enumeration names = req.getAttributeNames();
		while(names.hasMoreElements()){
			String key=(String)names.nextElement();
			print.out(key+"+"+req.getAttribute(key));
		}
		

		print.out("super:"+req.getClass().getName());
		print.out("servlet_path:"+servlet_path);
		print.out("request_uri:"+request_uri);
		print.out("path_info:"+path_info);
		print.out("query_string:"+query_string);
		
		print.out("servlet_path."+req.getServletPath());
		print.out("request_uri."+req.getRequestURI());
		print.out("path_info."+req.getPathInfo());
		print.out("query_string."+req.getQueryString());
		*/
	}
	
	private static HttpServletRequest pure(HttpServletRequest req) {
		HttpServletRequest req2;
		while(req instanceof HTTPServletRequestWrap){
			req2 = (HttpServletRequest) ((HTTPServletRequestWrap)req).getRequest();
			if(req2==req) break;
			req=req2;
		}
		return req;
	}

	/**
	 * @see javax.servlet.http.HttpServletRequestWrapper#getContextPath()
	 */
	public String getContextPath() {
		return context_path;
	}
	
	/**
	 * @see javax.servlet.http.HttpServletRequestWrapper#getPathInfo()
	 */
	public String getPathInfo() {
		return path_info;
	}
	
	/**
	 * @see javax.servlet.http.HttpServletRequestWrapper#getRequestURL()
	 */
	public StringBuffer getRequestURL() {
		return new StringBuffer(isSecure()?"https":"http").
			append("://").
			append(getServerName()).
			append(':').
			append(getServerPort()).
			append(request_uri.startsWith("/")?request_uri:"/"+request_uri);
	}
	
	/**
	 * @see javax.servlet.http.HttpServletRequestWrapper#getQueryString()
	 */
	public String getQueryString() {
		return query_string;
	}
	/**
	 * @see javax.servlet.http.HttpServletRequestWrapper#getRequestURI()
	 */
	public String getRequestURI() {
		return request_uri;
	}
	
	/**
	 * @see javax.servlet.http.HttpServletRequestWrapper#getServletPath()
	 */
	public String getServletPath() {
		return servlet_path;
	}
	
	/**
	 * @see javax.servlet.ServletRequestWrapper#getRequestDispatcher(java.lang.String)
	 */
	public RequestDispatcher getRequestDispatcher(String realpath) {
		return new RequestDispatcherWrap(this,realpath);
	}
	
	public RequestDispatcher getOriginalRequestDispatcher(String realpath) {
		return req.getRequestDispatcher(realpath);
	}

	/**
	 * @see javax.servlet.ServletRequestWrapper#removeAttribute(java.lang.String)
	 */
	public void removeAttribute(String name) {
		req.removeAttribute(name);
	}

	/**
	 * @see javax.servlet.ServletRequestWrapper#setAttribute(java.lang.String, java.lang.Object)
	 */
	public void setAttribute(String name, Object o) {
		req.setAttribute(name, o);
	}


	/**
	 * this method still throws a error if want read input stream a second time
	 * this is done to be compatibility with servletRequest class
	 * @see javax.servlet.ServletRequestWrapper#getInputStream()
	 */
	public ServletInputStream getInputStream() throws IOException {
		//if(ba rr!=null) throw new IllegalStateException();
		if(barr==null) {
			if(!firstRead) {
				PageContext pc = ThreadLocalPageContext.get();
				if(pc!=null) {
					return ((FormImpl)pc.formScope()).getInputStream();
				}
				return new ServletInputStreamDummy(new byte[]{});	//throw new IllegalStateException();
			}
			
			firstRead=false;
			if(isToBig(getContentLength())) {
				return super.getInputStream();
			}
			InputStream is=null;
			try {
				barr=IOUtil.toBytes(is=super.getInputStream());
				
				//Resource res = ResourcesImpl.getFileResourceProvider().getResource("/Users/mic/Temp/multipart.txt");
				//IOUtil.copy(new ByteArrayInputStream(barr), res, true);
				
			}
			catch(Throwable t) {
				barr=null;
				return new ServletInputStreamDummy(new byte[]{});	 
			}
			finally {
				IOUtil.closeEL(is);
			}
		}
		
		return new ServletInputStreamDummy(barr);	
	}
	
	private boolean isToBig(int contentLength) {
		if(contentLength<MIN_STORAGE_SIZE) return false;
		if(contentLength>MAX_STORAGE_SIZE) return true;
		Runtime rt = Runtime.getRuntime();
		long av = rt.maxMemory()-rt.totalMemory()+rt.freeMemory();
		return (av-SPACIG)<contentLength;
	}

	/* *
	 * with this method it is possibiliy to rewrite the input as many you want
	 * @return input stream from request
	 * @throws IOException
	 * /
	public ServletInputStream getStoredInputStream() throws IOException {
		if(firstRead || barr!=null) return getInputStream();
		return new ServletInputStreamDummy(new byte[]{});	 
	}*/

	/**
	 *
	 * @see javax.servlet.ServletRequestWrapper#getReader()
	 */
	public BufferedReader getReader() throws IOException {
		String enc = getCharacterEncoding();
		if(StringUtil.isEmpty(enc))enc="iso-8859-1";
		return IOUtil.toBufferedReader(IOUtil.getReader(getInputStream(), enc));
	}
	
	public void clear() {
		barr=null;
	}

	

	private String attr(String key) {
		return (String) req.getAttribute(key);
	}

	public HttpServletRequest getOriginalRequest() {
		return req;
	}
}