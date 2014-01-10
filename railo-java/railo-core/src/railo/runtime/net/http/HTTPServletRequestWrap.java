package railo.runtime.net.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import railo.commons.io.IOUtil;
import railo.commons.lang.StringUtil;
import railo.commons.net.URLItem;
import railo.runtime.PageContext;
import railo.runtime.engine.ThreadLocalPageContext;
import railo.runtime.type.scope.Form;
import railo.runtime.type.scope.FormImpl;
import railo.runtime.type.scope.URL;
import railo.runtime.type.scope.URLImpl;
import railo.runtime.type.scope.UrlFormImpl;
import railo.runtime.type.scope.util.ScopeUtil;
import railo.runtime.util.EnumerationWrapper;

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
	private HashMap<String, Object> disconnectedData;
	private boolean disconnected;
	private HttpServletRequest req;
	//private Request _request;

	/**
	 * Constructor of the class
	 * @param req 
	 * @param max how many is possible to re read
	 */
	public HTTPServletRequestWrap(HttpServletRequest req) {
		super(req);
		this.req=pure(req);
		
		if((servlet_path=attrAsString("javax.servlet.include.servlet_path"))!=null){
			request_uri=attrAsString("javax.servlet.include.request_uri");
			context_path=attrAsString("javax.servlet.include.context_path");
			path_info=attrAsString("javax.servlet.include.path_info");
			query_string = attrAsString("javax.servlet.include.query_string");
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
	
	private String attrAsString(String key) {
		Object res = getAttribute(key);
		if(res==null) return null;
		return res.toString();
	}
	
	public static HttpServletRequest pure(HttpServletRequest req) {
		HttpServletRequest req2;
		while(req instanceof HTTPServletRequestWrap){
			req2 =  ((HTTPServletRequestWrap)req).getOriginalRequest();
			if(req2==req) break;
			req=req2;
		}
		return req;
	}

	@Override
	public String getContextPath() {
		return context_path;
	}
	
	@Override
	public String getPathInfo() {
		return path_info;
	}
	
	@Override
	public StringBuffer getRequestURL() {
		return new StringBuffer(isSecure()?"https":"http").
			append("://").
			append(getServerName()).
			append(':').
			append(getServerPort()).
			append(request_uri.startsWith("/")?request_uri:"/"+request_uri);
	}
	
	@Override
	public String getQueryString() {
		return query_string;
	}
	@Override
	public String getRequestURI() {
		return request_uri;
	}
	
	@Override
	public String getServletPath() {
		return servlet_path;
	}
	
	@Override
	public RequestDispatcher getRequestDispatcher(String realpath) {
		return new RequestDispatcherWrap(this,realpath);
	}
	
	public RequestDispatcher getOriginalRequestDispatcher(String realpath) {
		if(disconnected) return null;
		return req.getRequestDispatcher(realpath);
	}

	@Override
	public void removeAttribute(String name) {
		if(disconnected) disconnectedData.remove(name); 
		else req.removeAttribute(name);
	}

	@Override
	public void setAttribute(String name, Object value) {
		if(disconnected) disconnectedData.put(name, value);
		else req.setAttribute(name, value);
	}
	
	/*public void setAttributes(Request request) {
		this._request=request;
	}*/


	@Override
	public Object getAttribute(String name) {
		if(disconnected) return disconnectedData.get(name);
		return req.getAttribute(name);
	}

	public Enumeration getAttributeNames() {
		if(disconnected) {
			return new EnumerationWrapper(disconnectedData);
		}
		return req.getAttributeNames();
		
	}

	@Override
	public ServletInputStream getInputStream() throws IOException {
		//if(ba rr!=null) throw new IllegalStateException();
		if(barr==null) {
			if(!firstRead) {
				PageContext pc = ThreadLocalPageContext.get();
				if(pc!=null) {
					return pc.formScope().getInputStream();
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
	
	@Override
	public Map<String,String[]> getParameterMap() {
		PageContext pc = ThreadLocalPageContext.get();
		FormImpl form=_form(pc);
		URLImpl url=_url(pc);
		
		return ScopeUtil.getParameterMap(
				new URLItem[][]{form.getRaw(),url.getRaw()}, 
				new String[]{form.getEncoding(),url.getEncoding()});
	}

	private static URLImpl _url(PageContext pc) {
		URL u = pc.urlScope();
		if(u instanceof UrlFormImpl) {
			return ((UrlFormImpl) u).getURL();
		}
		return (URLImpl) u;
	}

	private static FormImpl _form(PageContext pc) {
		Form f = pc.formScope();
		if(f instanceof UrlFormImpl) {
			return ((UrlFormImpl) f).getForm();
		}
		return (FormImpl) f;
	}

	@Override
	public Enumeration<String> getParameterNames() {
		return new ItasEnum<String>(getParameterMap().keySet().iterator());
	}

	@Override
	public String[] getParameterValues(String name) {
		return getParameterValues(ThreadLocalPageContext.get(), name); 
	}
	
	public static String[] getParameterValues(PageContext pc, String name) {
		pc = ThreadLocalPageContext.get(pc);
		FormImpl form = _form(pc);
		URLImpl url= _url(pc);
		
		return ScopeUtil.getParameterValues(
				new URLItem[][]{form.getRaw(),url.getRaw()}, 
				new String[]{form.getEncoding(),url.getEncoding()},name);
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

	@Override
	public BufferedReader getReader() throws IOException {
		String enc = getCharacterEncoding();
		if(StringUtil.isEmpty(enc))enc="iso-8859-1";
		return IOUtil.toBufferedReader(IOUtil.getReader(getInputStream(), enc));
	}
	
	public void clear() {
		barr=null;
	}

	


	public HttpServletRequest getOriginalRequest() {
		return req;
	}

	public void disconnect() {
		if(disconnected) return;
		Enumeration<String> names = req.getAttributeNames();
		disconnectedData=new HashMap<String, Object>();
		String k;
		while(names.hasMoreElements()){
			k=names.nextElement();
			disconnectedData.put(k, req.getAttribute(k));
		}
		disconnected=true;
		req=null;
	}
	
	static class ItasEnum<E> implements Enumeration<E> {

		private Iterator<E> it;

		public ItasEnum(Iterator<E> it){
			this.it=it;
		}
		@Override
		public boolean hasMoreElements() {
			return it.hasNext();
		}

		@Override
		public E nextElement() {
			return it.next();
		}
		
	}
}