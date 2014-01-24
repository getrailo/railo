package railo.runtime.cache.tag.request;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import railo.print;
import railo.runtime.PageContext;
import railo.runtime.cache.tag.CacheHandler;
import railo.runtime.cache.tag.CacheHandlerFilter;
import railo.runtime.cache.tag.CacheItem;

public class RequestCacheHandler implements CacheHandler {
	
	private static ThreadLocal<Map<String,CacheItem>> data=new ThreadLocal<Map<String,CacheItem>>() {
		@Override 
		protected Map<String,CacheItem> initialValue() {
			return new HashMap<String, CacheItem>();
		}
	};
	private final int cacheType;

	public RequestCacheHandler(int cacheType) {
		this.cacheType=cacheType;
	}

	@Override
	public CacheItem get(PageContext pc, String id) {
		return data.get().get(id);
	}

	@Override
	public boolean remove(PageContext pc, String id) {
		return data.get().remove(id)!=null;
	}

	@Override
	public void set(PageContext pc, String id,Object cachedwithin, CacheItem value) {
		// cachedwithin is ignored in this cache, it should be "request"
		data.get().put(id,value);
	}

	@Override
	public void clear(PageContext pc) {
		data.get().clear();
	}

	@Override
	public void clear(PageContext pc, CacheHandlerFilter filter) {
		Iterator<Entry<String, CacheItem>> it = data.get().entrySet().iterator();
		Entry<String, CacheItem> e;
		while(it.hasNext()){
			e = it.next();
			if(filter==null || filter.accept(e.getValue()))
				it.remove();
		}
	}

	@Override
	public int size(PageContext pc) {
		return data.get().size();
	}

	@Override
	public void clean(PageContext pc) {
		// not necessary
	}

}
