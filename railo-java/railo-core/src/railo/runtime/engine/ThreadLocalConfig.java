package railo.runtime.engine;

import railo.runtime.config.Config;

/**
 * class to handle thread local PageContext, 
 * do use pagecontext in classes that have no method argument pagecontext
 */
public final class ThreadLocalConfig {

	private static ThreadLocal cThreadLocal=new ThreadLocal();

	/**
	 * register a Config for he current thread
	 * @param config Config to register
	 */
	public static void register(Config config) {
		cThreadLocal.set(config);
	}

	/**
	 * returns Config registered for the current thread
	 * @return Config for the current thread or null 
	 */
	static Config get() {
		return (Config) cThreadLocal.get();
	}
	
	/**
	 * release the pagecontext for the current thread
	 */
	public static void release() {
		cThreadLocal.set(null);
	}
}