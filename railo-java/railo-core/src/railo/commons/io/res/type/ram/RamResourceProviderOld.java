package railo.commons.io.res.type.ram;

import java.io.IOException;
import java.util.Map;

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
import railo.runtime.type.util.ListUtil;

/**
 * Resource Provider for ram resource
 */
public final class RamResourceProviderOld implements ResourceProviderPro,Sizeable {

	private String scheme="ram";
	private RamResourceCore root;
	
	boolean caseSensitive=true;
	//private Resources resources;
	private long lockTimeout=1000;
	private ResourceLockImpl lock=new ResourceLockImpl(lockTimeout,caseSensitive);
	private Map arguments;

	
	/**
	 * initalize ram resource
	 * @param scheme
	 * @param arguments
	 * @return RamResource
	 */
	public ResourceProvider init(String scheme,Map arguments) {
		if(!StringUtil.isEmpty(scheme))this.scheme=scheme;
		
		if(arguments!=null) {
			this.arguments=arguments;
			Object oCaseSensitive= arguments.get("case-sensitive");
			if(oCaseSensitive!=null) {
				caseSensitive=Caster.toBooleanValue(oCaseSensitive,true);
			}
			
			// lock-timeout
			Object oTimeout = arguments.get("lock-timeout");
			if(oTimeout!=null) {
				lockTimeout=Caster.toLongValue(oTimeout,lockTimeout);
			}
		}
		lock.setLockTimeout(lockTimeout);
		lock.setCaseSensitive(caseSensitive);
		
		root=new RamResourceCore(null,RamResourceCore.TYPE_DIRECTORY,"");
		return this;
	}
	
	
	
	@Override
	public Resource getResource(String path) {
		path=ResourceUtil.removeScheme(scheme,path);
		return new RamResource(this,path);
	}
	
	/**
	 * returns core for this path if exists, otherwise return null
	 * @param path
	 * @return core or null
	 */
	RamResourceCore getCore(String path) {
		String[] names = ListUtil.listToStringArray(path,'/');
		
		
		RamResourceCore rrc=root;
		for(int i=0;i<names.length;i++) {
			rrc=rrc.getChild(names[i],caseSensitive);
			if(rrc==null) return null;
		}
		return rrc;
	}

	/**
	 * create a new core 
	 * @param path
	 * @param type
	 * @return created core
	 * @throws IOException
	 */
	RamResourceCore createCore(String path, int type) throws IOException {
		String[] names = ListUtil.listToStringArray(path,'/');
		RamResourceCore rrc=root;
		for(int i=0;i<names.length-1;i++) {
			rrc=rrc.getChild(names[i],caseSensitive);
			if(rrc==null) throw new IOException("can't create resource "+path+", missing parent resource");
		}
		rrc = new RamResourceCore(rrc,type,names[names.length-1]);
		return rrc;
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
		return true;
	}

	@Override
	public boolean isCaseSensitive() {
		return caseSensitive;
	}

	@Override
	public boolean isModeSupported() {
		return true;
	}

	@Override
	public long sizeOf() {
		return SizeOf.size(root)+SizeOf.size(lock);
	}

	@Override
	public Map getArguments() {
		return arguments;
	}
	
	@Override
	public char getSeparator() {
		return '/';
	}
}
