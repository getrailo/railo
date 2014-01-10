package railo.runtime.cache.eh;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import railo.commons.io.CharsetUtil;
import railo.commons.io.cache.CacheEntry;
import railo.commons.io.cache.exp.CacheException;
import railo.commons.io.res.Resource;
import railo.commons.io.res.filter.ResourceNameFilter;
import railo.loader.engine.CFMLEngine;
import railo.loader.engine.CFMLEngineFactory;
import railo.loader.util.Util;
import railo.runtime.config.Config;
import railo.runtime.type.Struct;

public class EHCacheLite extends EHCacheSupport {
	
	private static final boolean DISK_PERSISTENT = true;
	private static final boolean ETERNAL = false;
	private static final int MAX_ELEMENTS_IN_MEMEORY = 10000;
	private static final int MAX_ELEMENTS_ON_DISK = 10000000;
	private static final String MEMORY_EVICTION_POLICY = "LRU";
	private static final boolean OVERFLOW_TO_DISK = true;
	private static final long TIME_TO_IDLE_SECONDS = 86400; 
	private static final long TIME_TO_LIVE_SECONDS = 86400;
	
	/*private static final boolean REPLICATE_PUTS = true;
	private static final boolean REPLICATE_PUTS_VIA_COPY = true;
	private static final boolean REPLICATE_UPDATES = true;
	private static final boolean REPLICATE_UPDATES_VIA_COPY = true;
	private static final boolean REPLICATE_REMOVALS = true;
	private static final boolean REPLICATE_ASYNC = true;
	private static final int ASYNC_REP_INTERVAL = 1000; */
	private static Map<String,CacheManagerAndHashLite> managers=new HashMap<String,CacheManagerAndHashLite>();
	
	//private net.sf.ehcache.Cache cache;
	private int hits;
	private int misses;
	private String cacheName;
	private CacheManager manager;
	private ClassLoader classLoader;

	
	public static void init(Config config,String[] cacheNames,Struct[] arguments) throws IOException {
		System.setProperty("net.sf.ehcache.enableShutdownHook", "true");
		Thread.currentThread().setContextClassLoader(config.getClassLoader());
		
		
		
		Resource dir = config.getConfigDir().getRealResource("ehcache"),hashDir;
		if(!dir.isDirectory())dir.createDirectory(true);
		String[] hashArgs=createHash(arguments);
		
		// create all xml
		HashMap<String,String> mapXML = new HashMap<String,String>();
		HashMap<String,CacheManagerAndHashLite> newManagers = new HashMap<String,CacheManagerAndHashLite>();
		for(int i=0;i<hashArgs.length;i++){
			if(mapXML.containsKey(hashArgs[i])) continue;
			
			hashDir=dir.getRealResource(hashArgs[i]);
			String xml=createXML(hashDir.getAbsolutePath(), cacheNames,arguments,hashArgs,hashArgs[i]);
			String hash=MD5.getDigestAsString(xml);
			
			CacheManagerAndHashLite manager= managers.remove(hashArgs[i]);
			if(manager!=null && manager.hash.equals(hash)) {
				newManagers.put(hashArgs[i], manager);
			}	
			else mapXML.put(hashArgs[i], xml);
		}
		
		// shutdown all existing managers that have changed
		synchronized(managers){
			Entry<String, CacheManagerAndHashLite> entry;
			Iterator<Entry<String, CacheManagerAndHashLite>> it = managers.entrySet().iterator();
			while(it.hasNext()){
				entry = it.next();
				if(entry.getKey().toString().startsWith(dir.getAbsolutePath())){
					entry.getValue().manager.shutdown();
				}
				else newManagers.put(entry.getKey(), entry.getValue());
				
			}
			managers=newManagers;
		}
		
		Iterator<Entry<String, String>> it = mapXML.entrySet().iterator();
		Entry<String, String> entry;
		String xml,hashArg,hash;
		while(it.hasNext()){
			entry=it.next();
			hashArg=entry.getKey();
			xml=entry.getValue();
			
			hashDir=dir.getRealResource(hashArg);
			if(!hashDir.isDirectory())hashDir.createDirectory(true);
			
			writeEHCacheXML(hashDir,xml);
			hash=MD5.getDigestAsString(xml);
			
			moveData(dir,hashArg,cacheNames,arguments);
			
			CacheManagerAndHashLite m = new CacheManagerAndHashLite(new CacheManager(new ByteArrayInputStream(xml.getBytes(CharsetUtil.UTF8))),hash);
			newManagers.put(hashDir.getAbsolutePath(), m);
		}
		
		clean(dir);
	}
	
	public static void flushAllCaches() {
		Map.Entry entry;
		String[] names;
		Iterator it = managers.entrySet().iterator();
		while(it.hasNext()){
			entry=(Entry) it.next();
			CacheManager manager=((CacheManagerAndHashLite)entry.getValue()).manager;
			names = manager.getCacheNames();
			for(int i=0;i<names.length;i++){
				manager.getCache(names[i]).flush();
			}
		}
	}
	
	private static void clean(Resource dir) {
		Resource[] dirs = dir.listResources();
		Resource[] children;
		
		for(int i=0;i<dirs.length;i++){
			if(dirs[i].isDirectory()){
				//print.out(dirs[i]+":"+pathes.contains(dirs[i].getAbsolutePath()));
				children=dirs[i].listResources();
				if(children!=null && children.length>1)continue;
				clean(children);
				dirs[i].delete();
			}
		}
	}

	private static void clean(Resource[] arr) {
		if(arr!=null)for(int i=0;i<arr.length;i++){
			if(arr[i].isDirectory()){
				clean(arr[i].listResources());
			}
			arr[i].delete();
		}
	}

	private static void moveData(Resource dir, String hash, String[] cacheNames, Struct[] arguments) {
		String h;
		Resource trg = dir.getRealResource(hash);
		deleteData(dir, cacheNames);
		for(int i=0;i<cacheNames.length;i++){
			h=createHash(arguments[i]);
			if(h.equals(hash)){
				moveData(dir,cacheNames[i],trg);
			}
		}
		
	}

	private static void moveData(Resource dir, String cacheName, Resource trg) {
		Resource[] dirs = dir.listResources();
		Resource index,data;
		// move 
		for(int i=0;i<dirs.length;i++){
			if(!dirs[i].equals(trg) && 
				dirs[i].isDirectory() && 
				(data=dirs[i].getRealResource(cacheName+".data")).exists() && 
				(index=dirs[i].getRealResource(cacheName+".index")).exists() ){
				
				try {
					index.moveTo(trg.getRealResource(cacheName+".index"));
					data.moveTo(trg.getRealResource(cacheName+".data"));
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	private static void deleteData(Resource dir, String[] cacheNames) {
		HashSet names=new HashSet();
		for(int i=0;i<cacheNames.length;i++){
			names.add(cacheNames[i]);
		}
		
		Resource[] dirs = dir.listResources();
		String name;
		// move 
		for(int i=0;i<dirs.length;i++){
			if(dirs[i].isDirectory()){
				Resource[] datas = dirs[i].listResources(new DataFiterLite());
				if(datas!=null) for(int y=0;y<datas.length;y++){
					name=datas[y].getName();
					name=name.substring(0,name.length()-5);
					if(!names.contains(name)){
						datas[y].delete();
						dirs[i].getRealResource(name+".index").delete();
					}
						
				}
			}
		}
	}

	private static void writeEHCacheXML(Resource hashDir, String xml) {
		ByteArrayInputStream is = new ByteArrayInputStream(xml.getBytes(CharsetUtil.UTF8));
		OutputStream os=null;
		try{
			os = hashDir.getRealResource("ehcache.xml").getOutputStream();
			Util.copy(is, os);
		}
		catch(IOException ioe){ioe.printStackTrace();}
		finally{
			Util.closeEL(os);
		}
	}

	private static String createHash(Struct args) {
		try {
			return MD5.getDigestAsString("off");	
		} catch (IOException e) {
			return "";
		}
	}
	private static String[] createHash(Struct[] arguments) {
		String[] hashes=new String[arguments.length];
		for(int i=0;i<arguments.length;i++){
			hashes[i]=createHash(arguments[i]);
		}
		return hashes;
	}

	private static String createXML(String path, String[] cacheNames,Struct[] arguments, String[] hashes, String hash) {
		
		//Struct global=null;
		for(int i=0;i<hashes.length;i++){
			if(hash.equals(hashes[i])){
				//global=arguments[i];
				break;
			}
		}
		
		
		StringBuffer xml=new StringBuffer();
		xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
		xml.append("<ehcache xsi:noNamespaceSchemaLocation=\"ehcache.xsd\">\n");
				
		// disk storage
		xml.append("<diskStore path=\"");
		xml.append(path);
		xml.append("\"/>\n");
		xml.append("<cacheManagerEventListenerFactory class=\"\" properties=\"\"/>\n");

		xml.append("<defaultCache \n");
		xml.append("   diskPersistent=\"true\"\n");
		xml.append("   eternal=\"false\"\n");
		xml.append("   maxElementsInMemory=\"10000\"\n");
		xml.append("   maxElementsOnDisk=\"10000000\"\n");
		xml.append("   memoryStoreEvictionPolicy=\"LRU\"\n");
		xml.append("   timeToIdleSeconds=\"86400\"\n");
		xml.append("   timeToLiveSeconds=\"86400\"\n");
		xml.append("   overflowToDisk=\"true\"\n");
		xml.append("   diskSpoolBufferSizeMB=\"30\"\n");
		xml.append("   diskExpiryThreadIntervalSeconds=\"3600\"\n");
		xml.append(" />\n");
		
		// cache
		for(int i=0;i<cacheNames.length && i<arguments.length;i++){
			if(hashes[i].equals(hash))createCacheXml(xml,cacheNames[i],arguments[i]);
		}
		
		xml.append("</ehcache>\n");
		return xml.toString();
	}
	

	private static void createCacheXml(StringBuffer xml, String cacheName, Struct arguments) {

		// disk Persistent
		boolean diskPersistent=toBooleanValue(arguments.get("diskpersistent",Boolean.FALSE),DISK_PERSISTENT);
		
		// eternal
		boolean eternal=toBooleanValue(arguments.get("eternal",Boolean.FALSE),ETERNAL);
		
		// max elements in memory
		int maxElementsInMemory=toIntValue(arguments.get("maxelementsinmemory",new Integer(MAX_ELEMENTS_IN_MEMEORY)),MAX_ELEMENTS_IN_MEMEORY);
		
		// max elements on disk
		int maxElementsOnDisk=toIntValue(arguments.get("maxelementsondisk",new Integer(MAX_ELEMENTS_ON_DISK)),MAX_ELEMENTS_ON_DISK);
		
		// memory eviction policy
		String strPolicy=toString(arguments.get("memoryevictionpolicy",MEMORY_EVICTION_POLICY),MEMORY_EVICTION_POLICY);
		String policy = "LRU";
		if("FIFO".equalsIgnoreCase(strPolicy)) policy="FIFO";
		else if("LFU".equalsIgnoreCase(strPolicy)) policy="LFU";
		
		// overflow to disk
		boolean overflowToDisk=toBooleanValue(arguments.get("overflowtodisk",Boolean.FALSE),OVERFLOW_TO_DISK);
		
		// time to idle seconds
		long timeToIdleSeconds=toLongValue(arguments.get("timeToIdleSeconds",new Long(TIME_TO_IDLE_SECONDS)),TIME_TO_IDLE_SECONDS);
		
		// time to live seconds
		long timeToLiveSeconds=toLongValue(arguments.get("timeToLiveSeconds",new Long(TIME_TO_LIVE_SECONDS)),TIME_TO_LIVE_SECONDS);
		
		xml.append("<cache name=\""+cacheName+"\"\n");
		xml.append("   diskPersistent=\""+diskPersistent+"\"\n");
		xml.append("   eternal=\""+eternal+"\"\n");
		xml.append("   maxElementsInMemory=\""+maxElementsInMemory+"\"\n");
		xml.append("   maxElementsOnDisk=\""+maxElementsOnDisk+"\"\n");
		xml.append("   memoryStoreEvictionPolicy=\""+policy+"\"\n");
		xml.append("   timeToIdleSeconds=\""+timeToIdleSeconds+"\"\n");
		xml.append("   timeToLiveSeconds=\""+timeToLiveSeconds+"\"\n");
		xml.append("   overflowToDisk=\""+overflowToDisk+"\"");
		xml.append(">\n");
		xml.append(" </cache>\n");	
	}



	public void init(String cacheName, Struct arguments) {
		CFMLEngine engine = CFMLEngineFactory.getInstance();
		init(engine.getThreadPageContext().getConfig(),cacheName, arguments);
		
	}
	
	@Override
	public void init(Config config,String cacheName, Struct arguments) {
		
		this.classLoader=config.getClassLoader();
		this.cacheName=cacheName;
		
		setClassLoader();
		Resource hashDir = config.getConfigDir().getRealResource("ehcache").getRealResource(createHash(arguments));
		manager =managers.get(hashDir.getAbsolutePath()).manager;
	} 

	private void setClassLoader() {
		if(classLoader!=Thread.currentThread().getContextClassLoader())
			Thread.currentThread().setContextClassLoader(classLoader);
	}

	protected net.sf.ehcache.Cache getCache() {
		setClassLoader();
		return manager.getCache(cacheName);
	}

	@Override
	public boolean remove(String key) {
		try	{
			return getCache().remove(key);
		}
		catch(Throwable t){
			return false;
		}
	}

	public CacheEntry getCacheEntry(String key) throws CacheException {
		try {
			misses++;
			Element el = getCache().get(key);
			if(el==null)throw new CacheException("there is no entry in cache with key ["+key+"]");
			hits++;
			misses--;
			return new EHCacheEntry(el);
		}
		catch(IllegalStateException ise) {
			throw new CacheException(ise.getMessage());
		}
		catch(net.sf.ehcache.CacheException ce) {
			throw new CacheException(ce.getMessage());
		}
	}

	public CacheEntry getCacheEntry(String key, CacheEntry defaultValue) {
		try {
			Element el = getCache().get(key);
			if(el!=null){
				hits++;
				return new EHCacheEntry(el);
			}
		}
		catch(Throwable t) {
			misses++;
		}
		return defaultValue;
	}

	@Override
	public Object getValue(String key) throws CacheException {
		try {
			misses++;
			Element el = getCache().get(key);
			if(el==null)throw new CacheException("there is no entry in cache with key ["+key+"]");
			misses--;
			hits++;
			return el.getObjectValue();
		}
		catch(IllegalStateException ise) {
			throw new CacheException(ise.getMessage());
		}
		catch(net.sf.ehcache.CacheException ce) {
			throw new CacheException(ce.getMessage());
		}
	}

	@Override
	public Object getValue(String key, Object defaultValue) {
		try {
			Element el = getCache().get(key);
			if(el!=null){
				hits++;
				return el.getObjectValue();
			}
		}
		catch(Throwable t) {
			misses++;
		}
		return defaultValue;
	}

	@Override
	public long hitCount() {
		return hits;
	}

	@Override
	public long missCount() {
		return misses;
	}

	public void remove() {
		setClassLoader();
		CacheManager singletonManager = CacheManager.getInstance();
		if(singletonManager.cacheExists(cacheName))
			singletonManager.removeCache(cacheName);
		
	}

	
	

	private static boolean toBooleanValue(Object o, boolean defaultValue) {
		if(o instanceof Boolean) return ((Boolean)o).booleanValue();
        else if(o instanceof Number) return (((Number)o).doubleValue())!=0;
        else if(o instanceof String) {
        	String str = o.toString().trim().toLowerCase();
            if(str.equals("yes") || str.equals("on") || str.equals("true")) return true;
            else if(str.equals("no") || str.equals("false") || str.equals("off")) return false;
        }
        return defaultValue;
	}

	private static String toString(Object o, String defaultValue) {
		if(o instanceof String)return o.toString();
		return defaultValue;
	}

	private static int toIntValue(Object o, int defaultValue) {
		if(o instanceof Number) return ((Number)o).intValue();
		try{
		return Integer.parseInt(o.toString());
		}
		catch(Throwable t){
			return defaultValue;
		}
	}

	private static long toLongValue(Object o, long defaultValue) {
		if(o instanceof Number) return ((Number)o).longValue();
		try{
			return Long.parseLong(o.toString());
		}
		catch(Throwable t){
			return defaultValue;
		}
	}
	

}
class CacheManagerAndHashLite {

		CacheManager manager;
		String hash;

		public CacheManagerAndHashLite(CacheManager manager, String hash) {
			this.manager=manager;
			this.hash=hash;
		}
		
	}

class DataFiterLite implements ResourceNameFilter {

	@Override
	public boolean accept(Resource parent, String name) {
		return name.endsWith(".data");
	}

}
