package railo.runtime.config;

import railo.runtime.lock.LockManager;

/**
 * Web Context
 */
public interface ConfigWeb extends Config {

    /**
     * @return lockmanager
     */
    public abstract LockManager getLockManager();

	/**
	 * @return return if is allowed to define request timeout via URL
	 */
	public abstract boolean isAllowURLRequestTimeout();

	public abstract String getServerId();


}