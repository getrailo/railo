package railo.runtime.net.http;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Set;

import javax.servlet.RequestDispatcher;
import javax.servlet.Servlet;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import railo.commons.io.log.Log;
import railo.commons.io.log.LogConsole;
import railo.commons.io.res.Resource;
import railo.commons.io.res.util.ResourceUtil;
import railo.commons.lang.ExceptionUtil;
import railo.runtime.config.Config;
import railo.runtime.op.Caster;
import railo.runtime.type.KeyImpl;
import railo.runtime.type.Struct;
import railo.runtime.type.it.ItAsEnum;
import railo.runtime.util.EnumerationWrapper;

public class ServletContextDummy implements ServletContext {
	private Struct attributes;
	private Struct parameters;
	private int majorVersion;
	private int minorVersion;
	private Config config;
	private LogConsole log;
	private Resource root;
	
	
	public ServletContextDummy(Config config,Resource root,Struct attributes,Struct parameters,int majorVersion, int minorVersion) {
		this.config=config;
		this.root=root;
		this.attributes=attributes;
		this.parameters=parameters;
		this.majorVersion=majorVersion;
		this.minorVersion=minorVersion;
		log=new LogConsole(Log.LEVEL_INFO,config.getOutWriter());
		
	}

	@Override
	public Object getAttribute(String key) {
		return attributes.get(key,null);
	}

	@Override
	public Enumeration getAttributeNames() {
		return ItAsEnum.toStringEnumeration(attributes.keyIterator());
	}
	
	@Override
	public String getInitParameter(String key) {
		return Caster.toString(parameters.get(key,null),null);
	}

	@Override
	public Enumeration getInitParameterNames() {
		return new EnumerationWrapper(parameters.keyIterator());
	}

	@Override
	public int getMajorVersion() {
		return majorVersion;
	}

	@Override
	public int getMinorVersion() {
		return minorVersion;
	}

	@Override
	public String getMimeType(String file) {
		return ResourceUtil.getMimeType(config.getResource(file),null);
	}

	@Override
	public String getRealPath(String realpath) {
		return root.getRealResource(realpath).getAbsolutePath();
	}

	@Override
	public URL getResource(String realpath) throws MalformedURLException {
		Resource res = getRealResource(realpath);
		if(res instanceof File)return ((File)res).toURL();
		return new URL(res.getAbsolutePath());
	}

	@Override
	public InputStream getResourceAsStream(String realpath) {
		try {
			return getRealResource(realpath).getInputStream();
		} catch (IOException e) {
			return null;
		}
	}

	public Resource getRealResource(String realpath) {
		return root.getRealResource(realpath);
	}

	public Set getResourcePaths(String realpath) {
		// TODO Auto-generated method stub
		return null;
	}

	public RequestDispatcher getRequestDispatcher(String path) {
		// TODO Auto-generated method stub
		return null;
	}

	public ServletContext getContext(String key) {
		// TODO ?
		return this;
	}

	public RequestDispatcher getNamedDispatcher(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void log(String msg, Throwable t) {
		if(t==null)log.log(Log.LEVEL_INFO, "ServletContext", msg);
		else log.log(Log.LEVEL_ERROR, "ServletContext", msg+":\n"+ExceptionUtil.getStacktrace(t,false));
	}

	@Override
	public void log(Exception e, String msg) {
		log(msg,e);
	}

	@Override
	public void log(String msg) {
		log(msg,null);
	}

	@Override
	public void removeAttribute(String key) {
		attributes.removeEL(KeyImpl.init(key));
	}

	@Override
	public void setAttribute(String key, Object value) {
		attributes.setEL(KeyImpl.init(key), value);
	}
	
	


	public String getServletContextName() {
		// can return null
		return null;
	}

	public String getServerInfo() {
		// deprecated
		return null;
	}

	public Servlet getServlet(String arg0) throws ServletException {
		// deprecated
		return null;
	}

	public Enumeration getServletNames() {
		// deprecated
		return null;
	}

	public Enumeration getServlets() {
		// deprecated
		return null;
	}

}
