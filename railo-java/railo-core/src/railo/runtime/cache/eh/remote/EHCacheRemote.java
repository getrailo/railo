package railo.runtime.cache.eh.remote;

import java.io.IOException;
import java.net.URL;
import java.util.List;

import javax.xml.rpc.ServiceException;

import railo.commons.io.cache.CacheEntry;
import railo.loader.engine.CFMLEngineFactory;
import railo.runtime.cache.CacheSupport;
import railo.runtime.cache.eh.remote.rest.RESTClient;
import railo.runtime.cache.eh.remote.rest.sax.CacheConfiguration;
import railo.runtime.cache.eh.remote.soap.Element;
import railo.runtime.cache.eh.remote.soap.SoapClient;
import railo.runtime.config.Config;
import railo.runtime.config.ConfigWeb;
import railo.runtime.exp.PageException;
import railo.runtime.type.Struct;
import railo.runtime.type.util.KeyConstants;
import railo.runtime.util.Cast;

public class EHCacheRemote extends CacheSupport {

	private URL url;
	private String name;
	private RESTClient rest;
	private SoapClient soap;


	public EHCacheRemote() {	
	}
	
	public static void init(ConfigWeb config,String[] cacheNames,Struct[] arguments) {
		
	}

	public void init(Config config,String name, Struct arguments) throws IOException {
		Cast caster = CFMLEngineFactory.getInstance().getCastUtil();
		String strUrl=null;
		
		try {
			strUrl=caster.toString(arguments.get("url"));
			this.name=caster.toString(arguments.get("remoteCacheName"));
			
		} catch (PageException e) {
			throw new IOException(e.getMessage());
		}
		if(!strUrl.endsWith("/")){
			strUrl=strUrl+"/";
		}
		this.url=new URL(strUrl);
		
		
		
		
		this.rest=new RESTClient(new URL(url.toExternalForm()+"rest/"));
		this.soap=new SoapClient(new URL(url.toExternalForm()+"soap/EhcacheWebServiceEndpoint?wsdl"));
	}

	@Override
	public boolean contains(String key) {
		try {
			return rest.contains(name, key);
		} catch (IOException e) {
			return false;
		}
	}
	
	@Override
	public List keys() {
		try {
			return soap.getKeysWithExpiryCheck(name);
		} 
		catch (Throwable t) {
			throw new RuntimeException(t);
		}
	}

	public CacheEntry getQuiet(String key) throws IOException {
		try {
			return soap.getQuiet(name, key);
		} 
		catch (ServiceException e) {
			throw new IOException(e.getMessage());
		}
	}
	

	public CacheEntry getQuiet(String key,CacheEntry defaultValue) {
		try {
			return soap.getQuiet(name, key);
		} 
		catch (Throwable t) {
			return defaultValue;
		}
	}

	public CacheEntry getCacheEntry(String key) throws IOException {
		try {
			return soap.get(name, key);
		} 
		catch (ServiceException e) {
			throw new IOException(e.getMessage());
		}
	}

	public CacheEntry getCacheEntry(String key,CacheEntry defaultValue) {
		try {
			return soap.get(name, key);
		} 
		catch (Throwable t) {
			return defaultValue;
		}
	}

	

	public Struct getCustomInfo() {
		Struct info=super.getCustomInfo();
		try	{
			CacheConfiguration conf = rest.getMeta(name).getCacheConfiguration();
			
			info.setEL("disk_expiry_thread_interval", new Double(conf.getDiskExpiryThreadIntervalSeconds()));
			info.setEL("disk_spool_buffer_size", new Double(conf.getDiskSpoolBufferSize()));
			info.setEL("max_elements_in_memory", new Double(conf.getMaxElementsInMemory()));
			info.setEL("max_elements_on_disk", new Double(conf.getMaxElementsOnDisk()));
			info.setEL("time_to_idle", new Double(conf.getTimeToIdleSeconds()));
			info.setEL("time_to_live", new Double(conf.getTimeToLiveSeconds()));
			info.setEL(KeyConstants._name, conf.getName());
		}
		catch(Throwable t){
			//print.printST(t);
		}
		
		return info;
	}


	public long hitCount() {
		// TODO Auto-generated method stub
		return 0;
	}


	public long missCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	public void put(String key, Object value, Long idleTime, Long liveTime) {
		Boolean eternal = idleTime==null && liveTime==null?Boolean.TRUE:Boolean.FALSE;
		Integer idle = idleTime==null?null:new Integer((int)idleTime.longValue()/1000);
		Integer live = liveTime==null?null:new Integer((int)liveTime.longValue()/1000);
		try {
			Element el = new Element();
			el.setKey(key);
			// TODO make text/plain for string
			el.setMimeType("application/x-java-serialized-object");
			el.setValue(Converter.toBytes(value));
			el.setEternal(eternal);
			el.setTimeToIdleSeconds(idle);
			el.setTimeToLiveSeconds(live);
		
			soap.put(name,el);
		} catch (Throwable t) {
			throw new RuntimeException(t);
		}
		
	}

	@Override
	public boolean remove(String key) {
		try {
			return soap.remove(name, key);
		} 
		catch (Exception e) {
			return false;
		} 
	}


}
