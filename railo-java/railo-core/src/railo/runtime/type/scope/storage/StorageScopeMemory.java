package railo.runtime.type.scope.storage;

import railo.commons.io.log.Log;
import railo.runtime.PageContext;
import railo.runtime.type.StructImpl;
import railo.runtime.type.dt.DateTimeImpl;
import railo.runtime.type.scope.ScopeContext;

/**
 * client scope that not store it's data
 */
public abstract class StorageScopeMemory extends StorageScopeImpl implements MemoryScope {

	private static final long serialVersionUID = -6917303245683342065L;


	/**
	 * Constructor of the class
	 * @param pc
	 * @param log 
	 * @param name
	 */
	protected StorageScopeMemory(PageContext pc,String strType, int type, Log log) {
		super(
				new StructImpl(),
				new DateTimeImpl(pc.getConfig()),
				null,
				-1,1,strType,type);
		ScopeContext.info(log,"create new memory based "+strType+" scope for "+pc.getApplicationContext().getName()+"/"+pc.getCFID());
		
	}

	/**
	 * Constructor of the class, clone existing
	 * @param other
	 */
	protected StorageScopeMemory(StorageScopeMemory other,boolean deepCopy) {
		super(other,deepCopy);
	}
	
	
	

	
	@Override
	public String getStorageType() {
		return "Memory";
	}
}
