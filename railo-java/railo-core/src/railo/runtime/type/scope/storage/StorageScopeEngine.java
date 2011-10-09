package railo.runtime.type.scope.storage;

import railo.commons.io.log.Log;
import railo.runtime.CFMLFactoryImpl;

public class StorageScopeEngine {
	
	private StorageScopeCleaner[] cleaners;

	private CFMLFactoryImpl factory;

	private Log log;

	public StorageScopeEngine(CFMLFactoryImpl factory, Log log,StorageScopeCleaner[] cleaners){
		this.cleaners=cleaners;
		this.factory=factory;
		this.log=log;
		
		for(int i=0;i<cleaners.length;i++){
			cleaners[i].init(this);
		}
	}
	
	public void clean() {
		for(int i=0;i<cleaners.length;i++){
			cleaners[i].clean();
		}
	}
	
	/**
	 * @return the factory
	 */
	public CFMLFactoryImpl getFactory() {
		return factory;
	}

	/**
	 * @return the log
	 */
	public Log _getLog() {
		return log;
	}

	public void remove(int type, String appName, String cfid) {
		
		getFactory().getScopeContext().remove(type,appName,cfid);
	}
}
