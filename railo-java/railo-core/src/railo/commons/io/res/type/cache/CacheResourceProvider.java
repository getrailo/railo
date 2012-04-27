package railo.commons.io.res.type.cache;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import railo.commons.io.cache.Cache;
import railo.commons.io.cache.CacheEntry;
import railo.commons.io.res.Resource;
import railo.commons.io.res.ResourceProvider;
import railo.commons.io.res.Resources;
import railo.commons.io.res.util.ResourceLockImpl;
import railo.commons.io.res.util.ResourceUtil;
import railo.commons.lang.StringUtil;
import railo.runtime.cache.ram.RamCache;
import railo.runtime.config.Config;
import railo.runtime.config.ConfigImpl;
import railo.runtime.engine.ThreadLocalPageContext;
import railo.runtime.functions.cache.Util;
import railo.runtime.op.Caster;
import railo.runtime.op.Constants;
import railo.runtime.type.Struct;


/**
 * Resource Provider for ram resource
 */
public final class CacheResourceProvider implements ResourceProvider {

	private String scheme="ram";
	
	boolean caseSensitive=true;
	private long lockTimeout=1000;
	private ResourceLockImpl lock=new ResourceLockImpl(lockTimeout,caseSensitive);
	private Map arguments;

	private final Cache DEFAULT_CACHE=new RamCache();

	private Config config;

	
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
		
		
		
		return this;
	}
	
	
	
	/**
	 * @see res.ResourceProvider#getResource(java.lang.String)
	 */
	public Resource getResource(String path) {
		path=ResourceUtil.removeScheme(scheme,path);
		if(!StringUtil.startsWith(path,'/'))path="/"+path;
		return new CacheResource(this,path);
	}
	
	/**
	 * returns core for this path if exists, otherwise return null
	 * @param path
	 * @return core or null
	 */
	CacheResourceCore getCore(String path,String name) {
		Object obj = getCache().getValue(toKey(path,name),null);
		if(obj instanceof CacheResourceCore) return (CacheResourceCore) obj;
		return null;
	}

	void touch(String path,String name) {
		Cache cache = getCache();
		CacheEntry ce = cache.getCacheEntry(toKey(path,name),null);
		if(ce!=null){
			cache.put(ce.getKey(), ce.getValue(), ce.idleTimeSpan(), ce.liveTimeSpan());
		}
	}
	

	Struct getMeta(String path,String name) {
		CacheEntry ce = getCache().getCacheEntry(toKey(path,name),null);
		if(ce!=null) return ce.getCustomInfo();
		return null;
	}

	String[] getChildNames(String path) {
		List list = getCache().values(new ChildrenFilter(path));
		String[] arr = new String[list.size()];
		Iterator it = list.iterator();
		int index=0;
		while(it.hasNext()){
			arr[index++]=((CacheResourceCore) it.next()).getName();
		}
		// TODO remove none CacheResourceCore elements
		return arr;
	}
	/*CacheResourceCore[] getChildren(String path) {
		List list = getCache().values(new ChildrenFilter(path));
		CacheResourceCore[] arr = new CacheResourceCore[list.size()];
		Iterator it = list.iterator();
		int index=0;
		while(it.hasNext()){
			arr[index++]=(CacheResourceCore) it.next();
		}
		// TODO remove none CacheResourceCore elements
		return arr;
	}*/




	/**
	 * create a new core 
	 * @param path
	 * @param type
	 * @return created core
	 * @throws IOException
	 */
	CacheResourceCore createCore(String path, String name, int type) throws IOException {
		CacheResourceCore value = new CacheResourceCore(type,path,name);
		getCache().put(toKey(path,name),value,null,null);
		return value;
	}



	CacheResourceCore createRoot() {
		CacheResourceCore value = new CacheResourceCore(CacheResourceCore.TYPE_DIRECTORY,null,"");
		getCache().put(toKey("null",""),value,Constants.LONG_ZERO,Constants.LONG_ZERO);
		return value;
	}
	


	void removeCore(String path, String name) {
		getCache().remove(toKey(path,name));
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
	 * @see railo.commons.io.res.ResourceProvider#read(railo.commons.io.res.Resource)
	 */
	public void read(Resource res) throws IOException {
		lock.read(res);
	}

	/**
	 *
	 * @see railo.commons.io.res.ResourceProvider#isAttributesSupported()
	 */
	public boolean isAttributesSupported() {
		return true;
	}

	/**
	 *
	 * @see railo.commons.io.res.ResourceProvider#isCaseSensitive()
	 */
	public boolean isCaseSensitive() {
		return caseSensitive;
	}

	/**
	 *
	 * @see railo.commons.io.res.ResourceProvider#isModeSupported()
	 */
	public boolean isModeSupported() {
		return true;
	}

	/**
	 * @see railo.commons.io.res.ResourceProvider#getArguments()
	 */
	public Map getArguments() {
		return arguments;
	}
	

	private Cache getCache() {
		if(config==null){
			config=ThreadLocalPageContext.getConfig();
			createRoot();
			
		}
		Cache c = Util.getDefault(config,ConfigImpl.CACHE_DEFAULT_RESOURCE,DEFAULT_CACHE);
		return c;
	}
	
	private String toKey(String path, String name) {
		if(caseSensitive) return path+":"+name;
		return (path+":"+name).toLowerCase();
	}


}
