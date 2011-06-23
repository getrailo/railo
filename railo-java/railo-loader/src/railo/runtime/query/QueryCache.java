package railo.runtime.query;

import java.io.IOException;
import java.util.Date;

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
    public abstract void clearUnused() throws IOException;

    /**
     * returns a Query from Query Cache or null if no match found
     * @param sql
     * @param datasource
     * @param username
     * @param password
     * @param cacheAfter
     * @return Query
     */
    public abstract Query getQuery(SQL sql, String datasource, String username,
            String password, Date cacheAfter);

    /**
     * sets a Query to Cache
     * @param sql
     * @param datasource
     * @param username
     * @param password
     * @param value
     * @param cacheBefore
     */
    public abstract void set(SQL sql, String datasource, String username,
            String password, Object value, Date cacheBefore);

    /**
     * clear the cache
     * @throws IOException 
     */
    public abstract void clear();

	/**
	 * clear the cache
     * @param filter
	 */
	public abstract void clear(QueryCacheFilter filter);

	/**
	 * removes query from cache
	 * @param sql
	 * @param datasource
	 * @param username
	 * @param password
	 * @throws IOException 
	 */
	public abstract void remove(SQL sql, String datasource,String username, String password);

	public abstract Object get(SQL sql, String datasource,String username, String password, Date cachedafter);

	public abstract int size();
}