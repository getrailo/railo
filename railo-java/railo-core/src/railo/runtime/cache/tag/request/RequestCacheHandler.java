package railo.runtime.cache.tag.request;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import railo.commons.io.cache.Cache;
import railo.commons.io.cache.CacheEntry;
import railo.runtime.PageContext;
import railo.runtime.cache.tag.CacheHandler;
import railo.runtime.cache.tag.CacheHandlerFilter;
import railo.runtime.exp.PageException;
import railo.runtime.query.QueryCacheEntry;
import railo.runtime.type.Query;

public class RequestCacheHandler implements CacheHandler {
	
	private static ThreadLocal<Map<String,Object>> data=new ThreadLocal<Map<String,Object>>() {
		@Override 
		protected Map<String,Object> initialValue() {
			return new HashMap<String, Object>();
		}
	};

	@Override
	public Object get(PageContext pc, String id) {
		return data.get().get(id);
	}

	@Override
	public boolean remove(PageContext pc, String id) {
		return data.get().remove(id)!=null;
	}

	@Override
	public void set(PageContext pc, String id,Object cachedwithin, Object value) {
		// cachedwithin is ignored in this cache, it should be "request"
		data.get().put(id,value);
	}

	@Override
	public void clear(PageContext pc) {
		data.get().clear();
	}

	@Override
	public void clear(PageContext pc, CacheHandlerFilter filter) {
		Iterator<Entry<String, Object>> it = data.get().entrySet().iterator();
		Entry<String, Object> e;
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
