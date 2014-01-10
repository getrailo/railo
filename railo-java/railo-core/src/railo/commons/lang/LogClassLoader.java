package railo.commons.lang;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;

import railo.commons.io.log.Log;

public final class LogClassLoader extends ClassLoader {
	
	private final ClassLoader cl;
	private final Log log;

	public LogClassLoader(ClassLoader cl,Log log){
		this.cl=cl;
		this.log=log;
	}

	@Override
	public synchronized void clearAssertionStatus() {
		log.debug("LogClassLoader", "clearAssertion");
		cl.clearAssertionStatus();
	}

	@Override
	protected Package definePackage(String name, String specTitle, String specVersion, String specVendor, String implTitle, String implVersion, String implVendor, URL sealBase) throws IllegalArgumentException {
		log.debug("LogClassLoader", "definePackage");
		return null;
	}

	@Override
	protected Class findClass(String name) throws ClassNotFoundException {
		log.debug("LogClassLoader", "findClass");
		return null;
	}

	@Override
	protected String findLibrary(String libname) {
		log.debug("LogClassLoader", "findLibrary");
		return null;
	}

	@Override
	protected URL findResource(String name) {
		log.debug("LogClassLoader", "findResource");
		return null;
	}

	@Override
	protected Enumeration findResources(String name) throws IOException {
		log.debug("LogClassLoader", "findResources");
		return null;
	}

	@Override
	protected Package getPackage(String name) {
		log.debug("LogClassLoader", "getPackage");
		return null;
	}

	@Override
	protected Package[] getPackages() {
		log.debug("LogClassLoader", "getPackages");
		return null;
	}

	@Override
	public URL getResource(String name) {
		log.debug("LogClassLoader", "getResource");
		return cl.getResource(name);
	}

	@Override
	public InputStream getResourceAsStream(String name) {
		log.debug("LogClassLoader", "getResourceAsStream");
		return cl.getResourceAsStream(name);
	}

	@Override
	protected synchronized Class loadClass(String name, boolean resolve) throws ClassNotFoundException {
		log.debug("LogClassLoader", "loadClass");
		return null;
	}

	@Override
	public Class loadClass(String name) throws ClassNotFoundException {
		Class clazz = cl.loadClass(name);
		log.debug("LogClassLoader", "loadClass("+name+"):"+clazz);
		return clazz;
	}

	@Override
	public synchronized void setClassAssertionStatus(String className, boolean enabled) {
		log.debug("LogClassLoader", "setClassAssertionStatus");
		cl.setClassAssertionStatus(className, enabled);
	}

	@Override
	public synchronized void setDefaultAssertionStatus(boolean enabled) {
		log.debug("LogClassLoader", "setdefaultAssertionStatus");
		cl.setDefaultAssertionStatus(enabled);
	}

	@Override
	public synchronized void setPackageAssertionStatus(String packageName, boolean enabled) {
		log.debug("LogClassLoader", "setPackageAssertionStatus");
		cl.setPackageAssertionStatus(packageName, enabled);
	}
}
