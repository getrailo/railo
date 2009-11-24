package railo.runtime.net.http;

import javax.servlet.http.HttpServletRequestWrapper;

import railo.runtime.PageContext;
import railo.runtime.PageSource;

public class HttpServletRequestMod extends HttpServletRequestWrapper {


	private String requestURI;
	private String queryString="";

	public HttpServletRequestMod(PageContext pc,String realPath) {
		super(pc.getHttpServletRequest());
		requestURI=realPath;
		int index;
		
		if((index=realPath.indexOf('?'))!=-1){
			requestURI=realPath.substring(0,index);
			queryString=realPath.substring(index+1);
		}
		PageSource ps = pc.getRelativePageSource(requestURI);
		requestURI=ps.getFullRealpath();
		
		
	}

	/**
	 * @see javax.servlet.http.HttpServletRequest#getRequestURI()
	 */
	public String getRequestURI() {
		return requestURI;
	}
	

	/**
	 * @see javax.servlet.http.HttpServletRequest#getRequestURL()
	 */
	public StringBuffer getRequestURL() {
		return new StringBuffer(isSecure()?"https":"http").
			append("://").
			append(getServerName()).
			append(':').
			append(getServerPort()).
			append('/').
			append(requestURI);
	}
	
	/**
	 * @see javax.servlet.http.HttpServletRequest#getServletPath()
	 */
	public String getServletPath() {
		return requestURI;
	}
	
	/**
	 * @see javax.servlet.http.HttpServletRequestWrapper#getQueryString()
	 */
	public String getQueryString() {
		return queryString;
	}
}
