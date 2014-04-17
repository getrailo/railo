package railo.runtime.cache.tag;

import railo.runtime.PageContext;
import railo.runtime.exp.PageException;

public interface CacheHandler {
	public String label() throws PageException;
	public CacheItem get(PageContext pc, String id) throws PageException;
	public boolean remove(PageContext pc, String id) throws PageException;
	public void set(PageContext pc, String id, Object cachedwithin, CacheItem value) throws PageException;
	public void clear(PageContext pc) throws PageException;
	public void clear(PageContext pc, CacheHandlerFilter filter) throws PageException;
	public void clean(PageContext pc) throws PageException;
	public int size(PageContext pc) throws PageException;
     
}
