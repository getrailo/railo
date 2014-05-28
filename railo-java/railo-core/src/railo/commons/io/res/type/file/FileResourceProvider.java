package railo.commons.io.res.type.file;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import railo.commons.io.SystemUtil;
import railo.commons.io.res.Resource;
import railo.commons.io.res.ResourceProvider;
import railo.commons.io.res.ResourceProviderPro;
import railo.commons.io.res.Resources;
import railo.commons.io.res.util.ResourceLockImpl;
import railo.commons.io.res.util.ResourceUtil;
import railo.commons.lang.SizeOf;
import railo.commons.lang.StringUtil;
import railo.runtime.op.Caster;
import railo.runtime.type.Sizeable;

public final class FileResourceProvider implements ResourceProviderPro,Sizeable {

	private String scheme="file";
	
	private long lockTimeout=10000;
	private boolean caseSensitive=SystemUtil.isFSCaseSensitive();
	private final ResourceLockImpl lock=new ResourceLockImpl(lockTimeout,caseSensitive);
	private Map arguments;

	@Override
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
	
	@Override
	public Resource getResource(String path) {
		return new FileResource(this,ResourceUtil.removeScheme("file",path));
	}

	@Override
	public String getScheme() {
		return scheme;
	}
	
	@Override
	public void setResources(Resources resources) {
		//this.resources=resources;
	}
	

	@Override
	public void lock(Resource res) throws IOException {
		lock.lock(res);
	}

	@Override
	public void unlock(Resource res) {
		lock.unlock(res);
	}

	@Override
	public void read(Resource res) throws IOException {
		lock.read(res);
	}
	
	@Override
	public boolean isAttributesSupported() {
		return SystemUtil.isWindows();
	}
	
	@Override
	public boolean isCaseSensitive() {
		return caseSensitive;
	}
	
	@Override
	public boolean isModeSupported() {
		return false;//SystemUtil.isUnix(); FUTURE add again
	}
	@Override
	public long sizeOf() {
		return SizeOf.size(lock);
	}
	
	public Map getArguments() {
		return arguments;
	}
	
	@Override
	public char getSeparator() {
		return File.separatorChar;
	}
}
