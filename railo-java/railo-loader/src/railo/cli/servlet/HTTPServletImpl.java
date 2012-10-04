package railo.cli.servlet;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServlet;

public class HTTPServletImpl extends HttpServlet {
	private static final long serialVersionUID = 3270816399105433603L;
	
	private ServletConfig config;
	private ServletContext context;
	private String servletName;
	
	public HTTPServletImpl(ServletConfig config,ServletContext context, String servletName){
		this.config=config;
		this.context=context;
		this.servletName=servletName;
	}
	
	/**
	 * @see javax.servlet.GenericServlet#getServletConfig()
	 */
	public ServletConfig getServletConfig() {
		return config;
	}

	/**
	 * @see javax.servlet.GenericServlet#getServletContext()
	 */
	public ServletContext getServletContext() {
		return context;
	}

	/**
	 * @see javax.servlet.GenericServlet#getServletName()
	 */
	@Override
	public String getServletName() {
		return servletName;
	}

}
