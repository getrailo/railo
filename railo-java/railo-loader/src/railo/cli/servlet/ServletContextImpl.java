package railo.cli.servlet;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Map;
import java.util.Set;

import javax.servlet.RequestDispatcher;
import javax.servlet.Servlet;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import railo.cli.util.EnumerationWrapper;

public class ServletContextImpl implements ServletContext {
	private Map<String,Object> attributes;
	private Map<String, String> parameters;
	private int majorVersion;
	private int minorVersion;
	private File root;
	
	
	public ServletContextImpl(File root,Map<String,Object> attributes,Map<String, String> parameters,int majorVersion, int minorVersion) {
		this.root=root;
		this.attributes=attributes;
		this.parameters=parameters;
		this.majorVersion=majorVersion;
		this.minorVersion=minorVersion;
	}

	/**
	 * @see javax.servlet.ServletContext#getAttribute(java.lang.String)
	 */
	public Object getAttribute(String key) {
		return attributes.get(key);
	}

	/**
	 * @see javax.servlet.ServletContext#getAttributeNames()
	 */
	public Enumeration getAttributeNames() {
		return new EnumerationWrapper(attributes);
	}
	
	/**
	 * @see javax.servlet.ServletContext#getInitParameter(java.lang.String)
	 */
	public String getInitParameter(String key) {
		return parameters.get(key);
	}

	/**
	 * @see javax.servlet.ServletContext#getInitParameterNames()
	 */
	public Enumeration getInitParameterNames() {
		return new EnumerationWrapper(parameters);
	}

	/**
	 * @see javax.servlet.ServletContext#getMajorVersion()
	 */
	public int getMajorVersion() {
		return majorVersion;
	}

	/**
	 * @see javax.servlet.ServletContext#getMinorVersion()
	 */
	public int getMinorVersion() {
		return minorVersion;
	}

	/**
	 * @see javax.servlet.ServletContext#getMimeType(java.lang.String)
	 */
	public String getMimeType(String file) {
		throw notSupported("getMimeType(String file)");
		// TODO
		//return ResourceUtil.getMimeType(config.getResource(file),null);
	}

	@Override
	public String getRealPath(String relpath) {
		return getRealFile(relpath).getAbsolutePath();
	}

	/**
	 * @see javax.servlet.ServletContext#getResource(java.lang.String)
	 */
	public URL getResource(String relpath) throws MalformedURLException {
		File file = getRealFile(relpath);
		return file.toURL();
	}

	/**
	 * @see javax.servlet.ServletContext#getResourceAsStream(java.lang.String)
	 */
	public InputStream getResourceAsStream(String relpath) {
		try {
			return new FileInputStream(getRealFile(relpath));
		} catch (IOException e) {
			return null;
		}
	}

	public File getRealFile(String relpath) {
		return new File(root,relpath);
	}

	public File getRoot() {
		return root;
	}

	public Set getResourcePaths(String relpath) {
		throw notSupported("getResourcePaths(String relpath)");
	}

	public RequestDispatcher getRequestDispatcher(String path) {
		throw notSupported("getNamedDispatcher(String name)");
	}

	public ServletContext getContext(String key) {
		// TODO ?
		return this;
	}

	public RequestDispatcher getNamedDispatcher(String name) {
		// TODO Auto-generated method stub
		throw notSupported("getNamedDispatcher(String name)");
	}

	/**
	 * @see javax.servlet.ServletContext#log(java.lang.String, java.lang.Throwable)
	 */
	public void log(String msg, Throwable t) {// TODO better
		if(t==null)System.out.println(msg);
		else System.out.println(msg+":\n"+t.getMessage());
	
		
		//if(t==null)log.log(Log.LEVEL_INFO, "ServletContext", msg);
		//else log.log(Log.LEVEL_ERROR, "ServletContext", msg+":\n"+ExceptionUtil.getStacktrace(t,false));
	}

	/**
	 * @see javax.servlet.ServletContext#log(java.lang.Exception, java.lang.String)
	 */
	public void log(Exception e, String msg) {
		log(msg,e);
	}

	/**
	 * @see javax.servlet.ServletContext#log(java.lang.String)
	 */
	public void log(String msg) {
		log(msg,null);
	}

	/**
	 * @see javax.servlet.ServletContext#removeAttribute(java.lang.String)
	 */
	public void removeAttribute(String key) {
		attributes.remove(key);
	}

	/**
	 * @see javax.servlet.ServletContext#setAttribute(java.lang.String, java.lang.Object)
	 */
	public void setAttribute(String key, Object value) {
		attributes.put(key, value);
	}
	
	


	public String getServletContextName() {
		// can return null
		return null;
	}

	public String getServerInfo() {
		// deprecated
		throw notSupported("getServlet()");
	}

	public Servlet getServlet(String arg0) throws ServletException {
		// deprecated
		throw notSupported("getServlet()");
	}

	public Enumeration getServletNames() {
		// deprecated
		throw notSupported("getServlet()");
	}

	public Enumeration getServlets() {
		// deprecated
		throw notSupported("getServlet()");
	}

	private RuntimeException notSupported(String method) {
		throw new RuntimeException(new ServletException("method "+method+" not supported"));
	}

}
