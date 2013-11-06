package railo.runtime.cache.tag;

import java.io.IOException;

import railo.runtime.PageContext;

public interface CacheHandler {
	public abstract Object get(PageContext pc, CacheIdentifier id) throws IOException;
	public boolean remove(PageContext pc, CacheIdentifier id) throws IOException;
	public void set(PageContext pc, CacheIdentifier id, Object value) throws IOException;
	public void clear(PageContext pc) throws IOException;
	public void clean(PageContext pc) throws IOException;
	public int size(PageContext pc) throws IOException;
     
}
