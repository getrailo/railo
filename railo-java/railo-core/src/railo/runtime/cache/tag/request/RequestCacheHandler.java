package railo.runtime.cache.tag.request;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import railo.runtime.PageContext;
import railo.runtime.cache.tag.CacheHandler;
import railo.runtime.cache.tag.CacheIdentifier;

public class RequestCacheHandler implements CacheHandler {
	
	private static ThreadLocal<Map<String,Object>> data=new ThreadLocal<Map<String,Object>>() {
		@Override 
		protected Map<String,Object> initialValue() {
			return new HashMap<String, Object>();
		}
	};

	@Override
	public Object get(PageContext pc, CacheIdentifier id) throws IOException {
		return data.get().get(id.id());
	}

	@Override
	public boolean remove(PageContext pc, CacheIdentifier id) throws IOException {
		return data.get().remove(id.id())!=null;
	}

	@Override
	public void set(PageContext pc, CacheIdentifier id, Object value) throws IOException {
		data.get().put(id.id(),value);
	}

	@Override
	public void clear(PageContext pc) throws IOException {
		data.get().clear();
	}

	@Override
	public int size(PageContext pc) throws IOException {
		return data.get().size();
	}

	@Override
	public void clean(PageContext pc) throws IOException {
		// not necessary
	}

}
