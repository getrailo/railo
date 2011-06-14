package railo.runtime.type.scope.session;

import java.util.Map;

import railo.commons.io.log.Log;
import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.type.Collection;
import railo.runtime.type.Struct;
import railo.runtime.type.StructImpl;
import railo.runtime.type.scope.SessionPlus;
import railo.runtime.type.scope.storage.StorageScopeDatasource;

public class SessionDatasource extends StorageScopeDatasource implements SessionPlus {
	
	private SessionDatasource(PageContext pc,String datasourceName, Struct sct) { 
		super(pc,datasourceName,"session",SCOPE_SESSION, sct);
	}

	/**
	 * Constructor of the class, clone existing
	 * @param other
	 */
	private SessionDatasource(StorageScopeDatasource other,boolean deepCopy,Map<Object, Object> done) {
		super(other,deepCopy,done);
	}
	
	/**
	 * load an new instance of the client datasource scope
	 * @param datasourceName 
	 * @param appName
	 * @param pc
	 * @param checkExpires 
	 * @return client datasource scope
	 * @throws PageException
	 */
	public static SessionPlus getInstance(String datasourceName, PageContext pc,Log log) throws PageException {
			
			Struct _sct = _loadData(pc, datasourceName,"session",SCOPE_SESSION, log,false);
			if(_sct==null) _sct=new StructImpl();
			
		return new SessionDatasource(pc,datasourceName,_sct);
	}
	
	public static SessionPlus getInstance(String datasourceName, PageContext pc, Log log,SessionPlus defaultValue) {
		try {
			return getInstance(datasourceName, pc,log);
		}
		catch (PageException e) {}
		return defaultValue;
	}
	public static boolean hasInstance(String datasourceName, PageContext pc) {
		try {
			return _loadData(pc, datasourceName,"session",SCOPE_SESSION, null,false)!=null;
		} 
		catch (PageException e) {
			return false;
		}
	}
	

	/**
	 *
	 * @see railo.runtime.type.Collection#duplicate(boolean)
	 */
	public Collection duplicate(boolean deepCopy,Map<Object, Object> done) {
    	return new SessionDatasource(this,deepCopy,done);
	}
}
