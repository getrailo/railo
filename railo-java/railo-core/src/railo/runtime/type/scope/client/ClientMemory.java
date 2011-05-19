package railo.runtime.type.scope.client;

import java.util.Map;

import railo.commons.io.log.Log;
import railo.runtime.PageContext;
import railo.runtime.type.Collection;
import railo.runtime.type.scope.ClientPlus;
import railo.runtime.type.scope.storage.MemoryScope;
import railo.runtime.type.scope.storage.StorageScopeMemory;

public class ClientMemory extends StorageScopeMemory implements ClientPlus,MemoryScope {

	private static final long serialVersionUID = 5032226519712666589L;


	/**
	 * Constructor of the class
	 * @param pc
	 * @param log 
	 * @param name
	 */
	private ClientMemory(PageContext pc, Log log) {
		super(pc,"client",SCOPE_CLIENT,log);
	}

	/**
	 * Constructor of the class, clone existing
	 * @param other
	 */
	private ClientMemory(ClientMemory other,boolean deepCopy,Map<Object, Object> done) {
		super(other,deepCopy,done);
	}
	/**
	 * load a new instance of the class
	 * @param pc
	 * @param log 
	 * @return
	 */
	public static ClientPlus getInstance(PageContext pc, Log log) {
		return new ClientMemory(pc,log);
	}
	
	
	/**
	 *
	 * @see railo.runtime.type.Collection#duplicate(boolean)
	 */
	public Collection duplicate(boolean deepCopy,Map<Object, Object> done) {
    	return new ClientMemory(this,deepCopy,done);
	}
}
