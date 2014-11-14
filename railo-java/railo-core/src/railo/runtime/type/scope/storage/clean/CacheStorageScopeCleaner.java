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
package railo.runtime.type.scope.storage.clean;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import railo.commons.io.cache.Cache;
import railo.commons.io.cache.CacheEntry;
import railo.commons.io.cache.CacheKeyFilter;
import railo.runtime.cache.CacheConnection;
import railo.runtime.config.ConfigWebImpl;
import railo.runtime.type.scope.storage.StorageScopeCache;
import railo.runtime.type.scope.storage.StorageScopeEngine;
import railo.runtime.type.scope.storage.StorageScopeListener;

public class CacheStorageScopeCleaner extends StorageScopeCleanerSupport {
	
	private Filter filter;

	public CacheStorageScopeCleaner(int type,StorageScopeListener listener) {
		super(type,listener,INTERVALL_MINUTE);
		//this.strType=VariableInterpreter.scopeInt2String(type);
		filter=new Filter(strType);
	}
	
	public void init(StorageScopeEngine engine) {
		super.init(engine);
		
	}

	protected void _clean() {
		ConfigWebImpl config = (ConfigWebImpl) engine.getFactory().getConfig();
		Map<String, CacheConnection> connections = config.getCacheConnections();
		CacheConnection cc;
		
		if(connections!=null) {
			Map.Entry<String, CacheConnection> entry;
			Iterator<Entry<String, CacheConnection>> it = connections.entrySet().iterator();
			while(it.hasNext()){
				entry=it.next();
				cc=entry.getValue();
				if(cc.isStorage()){
					try {
						clean(cc,config);
					} catch (IOException e) {
						error(e);
					}
				}
			}
		}
		
	}

	private void clean(CacheConnection cc, ConfigWebImpl config) throws IOException {
		Cache cache = cc.getInstance(config);
		int len=filter.length(),index;
		List<CacheEntry> entries = cache.entries(filter);
		CacheEntry ce;
		long expires;
		
		String key,appname,cfid;
		if(entries.size()>0){
			Iterator<CacheEntry> it = entries.iterator();
			while(it.hasNext()){
				ce=it.next();
				expires=ce.lastModified().getTime()+ce.idleTimeSpan()-StorageScopeCache.SAVE_EXPIRES_OFFSET;
				if(expires<=System.currentTimeMillis()) {
					key=ce.getKey().substring(len);
					index=key.indexOf(':');
					cfid=key.substring(0,index);
					appname=key.substring(index+1);
					
					if(listener!=null)listener.doEnd(engine, this,appname, cfid);
					info("remove "+strType+"/"+appname+"/"+cfid+" from cache "+cc.getName());
					engine.remove(type,appname,cfid);
					cache.remove(ce.getKey());
				}
			}
		}
		
		//engine.remove(type,appName,cfid);
		
		
		//return (Struct) cache.getValue(key,null);
	}

	public static class Filter implements CacheKeyFilter {
		private String startsWith;

		public Filter(String type){
			startsWith="railo-storage:"+type+":";
		}
		
		public String toPattern() {
			return startsWith+"*";
		}

		public boolean accept(String key) {
			return key.startsWith(startsWith);
		}

		public int length() {
			return startsWith.length();
		}
		
	}
}
