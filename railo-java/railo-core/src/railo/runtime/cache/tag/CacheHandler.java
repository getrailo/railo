package railo.runtime.cache.tag;

import railo.runtime.PageContext;
import railo.runtime.exp.PageException;

public interface CacheHandler {
	public abstract Object get(PageContext pc, String id) throws PageException;
	public boolean remove(PageContext pc, String id);
	public void set(PageContext pc, String id, Object cachedwithin, Object value) throws PageException;
	public void clear(PageContext pc);
	public void clear(PageContext pc, CacheHandlerFilter filter);
	public void clean(PageContext pc);
	public int size(PageContext pc);
     
}
