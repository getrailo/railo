package railo.runtime.engine;

import railo.runtime.PageSource;

/**
 * this class is just used to make the pagesource availble for old code in ra files
 */
public final class ThreadLocalPageSource {

	private static ThreadLocal<PageSource> local=new ThreadLocal<PageSource>();

	/**
	 * register a Config for he current thread
	 * @param config Config to register
	 */
	public static void register(PageSource ps) {
		local.set(ps);
	}

	/**
	 * returns Config registered for the current thread
	 * @return Config for the current thread or null 
	 */
	public static PageSource get() {
		return local.get();
	}
	
	/**
	 * release the pagecontext for the current thread
	 */
	public static void release() {
		local.set(null);
	}
}