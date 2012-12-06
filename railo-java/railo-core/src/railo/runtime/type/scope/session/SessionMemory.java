package railo.runtime.type.scope.session;

import railo.commons.io.log.Log;
import railo.commons.lang.types.RefBoolean;
import railo.runtime.PageContext;
import railo.runtime.type.Collection;
import railo.runtime.type.scope.Session;
import railo.runtime.type.scope.storage.MemoryScope;
import railo.runtime.type.scope.storage.StorageScopeMemory;

public class SessionMemory extends StorageScopeMemory implements Session,MemoryScope {
	
	private static final long serialVersionUID = 7703261878730061485L;


	/**
	 * Constructor of the class
	 * @param pc
	 * @param isNew 
	 * @param name
	 */
	protected SessionMemory(PageContext pc,Log log) {
		super(pc,"session",SCOPE_SESSION,log);
	}

	/**
	 * Constructor of the class, clone existing
	 * @param other
	 */
	protected SessionMemory(StorageScopeMemory other,boolean deepCopy) {
		super(other,deepCopy);
	}
	/**
	 * load a new instance of the class
	 * @param pc
	 * @param isNew 
	 * @return
	 */
	public static Session getInstance(PageContext pc, RefBoolean isNew, Log log) {
		isNew.setValue(true);
		return new SessionMemory(pc,log);
	}
	
	
	@Override
	public Collection duplicate(boolean deepCopy) {
    	return new SessionMemory(this,deepCopy);
	}
}
