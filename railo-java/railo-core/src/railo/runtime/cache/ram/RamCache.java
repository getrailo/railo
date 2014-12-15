/**
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either 
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public 
 * License along with this library.  If not, see <http://www.gnu.org/licenses/>.
 * 
 **/
package railo.runtime.cache.ram;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import railo.print;
import railo.commons.io.SystemUtil;
import railo.commons.io.cache.CacheEntry;
import railo.commons.io.log.LogAndSource;
import railo.runtime.cache.CacheSupport;
import railo.runtime.config.Config;
import railo.runtime.op.Caster;
import railo.runtime.op.Constants;
import railo.runtime.type.Struct;

public class RamCache extends CacheSupport {

	public static final int DEFAULT_CONTROL_INTERVAL = 60;
	private Map<String, RamCacheEntry> entries= new ConcurrentHashMap<String, RamCacheEntry>();
	private long missCount;
	private int hitCount;
	
	private long idleTime;
	private long until;
	private int controlInterval=DEFAULT_CONTROL_INTERVAL*1000;
	private LogAndSource log;
	
	public RamCache(){
		new Controler(this).start();
	}

	public static void init(Config config,String[] cacheNames,Struct[] arguments)  {//print.ds();
		
	}
	
	public void init(Config config,String cacheName, Struct arguments) throws IOException {
		// until
		long until=Caster.toLongValue(arguments.get("timeToLiveSeconds",Constants.LONG_ZERO),Constants.LONG_ZERO)*1000;
		long idleTime=Caster.toLongValue(arguments.get("timeToIdleSeconds",Constants.LONG_ZERO),Constants.LONG_ZERO)*1000;
		Object ci = arguments.get("controlIntervall",null);
		if(ci==null)ci = arguments.get("controlInterval",null);
		int controlInterval=Caster.toIntValue(ci,DEFAULT_CONTROL_INTERVAL)*1000;
		init(config, until,idleTime,controlInterval);
	}

	public RamCache init(Config config, long until, long idleTime, int intervalInSeconds) {
		this.until=until;
		this.idleTime=idleTime;
		this.controlInterval=intervalInSeconds*1000;
		log = config.getApplicationLogger();
		return this;
	}
	
	@Override
	public boolean contains(String key) {
		return getQuiet(key,null)!=null;
	}

	
	

	public CacheEntry getQuiet(String key, CacheEntry defaultValue) {
		RamCacheEntry entry = entries.get(key);
		if(entry==null) {
			return defaultValue;
		}
		if(!valid(entry)) {
			entries.remove(key);
			return defaultValue;
		}
		return entry;
	}

	@Override
	public CacheEntry getCacheEntry(String key, CacheEntry defaultValue) {
		RamCacheEntry ce = (RamCacheEntry) getQuiet(key, null);
		if(ce!=null) {
			hitCount++;
			return ce.read();
		}
		missCount++;
		return defaultValue;
	}

	@Override
	public long hitCount() {
		return hitCount;
	}

	@Override
	public long missCount() {
		return missCount;
	}

	@Override
	public List<String> keys() {
		List<String> list=new ArrayList<String>();
		
		Iterator<Entry<String, RamCacheEntry>> it = entries.entrySet().iterator();
		RamCacheEntry entry;
		while(it.hasNext()){
			entry=it.next().getValue();
			if(valid(entry))list.add(entry.getKey());
		}
		return list;
	}

	public void put(String key, Object value, Long idleTime, Long until) {
		
		RamCacheEntry entry= entries.get(key);
		if(entry==null){
			entries.put(key, new RamCacheEntry(key,value,
					idleTime==null?this.idleTime:idleTime.longValue(),
					until==null?this.until:until.longValue()));
		}
		else
			entry.update(value);
	}

	public boolean remove(String key) {
		RamCacheEntry entry = entries.remove(key);
		if(entry==null) {
			return false;
		}
		return valid(entry);
		
	}
	
	public static  class Controler extends Thread {

		private RamCache ramCache;

		public Controler(RamCache ramCache) {
			this.ramCache=ramCache;
		}
		
		public void run(){
			while(true){
				SystemUtil.sleep(ramCache.controlInterval);
				try{
					_run();
				}
				catch(Throwable t){
					t.printStackTrace();
				}
			}
		}

		private void _run() {
			int before = ramCache.entries.size();
			RamCacheEntry[] values = ramCache.entries.values().toArray(new RamCacheEntry[ramCache.entries.size()]);
			for(int i=0;i<values.length;i++){
				if(!CacheSupport.valid(values[i])){
					ramCache.entries.remove(values[i].getKey());
				}
			}
			int after=ramCache.entries.size();
			int diff=before-after;
			if(diff<0)diff=0; // can happen when in meantime new elements was added
			ramCache.log.info("ram-cache", "removed "+diff+" element(s) from ram cache, the cache still has stored "+after+" element(s)");
		}
	}

	@Override
	public int clear() throws IOException {
		int size=entries.size();
		entries.clear();
		return size;
	}

}
