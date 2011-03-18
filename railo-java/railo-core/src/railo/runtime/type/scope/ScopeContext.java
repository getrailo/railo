package railo.runtime.type.scope;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpSession;

import org.safehaus.uuid.UUIDGenerator;

import railo.commons.collections.HashTable;
import railo.commons.io.log.Log;
import railo.commons.io.log.LogAndSource;
import railo.commons.lang.ClassUtil;
import railo.commons.lang.ExceptionUtil;
import railo.commons.lang.SizeOf;
import railo.commons.lang.StringUtil;
import railo.commons.lang.types.RefBoolean;
import railo.runtime.CFMLFactoryImpl;
import railo.runtime.PageContext;
import railo.runtime.cache.CacheConnection;
import railo.runtime.config.Config;
import railo.runtime.config.ConfigImpl;
import railo.runtime.config.ConfigServer;
import railo.runtime.db.DataSource;
import railo.runtime.db.DataSourceImpl;
import railo.runtime.exp.ApplicationException;
import railo.runtime.exp.ExceptionHandler;
import railo.runtime.exp.ExpressionException;
import railo.runtime.exp.PageException;
import railo.runtime.exp.PageRuntimeException;
import railo.runtime.functions.cache.Util;
import railo.runtime.interpreter.VariableInterpreter;
import railo.runtime.listener.ApplicationListener;
import railo.runtime.op.Caster;
import railo.runtime.reflection.Reflector;
import railo.runtime.type.Scope;
import railo.runtime.type.Struct;
import railo.runtime.type.StructImpl;
import railo.runtime.type.scope.client.ClientCache;
import railo.runtime.type.scope.client.ClientCookie;
import railo.runtime.type.scope.client.ClientDatasource;
import railo.runtime.type.scope.client.ClientFile;
import railo.runtime.type.scope.client.ClientMemory;
import railo.runtime.type.scope.session.SessionCache;
import railo.runtime.type.scope.session.SessionCookie;
import railo.runtime.type.scope.session.SessionDatasource;
import railo.runtime.type.scope.session.SessionFile;
import railo.runtime.type.scope.session.SessionMemory;
import railo.runtime.type.scope.storage.MemoryScope;
import railo.runtime.type.scope.storage.StorageScope;
import railo.runtime.type.scope.storage.StorageScopeCleaner;
import railo.runtime.type.scope.storage.StorageScopeEngine;
import railo.runtime.type.scope.storage.clean.DatasourceStorageScopeCleaner;
import railo.runtime.type.scope.storage.clean.FileStorageScopeCleaner;
import railo.runtime.type.util.ArrayUtil;
import railo.runtime.type.wrap.MapAsStruct;
import railo.runtime.util.ApplicationContext;
import railo.runtime.util.ApplicationContextPro;

/**
 * Scope Context handle Apllication and Session Scopes
 */
public final class ScopeContext {

	private static final int MINUTE = 60*1000;
	private static final long CLIENT_MEMORY_TIMESPAN =  5*MINUTE;
	private static final long SESSION_MEMORY_TIMESPAN =  5*MINUTE;
	
	private static UUIDGenerator generator = UUIDGenerator.getInstance();
	private Map cfSessionContextes=new HashTable();
	private Map cfClientContextes=new HashTable();
	private Map applicationContextes=new HashTable();

	private int maxSessionTimeout=0;

	private static Cluster cluster;
	private static Server server=null;
	

	private StorageScopeEngine client;
	private StorageScopeEngine session;
	private CFMLFactoryImpl factory;
	private LogAndSource log;
	
	
	
	public ScopeContext(CFMLFactoryImpl factory) {
		this.factory=factory;
		
	}

	/**
	 * @return the log
	 */
	private Log getLog() {
		if(log==null) {
			this.log=((ConfigImpl)factory.getConfig()).getScopeLogger();
			
		}
		return log;
	}
	
	public void info(String msg) {info(getLog(), msg);}
	public void error(String msg) {error(getLog(),msg);}
	public void error(Throwable t) {error(getLog(), t);}
	
	public static void info(Log log,String msg) {
		if(log!=null)log.info("scope-context", msg);
	}
	

	public static void error(Log log,String msg) {
		if(log!=null)log.error("scope-context", msg);
	}
	
	public static void error(Log log,Throwable t) {
		if(log!=null)log.error("scope-context",ExceptionUtil.getStacktrace(t, true));
	}
	

	/**
	 * return a map matching key from given map
	 * @param parent
	 * @param key key of the map
	 * @return matching map, if no map exist it willbe one created
	 */
	private Map getSubMap(Map parent, String key) {
			
		Map context=(Map) parent.get(key);
		if(context!=null) return context;
		
		context = new HashTable();
		parent.put(key,context);
		return context;
		
	}
	
	/**
	 * return the server Scope for this context
	 * @param pc
	 * @return server scope
	 */
	public static Server getServerScope(PageContext pc) {
	    if(server==null) {
	    	server=new ServerImpl(pc);
	    }
		return server;
	}
	
	/* *
	 * Returns the current Cluster Scope, if there is no current Cluster Scope, this method returns null. 
	 * @param pc
	 * @param create
	 * @return
	 * @throws SecurityException
	 * /
	public static Cluster getClusterScope() {
	    return cluster;
	}*/

	/**
	 * Returns the current Cluster Scope, if there is no current Cluster Scope and create is true, returns a new Cluster Scope.
	 * If create is false and the request has no valid Cluster Scope, this method returns null. 
	 * @param pc
	 * @param create
	 * @return
	 * @throws PageException 
	 */
	public static Cluster getClusterScope(ConfigServer cs, boolean create) throws PageException {
	    if(cluster==null && create) {
	    	try {
	    		if(Reflector.isInstaneOf(cs.getClusterClass(), Cluster.class)){
	    			cluster=(Cluster) ClassUtil.loadInstance(
							cs.getClusterClass(),
							ArrayUtil.OBJECT_EMPTY
							);
	    			cluster.init(cs);
	    		}
	    		else if(Reflector.isInstaneOf(cs.getClusterClass(), ClusterRemote.class)){
	    			ClusterRemote cb=(ClusterRemote) ClassUtil.loadInstance(
							cs.getClusterClass(),
							ArrayUtil.OBJECT_EMPTY
							);
		    		
	    			cluster=new ClusterWrap(cs,cb);
		    		//cluster.init(cs);
	    		}
			} 
	    	catch (Exception e) {
				throw Caster.toPageException(e);
			} 
	    }
		return cluster;
	}
	
	public static void clearClusterScope() {
		cluster=null;
	}
	
	
	public ClientPlus getClientScope(PageContext pc) throws PageException {
		ClientPlus client=null;
		ApplicationContextPro appContext = (ApplicationContextPro) pc.getApplicationContext(); 
		// get Context
			Map context=getSubMap(cfClientContextes,appContext.getName());
			
		// get Client
			boolean isMemory=false;
			String storage = appContext.getClientstorage();
			if(StringUtil.isEmpty(storage,true)){
				storage="file";
			}
			else if("ram".equalsIgnoreCase(storage)) {
				storage="memory";
				isMemory=true;
			}
			else if("registry".equalsIgnoreCase(storage)) {
				storage="file";
			}
			else {
				storage=storage.toLowerCase();
				if("memory".equals(storage))isMemory=true;
			}
			
			final boolean doMemory=isMemory || !appContext.getClientCluster();
			client=doMemory?(ClientPlus) context.get(pc.getCFID()):null;
			if(client==null || client.isExpired() || !client.getStorage().equalsIgnoreCase(storage)) {
				if("file".equals(storage)){
					client=ClientFile.getInstance(appContext.getName(),pc,getLog());
				}
				else if("cookie".equals(storage))
					client=ClientCookie.getInstance(appContext.getName(),pc,getLog());
				else if("memory".equals(storage)){
					client=ClientMemory.getInstance(pc,getLog());
				}
				else{
					DataSource ds = ((ConfigImpl)pc.getConfig()).getDataSource(storage,null);
					if(ds!=null)client=ClientDatasource.getInstance(storage,pc,getLog());
					else client=ClientCache.getInstance(storage,appContext.getName(),pc,getLog(),null);
					
					if(client==null){
						// datasource not enabled for storage
						if(ds!=null)
							throw new ApplicationException("datasource ["+storage+"] is not enabled to be used as session/client storage, you have to enable it in the railo administrator.");
						
						CacheConnection cc = Util.getCacheConnection(pc.getConfig(),storage,null);
						if(cc!=null) 
							throw new ApplicationException("cache ["+storage+"] is not enabled to be used  as a session/client storage, you have to enable it in the railo administrator.");
						
						throw new ApplicationException("there is no cache or datasource with name ["+storage+"] defined.");
					}
					
				}
				client.setStorage(storage);
				if(doMemory)context.put(pc.getCFID(),client);
			}
			else
				getLog().info("scope-context", "use existing client scope for "+appContext.getName()+"/"+pc.getCFID()+" from storage "+storage);
			
			client.touchBeforeRequest(pc);
			return client;
	}
	
	public ClientPlus getClientScopeEL(PageContext pc) {
		try {
			return getClientScope(pc);
		} catch (PageException pe) {
			throw new PageRuntimeException(pe);
		}
	}
	
	/*public ClientPlus getClientScopeEL(PageContext pc) {
		ClientPlus client=null;
		ApplicationContext appContext = pc.getApplicationContext(); 
		// get Context
			Map context=getSubMap(cfClientContextes,appContext.getName());
			
		// get Client
			String storage = appContext.getClientstorage();
			if(!StringUtil.isEmpty(storage))storage=storage.toLowerCase();
			else storage="";
			
			client=(ClientPlus) context.get(pc.getCFID());
			if(client==null || client.isExpired() || !client.getStorageType().equalsIgnoreCase(storage)) {
				if(StringUtil.isEmpty(storage) || "file".equals(storage) || "registry".equals(storage)){
					storage="file";
					client=ClientFile.getInstance(appContext.getName(),pc,getLog());
				}
				else if("cookie".equals(storage))
					client=ClientCookie.getInstance(appContext.getName(),pc,getLog());
				else if("memory".equals(storage) || "ram".equals(storage)){
					//storage="ram";
					client=ClientMemory.getInstance(pc,getLog());
				}
				else{
					DataSource ds = ((ConfigImpl)pc.getConfig()).getDataSource(storage,null);
					if(ds!=null)client=ClientDatasource.getInstanceEL(storage,pc,getLog());
					else client=ClientCache.getInstanceEL(storage,appContext.getName(),pc,getLog());

				}
				client.setStorage(storage);
				context.put(pc.getCFID(),client);
			}
			else
				getLog().info("scope-context", "use existing client scope for "+appContext.getName()+"/"+pc.getCFID()+" from storage "+storage);
			
			
			client.initialize(pc);
			return client;
	}*/



	/**
	 * return the session count of all application contextes
	 * @return
	 */
	public int getSessionCount(PageContext pc) {
		if(((ApplicationContextPro)pc.getApplicationContext()).getSessionType()==Config.SESSION_TYPE_J2EE) return 0;
		
		Iterator it = cfSessionContextes.entrySet().iterator();
		Map.Entry entry;
		int count=0;
		while(it.hasNext()) {
			entry=(Entry) it.next();
			count+=getSessionCount(((Map)entry.getValue()));
		}
		return count;
	}
	
	/**
	 * return the session count of this application context
	 * @return
	 */
	public int getAppContextSessionCount(PageContext pc) {
		ApplicationContextPro appContext = (ApplicationContextPro) pc.getApplicationContext(); 
		if(appContext.getSessionType()==Config.SESSION_TYPE_J2EE) return 0;
		Map context=getSubMap(cfSessionContextes,appContext.getName());
		return getSessionCount(context);
	}
	
	private int getSessionCount(Map context) {
		Iterator it = context.entrySet().iterator();
		Map.Entry entry;
		int count=0;
		Session s;
		while(it.hasNext()) {
			entry=(Entry) it.next();
			s=(Session)entry.getValue();
			if(!s.isExpired())
				count++;
		}
		return count;
	}
	


	/**
	 * return all session context of this application context
	 * @param pc
	 * @return
	 */
	public Struct getAllSessionScopes(PageContext pc) {
		return getAllSessionScopes(pc, pc.getApplicationContext().getName());
	}
	
	public Struct getAllApplicationScopes() {
		Struct trg=new StructImpl();
		StructImpl.copy(MapAsStruct.toStruct(applicationContextes, true), trg, false);
		return trg;
	}
	
	public Struct getAllCFSessionScopes() {
		Struct trg=new StructImpl();
		StructImpl.copy(MapAsStruct.toStruct(this.cfSessionContextes, true), trg, false);
		return trg;
	}
	
	/**
	 * return the size in bytes of all session contextes
	 * @return size in bytes
	 * @throws ExpressionException 
	 */
	public long getScopesSize(int scope) throws ExpressionException {
		if(scope==Scope.SCOPE_APPLICATION)return SizeOf.size(applicationContextes);
		if(scope==Scope.SCOPE_CLUSTER)return SizeOf.size(cluster);
		if(scope==Scope.SCOPE_SERVER)return SizeOf.size(server);
		if(scope==Scope.SCOPE_SESSION)return SizeOf.size(this.cfSessionContextes);
		if(scope==Scope.SCOPE_CLIENT)return SizeOf.size(this.cfClientContextes);
		
		throw new ExpressionException("can only return information of scope that are not request dependent");
	}
	
	/**
	 * get all session contexts of given applicaton name
	 * @param pc
	 * @param appName
	 * @return
	 */
	public Struct getAllSessionScopes(PageContext pc, String appName) {
        if(((ApplicationContextPro)pc.getApplicationContext()).getSessionType()==Config.SESSION_TYPE_J2EE)return new StructImpl();
		return getAllSessionScopes(getSubMap(cfSessionContextes,appName),appName);
	}
	
	private Struct getAllSessionScopes(Map context, String appName) {
		Iterator it = context.entrySet().iterator();
		Map.Entry entry;
		Struct sct=new StructImpl();
		Session s;
		while(it.hasNext()) {
			entry=(Entry) it.next();
			s=(Session)entry.getValue();
			if(!s.isExpired())
				sct.setEL(appName+"_"+entry.getKey()+"_0", s);
		}
		return sct;
	}

	/**
	 * return the session Scope for this context (cfid,cftoken,contextname)
	 * @param pc PageContext 
	 * @return session matching the context
	 * @throws PageException
	 */
	public SessionPlus getSessionScope(PageContext pc,RefBoolean isNew) throws PageException {
        if(((ApplicationContextPro)pc.getApplicationContext()).getSessionType()==Config.SESSION_TYPE_CFML)return getCFSessionScope(pc,isNew);
		return getJSessionScope(pc,isNew);
	}
	
	public boolean hasExistingSessionScope(PageContext pc) {
        if(((ApplicationContextPro)pc.getApplicationContext()).getSessionType()==Config.SESSION_TYPE_CFML)return hasExistingCFSessionScope(pc);
		return hasExistingJSessionScope(pc);
	}
	
	private synchronized boolean hasExistingCFSessionScopeX(PageContext pc) {
		ApplicationContextPro ac=(ApplicationContextPro) pc.getApplicationContext();
		String storage = ac.getSessionstorage();
		
		Map context=getSubMap(cfSessionContextes,ac.getName());
		SessionPlus session=(SessionPlus) context.get(pc.getCFID());
		if(!(session instanceof StorageScope)) return false;
		
		
		
		return ((StorageScope)session).getStorage().equalsIgnoreCase(storage);
	}
	
	private synchronized boolean hasExistingJSessionScope(PageContext pc) {
		HttpSession httpSession=pc.getSession();
        if(httpSession==null) return false;
        
        Session session=(Session) httpSession.getAttribute(pc.getApplicationContext().getName());
        return session instanceof JSession;
	}
	
	
	private boolean hasExistingCFSessionScope(PageContext pc) {
		
		ApplicationContextPro appContext = (ApplicationContextPro) pc.getApplicationContext(); 
		// get Context
			Map context=getSubMap(cfSessionContextes,appContext.getName());
			
		// get Session
			String storage = appContext.getSessionstorage();
			if(StringUtil.isEmpty(storage,true))storage="memory";
			else if("ram".equalsIgnoreCase(storage)) storage="memory";
			else if("registry".equalsIgnoreCase(storage)) storage="file";
			else storage=storage.toLowerCase();
			
			
			
			SessionPlus session=(SessionPlus) context.get(pc.getCFID());
			
			if(!(session instanceof StorageScope) || session.isExpired() || !((StorageScope)session).getStorage().equalsIgnoreCase(storage)) {
				
				if("memory".equals(storage)) return false;
				else if("file".equals(storage))
					return SessionFile.hasInstance(appContext.getName(),pc);
				else if("cookie".equals(storage))
					return SessionCookie.hasInstance(appContext.getName(),pc);
				else {
					DataSourceImpl ds = (DataSourceImpl) ((ConfigImpl)pc.getConfig()).getDataSource(storage,null);
					if(ds!=null && ds.isStorage()){
						if(SessionDatasource.hasInstance(storage,pc)) return true;
					}
					return  SessionCache.hasInstance(storage,appContext.getName(),pc);
				}
			}
			else 
				return true;
	}
	
	
	/**
	 * return cf session scope
	 * @param pc PageContext
	 * @param checkExpires 
	 * @param listener 
	 * @return cf session matching the context
	 * @throws PageException 
	 */
	private synchronized SessionPlus getCFSessionScope(PageContext pc, RefBoolean isNew) throws PageException {
		
		ApplicationContextPro appContext = (ApplicationContextPro) pc.getApplicationContext(); 
		// get Context
			Map context=getSubMap(cfSessionContextes,appContext.getName());
			
		// get Session
			boolean isMemory=false;
			String storage = appContext.getSessionstorage();
			if(StringUtil.isEmpty(storage,true)){
				storage="memory";
				isMemory=true;
			}
			else if("ram".equalsIgnoreCase(storage)) {
				storage="memory";
				isMemory=true;
			}
			else if("registry".equalsIgnoreCase(storage)) {
				storage="file";
			}
			else {
				storage=storage.toLowerCase();
				if("memory".equals(storage))isMemory=true;
			}
			
			final boolean doMemory=isMemory || !appContext.getSessionCluster();
			SessionPlus session=doMemory?appContext.getSessionCluster()?null:(SessionPlus) context.get(pc.getCFID()):null;
			if(!(session instanceof StorageScope) || session.isExpired() || !((StorageScope)session).getStorage().equalsIgnoreCase(storage)) {
				
				if(isMemory){
					session=SessionMemory.getInstance(pc,isNew,getLog());
				}
				else if("file".equals(storage)){
					session=SessionFile.getInstance(appContext.getName(),pc,getLog());
				}
				else if("cookie".equals(storage))
					session=SessionCookie.getInstance(appContext.getName(),pc,getLog());
				else{
					DataSourceImpl ds = (DataSourceImpl) ((ConfigImpl)pc.getConfig()).getDataSource(storage,null);
					if(ds!=null && ds.isStorage())session=SessionDatasource.getInstance(storage,pc,getLog(),null);
					else session=SessionCache.getInstance(storage,appContext.getName(),pc,getLog(),null);
					
					if(session==null){
						// datasource not enabled for storage
						if(ds!=null)
							throw new ApplicationException("datasource ["+storage+"] is not enabled to be used as session/client storage, you have to enable it in the railo administrator.");
						
						CacheConnection cc = Util.getCacheConnection(pc.getConfig(),storage,null);
						if(cc!=null) 
							throw new ApplicationException("cache ["+storage+"] is not enabled to be used  as a session/client storage, you have to enable it in the railo administrator.");
						
						throw new ApplicationException("there is no cache or datasource with name ["+storage+"] defined.");
					}
				}
				((StorageScope)session).setStorage(storage);
				if(doMemory)context.put(pc.getCFID(),session);
				isNew.setValue(true);
			}
			else 
				getLog().info("scope-context", "use existing session scope for "+appContext.getName()+"/"+pc.getCFID()+" from storage "+storage);
			
			session.touchBeforeRequest(pc);
			return session;
	}
	

	public boolean remove(int type, String appName, String cfid) {
		Map contextes = type==Scope.SCOPE_CLIENT?cfClientContextes:cfSessionContextes;
		Map context=getSubMap(contextes,appName);
		Object res = context.remove(cfid);
		getLog().info("scope-context", "remove "+VariableInterpreter.scopeInt2String(type)+" scope "+appName+"/"+cfid+" from memory");
		
		return res!=null;
	}

	/**
	 * return j session scope
	 * @param pc PageContext
	 * @param listener 
	 * @return j session matching the context
	 * @throws PageException
	 */
	private synchronized SessionPlus getJSessionScope(PageContext pc, RefBoolean isNew) {
        HttpSession httpSession=pc.getSession();
        ApplicationContext appContext = pc.getApplicationContext(); 
        Object session=null;// this is from type object, because it is possible that httpSession return object from prior restart
		
        int s=(int) appContext.getSessionTimeout().getSeconds();
        if(maxSessionTimeout<s)maxSessionTimeout=s;
        
        if(httpSession!=null) {
        	httpSession.setMaxInactiveInterval(maxSessionTimeout);
        	session= httpSession.getAttribute(appContext.getName());
        }
     // call of listeners
        else {
        	Map context=getSubMap(cfSessionContextes,appContext.getName());
        	session=context.get(pc.getCFID());
        }
        
        JSession jSession=null;
		if(session instanceof JSession) {
			jSession=(JSession) session;
            try {
                if(jSession.isExpired()) {
                	jSession.touch();
                }
                info(getLog(), "use existing JSession for "+appContext.getName()+"/"+pc.getCFID());
                
            }
            catch(ClassCastException cce) {
            	error(getLog(), cce);
                jSession=new JSession();
                httpSession.setAttribute(appContext.getName(),jSession);
				isNew.setValue(true);
            }
		}
		else {
			info(getLog(), "create new JSession for "+appContext.getName()+"/"+pc.getCFID());
			jSession=new JSession();
		    httpSession.setAttribute(appContext.getName(),jSession);
			isNew.setValue(true);
			Map context=getSubMap(cfSessionContextes,appContext.getName());
			context.put(pc.getCFID(),jSession);
		}
		jSession.touchBeforeRequest(pc);
		return jSession;    
	}

	/**
	 * return the application Scope for this context (cfid,cftoken,contextname)
	 * @param pc PageContext 
	 * @param listener 
	 * @param isNew 
	 * @return session matching the context
	 * @throws PageException 
	 */
	public synchronized Application getApplicationScope(PageContext pc, RefBoolean isNew) {
		ApplicationContext appContext = pc.getApplicationContext(); 
		// getApplication Scope from Context
			ApplicationImpl application;
			Object objApp=applicationContextes.get(appContext.getName());
			if(objApp!=null) {
			    application=(ApplicationImpl)objApp;
			    if(application.isExpired()) {
			    	application.release();	
			    	isNew.setValue(true);
			    }
			}
			else {
				application=new ApplicationImpl();
				applicationContextes.put(appContext.getName(),application);	
		    	isNew.setValue(true);
			}
			application.touchBeforeRequest(pc);
			//if(newApplication)listener.onApplicationStart(pc);
			
			return application;
	}
	
	public void removeApplicationScope(PageContext pc) {
		applicationContextes.remove(pc.getApplicationContext().getName());
	}


    /**
     * remove all unused scope objects
     */
    public void clearUnused() {
    	
    	Log log=getLog();
    	try{
    	// create cleaner engine for session/client scope
    	if(session==null)session=new StorageScopeEngine(factory,log,new StorageScopeCleaner[]{
			new FileStorageScopeCleaner(Scope.SCOPE_SESSION, null)//new SessionEndListener())
			,new DatasourceStorageScopeCleaner(Scope.SCOPE_SESSION, null)//new SessionEndListener())
			//,new CacheStorageScopeCleaner(Scope.SCOPE_SESSION, new SessionEndListener())
    	});
    	if(client==null)client=new StorageScopeEngine(factory,log,new StorageScopeCleaner[]{
    			new FileStorageScopeCleaner(Scope.SCOPE_CLIENT, null)
    			,new DatasourceStorageScopeCleaner(Scope.SCOPE_CLIENT, null)
    			//,new CacheStorageScopeCleaner(Scope.SCOPE_CLIENT, null) //Cache storage need no control, if there is no listener
    	});

    	
    	
    	// store session/client scope and remove from memory
        storeUnusedStorageScope(factory, Scope.SCOPE_CLIENT);
        storeUnusedStorageScope(factory, Scope.SCOPE_SESSION);
        
        // remove unused memory based client/session scope (invoke onSessonEnd)
    	clearUnusedMemoryScope(factory, Scope.SCOPE_CLIENT);
    	clearUnusedMemoryScope(factory, Scope.SCOPE_SESSION);
    	
    	
    	// session must be executed first, because session creates a reference from client scope
    	session.clean();
    	client.clean();
    	
        // clean all unused application scopes
        clearUnusedApplications(factory);
    	}
    	catch(Throwable t){
    		error(t);
    	}
    }
    
    /**
     * remove all scope objects
     */
    public void clear() {
    	try{
	    	Scope scope;
	    	Map.Entry entry,e;
	    	Map context;
	    	
	    	// release all session scopes
	    	Iterator it = cfSessionContextes.entrySet().iterator(),itt;
	    	while(it.hasNext()){
	    		entry=(Entry) it.next();
	    		context=(Map) entry.getValue();
	    		itt=context.entrySet().iterator();
	    		while(itt.hasNext()){
	    			e=(Entry) itt.next();
	    			scope=(Scope) e.getValue();
	    			//print.o("release-session:"+entry.getKey()+"/"+e.getKey());
	    			scope.release();
	    		}
	    	}
	        cfSessionContextes.clear();
	    	
	    	// release all application scopes
	    	it = applicationContextes.entrySet().iterator();
	    	while(it.hasNext()){
	    		entry=(Entry) it.next();
	    		scope=(Scope) entry.getValue();
	    		//print.o("release-application:"+entry.getKey());
	    		scope.release();
	    	}
	        applicationContextes.clear();
	    	
	    	// release server scope
	    	if(server!=null){
	    		server.release();
	    		server=null;
	    	}
    	
    	}
    	catch(Throwable t){t.printStackTrace();}
    }

    

	private void storeUnusedStorageScope(CFMLFactoryImpl cfmlFactory, int type) {
        Map contextes=type==Scope.SCOPE_CLIENT?cfClientContextes:cfSessionContextes;
		long timespan = type==Scope.SCOPE_CLIENT?CLIENT_MEMORY_TIMESPAN:SESSION_MEMORY_TIMESPAN;
		String strType=VariableInterpreter.scopeInt2String(type);
		
		if(contextes.size()==0)return;
		long now = System.currentTimeMillis();
		Object[] arrContextes= contextes.keySet().toArray();
		Object applicationName,cfid,o;
		
		for(int i=0;i<arrContextes.length;i++) {
			
			applicationName=arrContextes[i];
            Map fhm=(Map) contextes.get(applicationName);
            if(fhm.size()>0){
    			Object[] arrClients= fhm.keySet().toArray();
                int count=arrClients.length;
                for(int y=0;y<arrClients.length;y++) {
                	cfid=arrClients[y];
                	o=fhm.get(cfid);
                	if(!(o instanceof StorageScope)) continue;
    				StorageScope scope=(StorageScope)o;
    				if(scope.lastVisit()+timespan<now && !(scope instanceof MemoryScope)) {
    					getLog().info("scope-context", "remove from memory "+strType+" scope for "+applicationName+"/"+cfid+" from storage "+scope.getStorage());
    					
        				//if(scope instanceof StorageScope)((StorageScope)scope).store(cfmlFactory.getConfig());
    					fhm.remove(arrClients[y]);
        				count--;
    				}
    			}
                if(count==0)contextes.remove(arrContextes[i]);
            }
		}
	}
    
	/**
	 * @param cfmlFactory 
	 * 
	 */
	private void clearUnusedMemoryScope(CFMLFactoryImpl cfmlFactory, int type) {
        Map contextes=type==Scope.SCOPE_CLIENT?cfClientContextes:cfSessionContextes;
		if(contextes.size()==0)return;
		
		
		
        Object[] arrContextes= contextes.keySet().toArray();
		ApplicationListener listener = cfmlFactory.getConfig().getApplicationListener();
		Object applicationName,cfid,o;
		//long now = System.currentTimeMillis();
		
		for(int i=0;i<arrContextes.length;i++) {
			applicationName=arrContextes[i];
            Map fhm=(Map) contextes.get(applicationName);

			if(fhm.size()>0){
    			Object[] cfids= fhm.keySet().toArray();
                int count=cfids.length;
                for(int y=0;y<cfids.length;y++) {
                	cfid=cfids[y];
                	o=fhm.get(cfid);
                	if(!(o instanceof MemoryScope)) continue;
                	MemoryScope scope=(MemoryScope) o;
    				// close
    				if(scope.isExpired()) {
    					// TODO macht das sinn? ist das nicht kopierleiche?
    					ApplicationImpl application=(ApplicationImpl) applicationContextes.get(applicationName);
    					long appLastAccess=0;
    					if(application!=null){
    						appLastAccess=application.getLastAccess();
    						application.touch();
    					}
    					scope.touch();
                        
    					try {
    						if(type==Scope.SCOPE_SESSION)listener.onSessionEnd(cfmlFactory,(String)applicationName,(String)cfid);
    					} 
    					catch (Throwable t) {t.printStackTrace();
    						ExceptionHandler.log(cfmlFactory.getConfig(),Caster.toPageException(t));
    					}
    					finally {
    						if(application!=null)application.setLastAccess(appLastAccess);
    						fhm.remove(cfids[y]);
        					scope.release();
        					getLog().info("scope-context", "remove memory based "+VariableInterpreter.scopeInt2String(type)+" scope for "+applicationName+"/"+cfid);
        					count--;
    					}
    				}
    			}
                if(count==0)contextes.remove(arrContextes[i]);
            }
		}
	}
	
	private void clearUnusedApplications(CFMLFactoryImpl jspFactory) {
        
        if(applicationContextes.size()==0)return;
		
		long now=System.currentTimeMillis();
		Object[] arrContextes= applicationContextes.keySet().toArray();
		ApplicationListener listener = jspFactory.getConfig().getApplicationListener();
		for(int i=0;i<arrContextes.length;i++) {
            Application application=(Application) applicationContextes.get(arrContextes[i]);
			
			if(application.getLastAccess()+application.getTimeSpan()<now) {
                //SystemOut .printDate(jspFactory.getConfigWebImpl().getOut(),"Clear application scope:"+arrContextes[i]+"-"+this);
                application.touch();
				try {
					listener.onApplicationEnd(jspFactory,(String)arrContextes[i]);
				} 
				catch (Throwable t) {
					ExceptionHandler.log(jspFactory.getConfig(),Caster.toPageException(t));
				}
				finally {
					applicationContextes.remove(arrContextes[i]);
					application.release();
				}
				
			}
		}
	}

	/**
	 * @return returns a new CFIs
	 */
	public static String getNewCFId() {
		return generator.generateRandomBasedUUID().toString();
	}
	
	/**
	 * @return returns a new CFToken
	 */
	public static String getNewCFToken() {
		return "0";
	}


}