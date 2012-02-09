package railo.runtime.functions.cache;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Iterator;

import railo.commons.io.cache.Cache;
import railo.commons.io.cache.CacheEntryFilter;
import railo.commons.io.cache.exp.CacheException;
import railo.commons.lang.StringUtil;
import railo.runtime.PageContext;
import railo.runtime.cache.CacheConnection;
import railo.runtime.config.Config;
import railo.runtime.config.ConfigImpl;
import railo.runtime.config.ConfigWeb;
import railo.runtime.op.Caster;

public class Util {

	public static Cache getDefault(PageContext pc, int type) throws IOException {
		return getDefault(pc.getConfig(), type);
	}
	
	public static CacheConnection getDefaultCacheConnection(Config config, int type) throws IOException {
		CacheConnection cc= ((ConfigImpl)config).getCacheDefaultConnection(type);
		if(cc==null) throw new CacheException("there is no default "+toStringType(type,"")+" cache defined, you need to define this default cache in the Railo Administrator");
		return cc;
	}
	public static Cache getDefault(Config config, int type) throws IOException {
		CacheConnection cc= ((ConfigImpl)config).getCacheDefaultConnection(type);
		if(cc==null) throw new CacheException("there is no default "+toStringType(type,"")+" cache defined, you need to define this default cache in the Railo Administrator");
		return cc.getInstance(config);
	}
	
	public static Cache getDefault(Config config, int type,Cache defaultValue) {
		CacheConnection cc= ((ConfigImpl)config).getCacheDefaultConnection(type);
		
		if(cc==null) return defaultValue;
		try {
			return cc.getInstance(config);
		} catch (IOException e) {
			return defaultValue;
		}
	}
	

	public static CacheConnection getCacheConnection(Config config,String cacheName, int type) throws IOException {
		if(StringUtil.isEmpty(cacheName)){
			return getDefaultCacheConnection(config, type);
		}
		return getCacheConnection(config, cacheName);
	}

	public static Cache getCache(Config config,String cacheName, int type) throws IOException {
		if(StringUtil.isEmpty(cacheName)){
			return getDefault(config, type);
		}
		return getCache(config, cacheName);
	}

	public static Cache getCache(Config config,String cacheName, int type, Cache defaultValue)  {
		if(StringUtil.isEmpty(cacheName)){
			return getDefault(config, type,defaultValue);
		}
		return getCache(config, cacheName,defaultValue);
	}
	
	/**
	 * @param pc
	 * @param cacheName
	 * @param type
	 * @return
	 * @throws IOException
	 * @deprecated use <code>getCache(Config config,String cacheName, int type)</code> instead
	 */
	public static Cache getCache(PageContext pc,String cacheName, int type) throws IOException {
		return getCache(pc.getConfig(), cacheName);
	}
	/**
	 * @param pc
	 * @param cacheName
	 * @return
	 * @throws IOException
	 * @deprecated use <code>getCache(Config config,String cacheName)</code> instead
	 */
	public static Cache getCache(PageContext pc,String cacheName) throws IOException {
		return getCache(pc.getConfig(), cacheName);
	}
	public static Cache getCache(Config config,String cacheName) throws IOException {
		CacheConnection cc= (CacheConnection) ((ConfigImpl)config).getCacheConnections().get(cacheName.toLowerCase().trim());
		if(cc==null) throw noCache(config,cacheName);
		return cc.getInstance(config);	
	}
	public static Cache getCache(Config config,String cacheName, Cache defaultValue) {
		CacheConnection cc= (CacheConnection) ((ConfigImpl)config).getCacheConnections().get(cacheName.toLowerCase().trim());
		if(cc==null) return defaultValue;
		try {
			return cc.getInstance(config);
		} catch (IOException e) {
			return defaultValue;
		}	
	}
	public static CacheConnection getCacheConnection(Config config,String cacheName) throws IOException {
		CacheConnection cc= (CacheConnection) ((ConfigImpl)config).getCacheConnections().get(cacheName.toLowerCase().trim());
		if(cc==null) throw noCache(config,cacheName);
		return cc;	
	}
	private static CacheException noCache(Config config, String cacheName) {
		StringBuilder sb=new StringBuilder("there is no cache defined with name [").append(cacheName).append("], available caches are [");
		Iterator it = ((ConfigImpl)config).getCacheConnections().keySet().iterator();
		if(it.hasNext()){
			sb.append(Caster.toString(it.next(),""));
		}
		while(it.hasNext()){
			sb.append(", ").append(Caster.toString(it.next(),""));
		}
		sb.append("]");
		
		return new CacheException(sb.toString());
	}

	public static CacheConnection getCacheConnection(Config config,String cacheName, CacheConnection defaultValue) {
		CacheConnection cc= (CacheConnection) ((ConfigImpl)config).getCacheConnections().get(cacheName.toLowerCase().trim());
		if(cc==null) return defaultValue;
		return cc;	
	}

	private static String toStringType(int type, String defaultValue) {
		if(type==ConfigImpl.CACHE_DEFAULT_OBJECT) return "object";
		if(type==ConfigImpl.CACHE_DEFAULT_TEMPLATE) return "template";
		if(type==ConfigImpl.CACHE_DEFAULT_QUERY) return "query";
		if(type==ConfigImpl.CACHE_DEFAULT_RESOURCE) return "resource";
		return defaultValue;
	}

	public static String key(String key) {
		return key.toUpperCase().trim();
	}


	public static boolean removeEL(ConfigWeb config, CacheConnection cc)  {
		try {
			remove(config,cc);
			return true;
		} catch (Throwable e) {
			return false;
		}
	}
	public static void remove(ConfigWeb config, CacheConnection cc) throws Throwable  {
		Cache c = cc.getInstance(config);
		// FUTURE no reflection needed
		Method remove=null;
		try{
			remove = c.getClass().getMethod("remove", new Class[0]);
			
		}
		catch(Exception ioe){
			c.remove((CacheEntryFilter)null);
			return;
		}
		
		try {
			remove.invoke(c, new Object[0]);
		}
		catch (InvocationTargetException e) {
			throw e.getTargetException();
		}
	}

	public static int toType(String type, int defaultValue) {
		type=type.trim().toLowerCase();
		if("object".equals(type)) return ConfigImpl.CACHE_DEFAULT_OBJECT;
		if("query".equals(type)) return ConfigImpl.CACHE_DEFAULT_QUERY;
		if("resource".equals(type)) return ConfigImpl.CACHE_DEFAULT_RESOURCE;
		if("template".equals(type)) return ConfigImpl.CACHE_DEFAULT_TEMPLATE;
		return defaultValue;
	}

	public static String toType(int type, String defaultValue) {
		if(ConfigImpl.CACHE_DEFAULT_OBJECT==type) return "object";
		if(ConfigImpl.CACHE_DEFAULT_QUERY==type) return "query";
		if(ConfigImpl.CACHE_DEFAULT_RESOURCE==type) return "resource";
		if(ConfigImpl.CACHE_DEFAULT_TEMPLATE==type) return "template";
		return defaultValue;
	}
}
