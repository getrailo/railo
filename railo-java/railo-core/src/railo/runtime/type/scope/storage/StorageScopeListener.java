package railo.runtime.type.scope.storage;


public interface StorageScopeListener {
	public void doEnd(StorageScopeEngine engine,StorageScopeCleaner cleaner,String appName, String cfid);
}
