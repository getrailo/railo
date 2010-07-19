package railo.runtime.util.pool;

/**
 * Interface for a Pool
 */
public interface Pool {
	
	/**
	 * adds a new object to the pool, if object is already in the Pool, it will be overwritten
	 * @param key key for the Objects
	 * @param handler pool handler object
	 */
	public void set(Object key, PoolHandler handler);
	
	/**
	 * gets a Object from the pool
	 * @param key key for the Objects
	 * @return
	 */
	public PoolHandler get(Object key);

	/**
	 * checks if Object exists in Pool
	 * @param key key for the Objects
	 * @return object exists or not
	 */
	public boolean exists(Object key);
	
	/**
	 * remove a Object from the pool
	 * @param key key for the Objects
	 * @return
	 */
	public boolean remove(Object key);
	
}