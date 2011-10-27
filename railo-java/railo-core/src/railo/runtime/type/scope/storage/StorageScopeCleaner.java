package railo.runtime.type.scope.storage;


public interface StorageScopeCleaner {
	
	/**
	 * initialize the Cleaner
	 * @param engine
	 */
	public void init(StorageScopeEngine engine);
	
	/**
	 * clean storage from expired Scopes
	 * @param config
	 */
	public void clean();
	
	public void info(String msg);
	public void error(String msg);
	public void error(Throwable t);
}
