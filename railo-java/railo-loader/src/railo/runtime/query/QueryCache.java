package railo.runtime.query;

import java.io.IOException;
import java.util.Date;

import railo.runtime.PageContext;
import railo.runtime.db.SQL;
import railo.runtime.type.Query;

/**
 * interface for a query cache
 */
public interface QueryCache {

    /**
     * clear expired queries from cache
     * @throws IOException 
     */
    public abstract void clearUnused(PageContext pc) throws IOException;

    /**
     * returns a Query from Query Cache or null if no match found
     * @param sql
     * @param datasource
     * @param username
     * @param password
     * @param cacheAfter
     * @return Query
     */
    public abstract Query getQuery(PageContext pc,SQL sql, String datasource, String username, String password, Date cacheAfter);
    
    
    /**
     * sets a Query to Cache
     * @param sql
     * @param datasource
     * @param username
     * @param password
     * @param value
     * @param cacheBefore
     */
    public abstract void set(PageContext pc,SQL sql, String datasource, String username,
            String password, Object value, Date cacheBefore);

    /**
     * clear the cache
     * @throws IOException 
     */
    public abstract void clear(PageContext pc);

	/**
	 * clear the cache
     * @param filter
	 */
	public abstract void clear(PageContext pc, QueryCacheFilter filter);

	/**
	 * removes query from cache
	 * @param sql
	 * @param datasource
	 * @param username
	 * @param password
	 * @throws IOException 
	 */
	public abstract void remove(PageContext pc,SQL sql, String datasource,String username, String password);

	public abstract Object get(PageContext pc,SQL sql, String datasource,String username, String password, Date cachedafter);

	public abstract int size(PageContext pc);
}