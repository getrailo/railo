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

	/**
	 *
	 * @see java.lang.ClassLoader#clearAssertionStatus()
	 */
	public synchronized void clearAssertionStatus() {
		log.debug("LogClassLoader", "clearAssertion");
		cl.clearAssertionStatus();
	}

	/**
	 *
	 * @see java.lang.ClassLoader#definePackage(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.net.URL)
	 */
	protected Package definePackage(String name, String specTitle, String specVersion, String specVendor, String implTitle, String implVersion, String implVendor, URL sealBase) throws IllegalArgumentException {
		log.debug("LogClassLoader", "definePackage");
		return null;
	}

	/**
	 *
	 * @see java.lang.ClassLoader#findClass(java.lang.String)
	 */
	protected Class findClass(String name) throws ClassNotFoundException {
		log.debug("LogClassLoader", "findClass");
		return null;
	}

	/**
	 *
	 * @see java.lang.ClassLoader#findLibrary(java.lang.String)
	 */
	protected String findLibrary(String libname) {
		log.debug("LogClassLoader", "findLibrary");
		return null;
	}

	/**
	 *
	 * @see java.lang.ClassLoader#findResource(java.lang.String)
	 */
	protected URL findResource(String name) {
		log.debug("LogClassLoader", "findResource");
		return null;
	}

	/**
	 *
	 * @see java.lang.ClassLoader#findResources(java.lang.String)
	 */
	protected Enumeration findResources(String name) throws IOException {
		log.debug("LogClassLoader", "findResources");
		return null;
	}

	/**
	 *
	 * @see java.lang.ClassLoader#getPackage(java.lang.String)
	 */
	protected Package getPackage(String name) {
		log.debug("LogClassLoader", "getPackage");
		return null;
	}

	/**
	 *
	 * @see java.lang.ClassLoader#getPackages()
	 */
	protected Package[] getPackages() {
		log.debug("LogClassLoader", "getPackages");
		return null;
	}

	/**
	 *
	 * @see java.lang.ClassLoader#getResource(java.lang.String)
	 */
	public URL getResource(String name) {
		log.debug("LogClassLoader", "getResource");
		return cl.getResource(name);
	}

	/**
	 *
	 * @see java.lang.ClassLoader#getResourceAsStream(java.lang.String)
	 */
	public InputStream getResourceAsStream(String name) {
		log.debug("LogClassLoader", "getResourceAsStream");
		return cl.getResourceAsStream(name);
	}

	/**
	 *
	 * @see java.lang.ClassLoader#loadClass(java.lang.String, boolean)
	 */
	protected synchronized Class loadClass(String name, boolean resolve) throws ClassNotFoundException {
		log.debug("LogClassLoader", "loadClass");
		return null;
	}

	/**
	 *
	 * @see java.lang.ClassLoader#loadClass(java.lang.String)
	 */
	public Class loadClass(String name) throws ClassNotFoundException {
		Class clazz = cl.loadClass(name);
		log.debug("LogClassLoader", "loadClass("+name+"):"+clazz);
		return clazz;
	}

	/**
	 *
	 * @see java.lang.ClassLoader#setClassAssertionStatus(java.lang.String, boolean)
	 */
	public synchronized void setClassAssertionStatus(String className, boolean enabled) {
		log.debug("LogClassLoader", "setClassAssertionStatus");
		cl.setClassAssertionStatus(className, enabled);
	}

	/**
	 *
	 * @see java.lang.ClassLoader#setDefaultAssertionStatus(boolean)
	 */
	public synchronized void setDefaultAssertionStatus(boolean enabled) {
		log.debug("LogClassLoader", "setdefaultAssertionStatus");
		cl.setDefaultAssertionStatus(enabled);
	}

	/**
	 *
	 * @see java.lang.ClassLoader#setPackageAssertionStatus(java.lang.String, boolean)
	 */
	public synchronized void setPackageAssertionStatus(String packageName, boolean enabled) {
		log.debug("LogClassLoader", "setPackageAssertionStatus");
		cl.setPackageAssertionStatus(packageName, enabled);
	}
}
