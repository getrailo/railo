package railo.runtime.config;

import railo.commons.io.res.Resource;
import railo.runtime.CFMLFactory;
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

	public String getLabel();

	public abstract Resource getConfigServerDir();
	
	public CFMLFactory getFactory();
}