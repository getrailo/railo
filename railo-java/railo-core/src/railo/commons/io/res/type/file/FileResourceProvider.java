

package railo.commons.io.res.type.file;

import java.io.IOException;
import java.util.Map;

import railo.commons.io.SystemUtil;
import railo.commons.io.res.Resource;
import railo.commons.io.res.ResourceProvider;
import railo.commons.io.res.Resources;
import railo.commons.io.res.util.ResourceLockImpl;
import railo.commons.io.res.util.ResourceUtil;
import railo.commons.lang.SizeOf;
import railo.commons.lang.StringUtil;
import railo.runtime.op.Caster;
import railo.runtime.type.Sizeable;

public final class FileResourceProvider implements ResourceProvider,Sizeable {

	private String scheme="file";
	
	private long lockTimeout=10000;
	private boolean caseSensitive=SystemUtil.isFSCaseSensitive();
	private final ResourceLockImpl lock=new ResourceLockImpl(lockTimeout,caseSensitive);
	private Map arguments;

	/**
	 * @see railo.commons.io.res.ResourceProvider#init(java.lang.String, java.util.Map)
	 */
	public ResourceProvider init(String scheme, Map arguments) {
		if(!StringUtil.isEmpty(scheme))this.scheme=scheme;
		this.arguments=arguments;
		if(arguments!=null) {
			// lock-timeout
			String strTimeout = (String) arguments.get("lock-timeout");
			if(strTimeout!=null) {
				lockTimeout=Caster.toLongValue(arguments.get("lock-timeout"),lockTimeout);
			}
		}
		lock.setLockTimeout(lockTimeout);
		
		return this;
	}
	/**
	 * Constructor of the class
	 */
	public FileResourceProvider() {}
	
	/**
	 * @see res.ResourceProvider#getResource(java.lang.String)
	 */
	public Resource getResource(String path) {
		return new FileResource(this,ResourceUtil.removeScheme("file",path));
	}

	/**
	 * @see res.ResourceProvider#getScheme()
	 */
	public String getScheme() {
		return scheme;
	}
	
	/**
	 * @see railo.commons.io.res.ResourceProvider#setResources(railo.commons.io.res.Resources)
	 */
	public void setResources(Resources resources) {
		//this.resources=resources;
	}
	

	/**
	 * @throws IOException 
	 * @see railo.commons.io.res.ResourceProvider#lock(railo.commons.io.res.Resource)
	 */
	public void lock(Resource res) throws IOException {
		lock.lock(res);
	}

	/**
	 * @see railo.commons.io.res.ResourceProvider#unlock(railo.commons.io.res.Resource)
	 */
	public void unlock(Resource res) {
		lock.unlock(res);
	}

	/**
	 * @throws IOException 
	 * @see railo.commons.io.res.ResourceProvider#read(railo.commons.io.res.Resource)
	 */
	public void read(Resource res) throws IOException {
		lock.read(res);
	}
	
	/**
	 * @see railo.commons.io.res.ResourceProvider#isAttributesSupported()
	 */
	public boolean isAttributesSupported() {
		return SystemUtil.isWindows();
	}
	
	/**
	 * @see railo.commons.io.res.ResourceProvider#isCaseSensitive()
	 */
	public boolean isCaseSensitive() {
		return caseSensitive;
	}
	
	/**
	 * @see railo.commons.io.res.ResourceProvider#isModeSupported()
	 */
	public boolean isModeSupported() {
		return false;//SystemUtil.isUnix(); FUTURE add again
	}
	/**
	 * @see railo.runtime.type.Sizeable#sizeOf()
	 */
	public long sizeOf() {
		return SizeOf.size(lock);
	}
	
	public Map getArguments() {
		return arguments;
	}
}
