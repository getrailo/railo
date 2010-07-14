package railo.runtime.net.http;

import java.util.Enumeration;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;


public class ServletConfigDummy implements ServletConfig {

	private String servletName;
	private ServletContext context;

	/**
	 * Constructor of the class
	 * @param parameters
	 * @param attrs
	 * @param servletName
	 */
	public ServletConfigDummy(ServletContextDummy context,String servletName){
		this.servletName=servletName;
		this.context=context;
	}
	
	/**
	 * @see javax.servlet.ServletConfig#getInitParameter(java.lang.String)
	 */
	public String getInitParameter(String key) {
		return context.getInitParameter(key);
	}

	/**
	 * @see javax.servlet.ServletConfig#getInitParameterNames()
	 */
	public Enumeration getInitParameterNames() {
		return context.getInitParameterNames();
	}

	/**
	 * @see javax.servlet.ServletConfig#getServletName()
	 */
	public String getServletName() {
		return servletName;
	}

	public ServletContext getServletContext() {
		return context;
	}
}