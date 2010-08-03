package railo.runtime.engine;


/**
 * class to handle thread local PageContext, 
 * do use pagecontext in classes that have no method argument pagecontext
 */
public final class ThreadLocalWDDXResult {

	private static ThreadLocal resultThreadLocal=new ThreadLocal();

	/**
	 * register a pagecontext for he current thread
	 * @param pc PageContext to register
	 */
	public static void set(Object result) {
		resultThreadLocal.set(result);
	}

	public static Object get() {
		return resultThreadLocal.get();
	}

	public static void release() {
		resultThreadLocal.set(null);
	}

}