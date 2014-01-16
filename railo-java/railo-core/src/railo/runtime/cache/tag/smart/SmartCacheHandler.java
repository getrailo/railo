package railo.runtime.cache.tag.smart;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import railo.runtime.PageContext;
import railo.runtime.cache.tag.CacheHandler;
import railo.runtime.cache.tag.CacheHandlerFactory;
import railo.runtime.cache.tag.CacheHandlerFilter;
import railo.runtime.cache.tag.request.CacheEntry;
import railo.runtime.config.ConfigImpl;
import railo.runtime.exp.PageException;
import railo.runtime.type.Query;
import railo.runtime.type.QueryImpl;
import railo.runtime.type.Struct;
import railo.runtime.type.StructImpl;
import railo.runtime.type.dt.DateTimeImpl;
import railo.runtime.type.dt.TimeSpan;

public class SmartCacheHandler implements CacheHandler {

	private int cacheType; 
	public static Map<String,SmartEntry> entries=new LinkedHashMap<String,SmartEntry>();
	public static Map<String,TimeSpan> rules=new ConcurrentHashMap<String,TimeSpan>();
	public static Map<String,CE> cache=new ConcurrentHashMap<String,CE>();
	private static boolean running;
	private static long startTime;
	

	public SmartCacheHandler(int cacheType) {
		this.cacheType=cacheType;
	}

	@Override
	public Object get(PageContext pc, String id) throws PageException {
		if(!running) return null;
		
		CE ce = cache.get(id);
		//print.e("get("+id+"):"+(ce!=null));
		if(ce!=null) {
			if(ce.cacheUntil>System.currentTimeMillis()) {
				
				//print.e("return cached object");
				return ce.value;
			}
			cache.remove(id);
		}
		return null;
	}

	@Override
	public boolean remove(PageContext pc, String id) {
		return rules.remove(id)!=null;
	}

	@Override
	public void set(PageContext pc, String id, Object cachedwithin, Object value) throws PageException {
		if(!running) return;
		
		// add do cache if necessary
		TimeSpan ts = rules.get(id);
		//print.e("set("+id+"):"+(ts!=null));
		if(ts!=null) {
			cache.put(id, new CE(value,System.currentTimeMillis()+ts.getMillis()));
		}

		// store info
		if(cacheType==ConfigImpl.CACHE_DEFAULT_QUERY) {
			// get Raw type
			if(value instanceof CacheEntry) {
				CacheEntry ce=(CacheEntry) value;
				value=ce.query;
			}
			if(value instanceof Query) setQuery(pc,id,(Query)value);
			// TODO handle storedproc
		}
		// TODO else handle all other types
	}

	private void setQuery(PageContext pc, String id, Query qry) {
		SmartEntry se = new QuerySmartEntry(pc,qry,id,cacheType);
		entries.put(se.getId(),se);
		
	}

	@Override
	public void clear(PageContext pc) {
		cache.clear();
	}

	@Override
	public void clear(PageContext pc, CacheHandlerFilter filter) {
		print(pc,"SmartCacheHandler.clear:"+filter);
	}

	@Override
	public void clean(PageContext pc) {
		Iterator<Entry<String, CE>> it = cache.entrySet().iterator();
		Entry<String, CE> e;
		while(it.hasNext()){
			e = it.next();
			if(e.getValue().cacheUntil<System.currentTimeMillis()) {
				it.remove();
			}
		}
	}

	@Override
	public int size(PageContext pc) {
		return running?cache.size():0;
	}

	private void print(PageContext pc, String msg) {
		//print.e(CacheHandlerFactory.toStringCacheName(cacheType, null)+"->"+msg);
		((ConfigImpl)pc.getConfig()).getLog("application").error(CacheHandlerFactory.toStringCacheName(cacheType, null),msg);
	}

	public static void setRule(String id, TimeSpan timeSpan) { 
		//print.e("setRule("+id+"):"+timeSpan);
		
		// flush all cached elements for the old rule
		if(rules.containsKey(id)) {
			cache.remove(id);
		}		
		rules.put(id, timeSpan);
	}

	public static void clearRules() {
		rules.clear();
	}

	public static void removeRule(String id) { 
		rules.remove(id);
	}
	
	public static Query getRules() {
		Query qry=new QueryImpl(new String[]{"entryHash","timespan"},0,"rules");
		Iterator<Entry<String, TimeSpan>> it = rules.entrySet().iterator();
		int row;
		while(it.hasNext()){
			Entry<String, TimeSpan> e = it.next();
			row=qry.addRow();
			qry.setAtEL("entryHash", row, e.getKey());
			qry.setAtEL("timespan", row, e.getValue());
		}
		return qry;
	}
	
	public static Struct info() {
		Struct info=new StructImpl();
		info.setEL("entries", entries.size());
		info.setEL("rules", rules.size());
		info.setEL("cache", cache.size());
		info.setEL("starttime", running?new DateTimeImpl(startTime,true):"");// TODO muss von richtigem start kommen
		return info;
	}

	public static void start() {
		startTime = System.currentTimeMillis();
		running=true;
	}

	public static void stop() {
		running=false;
	}

	
	private static class CE {

		private Object value;
		private long cacheUntil;

		public CE(Object value, long cacheUntil) {
			this.value=value;
			this.cacheUntil=cacheUntil;
		}
		
	}

}
