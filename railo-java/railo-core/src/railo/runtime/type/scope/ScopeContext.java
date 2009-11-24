package railo.runtime.type.scope;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpSession;

import org.safehaus.uuid.UUIDGenerator;

import railo.commons.collections.HashTable;
import railo.commons.lang.ClassUtil;
import railo.commons.lang.SizeOf;
import railo.commons.lang.StringUtil;
import railo.commons.lang.types.RefBoolean;
import railo.runtime.CFMLFactoryImpl;
import railo.runtime.PageContext;
import railo.runtime.config.Config;
import railo.runtime.config.ConfigServer;
import railo.runtime.exp.ExceptionHandler;
import railo.runtime.exp.ExpressionException;
import railo.runtime.exp.PageException;
import railo.runtime.listener.ApplicationListener;
import railo.runtime.op.Caster;
import railo.runtime.reflection.Reflector;
import railo.runtime.type.Scope;
import railo.runtime.type.Struct;
import railo.runtime.type.StructImpl;
import railo.runtime.type.util.ArrayUtil;
import railo.runtime.type.wrap.MapAsStruct;
import railo.runtime.util.ApplicationContext;

/**
 * Scope Context handle Apllication and Session Scopes
 */
public final class ScopeContext {
	
	private static final long CLIENT_MEMORY_TIMESPAN =  1*60*1000;
	
	private static UUIDGenerator generator = UUIDGenerator.getInstance();
	private Map cfSessionContextes=new HashTable();
	private Map cfClientContextes=new HashTable();
	private Map applicationContextes=new HashTable();

	private int maxSessionTimeout=0;

	private static Cluster cluster;
	private static Server server=null;
	
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
	        server=new ServerImpl();
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
	
	
	public Client getClientScope(PageContext pc) throws PageException {
		Client client=null;
		ApplicationContext appContext = pc.getApplicationContext(); 
		// get Context
			Map context=getSubMap(cfClientContextes,appContext.getName());
			
		// get Client
			String storage = appContext.getClientstorage();
			if(!StringUtil.isEmpty(storage))storage=storage.toLowerCase();
			else storage="";
			client=(Client) context.get(pc.getCFID()+storage);
			if(client==null) {
				if(StringUtil.isEmpty(storage) || "file".equals(storage) || "registry".equals(storage)){
					client=ClientFile.getInstance(appContext.getName(),pc);
				}
				else if("cookie".equals(storage))
					client=ClientCookie.getInstance(appContext.getName(),pc);
				else if("memory".equals(storage) || "ram".equals(storage)){
					//storage="ram";
					client=ClientMemory.getInstance(pc);
				}
				else{
					//storage="db";
					client=ClientDatasource.getInstance(storage,appContext.getName(),pc);
				}
				context.put(pc.getCFID()+storage,client);
			}
			client.initialize(pc);
			return client;
	}
	
	public Client getClientScopeEL(PageContext pc) {
		Client client=null;
		ApplicationContext appContext = pc.getApplicationContext(); 
		// get Context
			Map context=getSubMap(cfClientContextes,appContext.getName());
			
		// get Client
			String storage = StringUtil.toLowerCase(StringUtil.toStringEmptyIfNull(appContext.getClientstorage()));
			client=(Client) context.get(pc.getCFID()+storage);
			
			if(client==null) {
				if(StringUtil.isEmpty(storage) || "file".equals(storage) || "registry".equals(storage)){
					client=ClientFile.getInstance(appContext.getName(),pc);
				}
				else if("cookie".equals(storage))
					client=ClientCookie.getInstance(appContext.getName(),pc);
				else if("memory".equals(storage) || "ram".equals(storage)){
					//storage="ram";
					client=ClientMemory.getInstance(pc);
				}
				else{
					//storage="db";
					client=ClientDatasource.getInstanceEL(storage,appContext.getName(),pc);
				}
				context.put(pc.getCFID()+storage,client);
			}
			client.initialize(pc);
			return client;
	}



	/**
	 * return the session count of all application contextes
	 * @return
	 */
	public int getSessionCount(PageContext pc) {
		if(pc.getConfig().getSessionType()==Config.SESSION_TYPE_J2EE) return 0;
		
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
		if(pc.getConfig().getSessionType()==Config.SESSION_TYPE_J2EE) return 0;
		ApplicationContext appContext = pc.getApplicationContext(); 
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
        if(pc.getConfig().getSessionType()==Config.SESSION_TYPE_J2EE)return new StructImpl();
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
	public Session getSessionScope(PageContext pc,RefBoolean isNew) {
        if(pc.getConfig().getSessionType()==Config.SESSION_TYPE_CFML)return getCFSessionScope(pc,isNew);
		return getJSessionScope(pc,isNew);
	}
	
	/**
	 * return cf session scope
	 * @param pc PageContext
	 * @param listener 
	 * @return cf session matching the context
	 * @throws PageException 
	 */
	private Session getCFSessionScope(PageContext pc, RefBoolean isNew) {
		
		ApplicationContext appContext = pc.getApplicationContext(); 
		// get Context
			Map context=getSubMap(cfSessionContextes,appContext.getName());
			
		// get Session
			Session session=(Session) context.get(pc.getCFID());
			if(session instanceof CFSession) {
				if(session.isExpired()) {
					session.touch();
				}
			}
			else {
			    session=new CFSession();
				context.put(pc.getCFID(),session);
				isNew.setValue(true);
			}
			session.initialize(pc);
			return session;
	}
	
	/**
	 * return j session scope
	 * @param pc PageContext
	 * @param listener 
	 * @return j session matching the context
	 * @throws PageException
	 */
	private Session getJSessionScope(PageContext pc, RefBoolean isNew) {
        HttpSession httpSession=pc.getSession();
        ApplicationContext appContext = pc.getApplicationContext(); 
        Session session=null;
		
        int s=(int) appContext.getSessionTimeout().getSeconds();
        if(maxSessionTimeout<s)maxSessionTimeout=s;
        
        if(httpSession!=null) {
        	httpSession.setMaxInactiveInterval(maxSessionTimeout);
        	session=(Session) httpSession.getAttribute(appContext.getName());
        }
     // call of listeners
        else {
        	Map context=getSubMap(cfSessionContextes,appContext.getName());
        	session=(Session) context.get(pc.getCFID());
        }
        
		if(session instanceof JSession) {
            try {
                if(session.isExpired()) {
					session.touch();
                }
            }
            catch(ClassCastException cce) {
                session=new JSession();
                httpSession.setAttribute(appContext.getName(),session);
				isNew.setValue(true);
            }
		}
		else {
            session=new JSession();
		    httpSession.setAttribute(appContext.getName(),session);
			isNew.setValue(true);
			Map context=getSubMap(cfSessionContextes,appContext.getName());
			context.put(pc.getCFID(),session);
		}
		session.initialize(pc);
		return session;    
	}

	/**
	 * return the application Scope for this context (cfid,cftoken,contextname)
	 * @param pc PageContext 
	 * @param listener 
	 * @param isNew 
	 * @return session matching the context
	 * @throws PageException 
	 */
	public Application getApplicationScope(PageContext pc, RefBoolean isNew) {
		ApplicationContext appContext = pc.getApplicationContext(); 
		// getApplication Scope from Context
			Application application;
			Object objApp=applicationContextes.get(appContext.getName());
			if(objApp!=null) {
			    application=(Application)objApp;
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
			application.initialize(pc);
			//if(newApplication)listener.onApplicationStart(pc);
			
			return application;
	}
	
	public void removeApplicationScope(PageContext pc) {
		applicationContextes.remove(pc.getApplicationContext().getName());
	}
	
	

    /**
     * remove all unused scope objects
     */
    public void clearUnused(CFMLFactoryImpl jspFactory) {
        clearUnusedSessions(jspFactory);
        clearUnusedApplications(jspFactory);
        storeUnusedClients(jspFactory);
    }
    
    /**
     * remove all scope objects
     */
    public void clear() {
        cfSessionContextes.clear();
        applicationContextes.clear();
        server=null;
        
    }

    

	private void storeUnusedClients(CFMLFactoryImpl jspFactory) {
        if(cfClientContextes.size()==0)return;
		long now = System.currentTimeMillis();
		Object[] arrContextes= cfClientContextes.keySet().toArray();
		Object applicationName,cfid;
		
		for(int i=0;i<arrContextes.length;i++) {
			
			applicationName=arrContextes[i];
            Map fhm=(Map) cfClientContextes.get(applicationName);
            if(fhm.size()>0){
    			Object[] arrClients= fhm.keySet().toArray();
                int count=arrClients.length;
                for(int y=0;y<arrClients.length;y++) {
                	cfid=arrClients[y];
    				Client client=(Client) fhm.get(cfid);
    				if(client.lastVisit()+CLIENT_MEMORY_TIMESPAN<now) {
    					if(client instanceof ClientSupport)((ClientSupport)client).store();
    					fhm.remove(arrClients[y]);
        				count--;
    				}
    			}
                if(count==0)cfClientContextes.remove(arrContextes[i]);
            }
		}
	}
    
	/**
	 * @param jspFactory 
	 * 
	 */
	private void clearUnusedSessions(CFMLFactoryImpl jspFactory) {
        if(cfSessionContextes.size()==0)return;
		Object[] arrContextes= cfSessionContextes.keySet().toArray();
		ApplicationListener listener = jspFactory.getConfig().getApplicationListener();
		Object applicationName,cfid;
		
		for(int i=0;i<arrContextes.length;i++) {
			applicationName=arrContextes[i];
            Map fhm=(Map) cfSessionContextes.get(applicationName);
            if(fhm.size()>0){
    			Object[] arrSessions= fhm.keySet().toArray();
                int count=arrSessions.length;
                for(int y=0;y<arrSessions.length;y++) {
                	cfid=arrSessions[y];
    				Session session=(Session) fhm.get(cfid);
    				if(session.isExpired()) {
    					// TODO macht das sinn? ist das nicht kopierleiche?
    					ApplicationImpl application=(ApplicationImpl) applicationContextes.get(applicationName);
    					long appLastAccess=0;
    					if(application!=null){
    						appLastAccess=application.getLastAccess();
    						application.touch();
    					}
    					session.touch();
                        
    					try {
    						listener.onSessionEnd(jspFactory,(String)applicationName,(String)cfid);
    					} 
    					catch (Throwable t) {
    						ExceptionHandler.log(jspFactory.getConfig(),Caster.toPageException(t));
    					}
    					finally {
    						if(application!=null)application.setLastAccess(appLastAccess);
    						fhm.remove(arrSessions[y]);
        					session.release();
    						count--;
    					}
    				}
    			}
                if(count==0)cfSessionContextes.remove(arrContextes[i]);
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