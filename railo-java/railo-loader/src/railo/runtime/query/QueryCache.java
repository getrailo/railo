package railo.runtime.query;

import java.util.Date;

import railo.runtime.db.SQL;
import railo.runtime.type.Query;

/**
 * interface for a query cache
 */
public interface QueryCache {

    /**
     * clear expired queries from cache
     */
    public abstract void clearUnused();

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
     */
    public abstract void clear();

	/**
	 * removes query from cache
	 * @param sql
	 * @param datasource
	 * @param username
	 * @param password
	 */
	public abstract void remove(SQL sql, String datasource,String username, String password);

	public abstract Object get(SQL sql, String datasource,String username, String password, Date cachedafter);

}