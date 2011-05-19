package railo.runtime.type.scope.client;

import java.util.Map;

import railo.commons.io.log.Log;
import railo.commons.io.res.Resource;
import railo.runtime.PageContext;
import railo.runtime.type.Collection;
import railo.runtime.type.Struct;
import railo.runtime.type.scope.ClientPlus;
import railo.runtime.type.scope.storage.StorageScopeFile;

public class ClientFile extends StorageScopeFile implements ClientPlus {

	
	/**
	 * Constructor of the class
	 * @param pc
	 * @param name
	 * @param sct
	 */
	private ClientFile(PageContext pc,Resource res,Struct sct) {
		super(pc,res,"client",SCOPE_CLIENT,sct);
	}
	

	/**
	 * Constructor of the class, clone existing
	 * @param other
	 */
	private ClientFile(ClientFile other,boolean deepCopy,Map<Object, Object> done) {
		super(other,deepCopy,done);
	}

	/**
	 * load new instance of the class
	 * @param name
	 * @param pc
	 * @param log 
	 * @return
	 */
	public static ClientPlus getInstance(String name, PageContext pc, Log log) {

		Resource res=_loadResource(pc.getConfig(),SCOPE_CLIENT,name,pc.getCFID());
		Struct data=_loadData(pc,res,log);
		return new ClientFile(pc,res,data);
	}
	
	/**
	 *
	 * @see railo.runtime.type.Collection#duplicate(boolean)
	 */
	public Collection duplicate(boolean deepCopy,Map<Object, Object> done) {
    	return new ClientFile(this,deepCopy,done);
	}
}
