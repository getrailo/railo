package railo.runtime.listener;

import railo.commons.lang.StringUtil;
import railo.commons.lang.types.RefBoolean;
import railo.runtime.Component;
import railo.runtime.ComponentWrap;
import railo.runtime.Mapping;
import railo.runtime.PageContext;
import railo.runtime.component.Member;
import railo.runtime.config.ConfigWebImpl;
import railo.runtime.net.s3.Properties;
import railo.runtime.op.Caster;
import railo.runtime.op.Decision;
import railo.runtime.orm.ORMConfiguration;
import railo.runtime.type.Collection;
import railo.runtime.type.Collection.Key;
import railo.runtime.type.KeyImpl;
import railo.runtime.type.Scope;
import railo.runtime.type.Struct;
import railo.runtime.type.StructImpl;
import railo.runtime.type.cfc.ComponentAccess;
import railo.runtime.type.dt.TimeSpan;

/**
 * @author mic
 *
 */
/**
 * @author mic
 *
 */
public class ModernApplicationContext extends ApplicationContextSupport {

	private static final long serialVersionUID = -8230105685329758613L;

	private static final Collection.Key NAME = KeyImpl.getInstance("name");
	private static final Collection.Key APPLICATION_TIMEOUT = KeyImpl.getInstance("applicationTimeout");
	private static final Collection.Key CLIENT_MANAGEMENT = KeyImpl.getInstance("clientManagement");
	private static final Collection.Key CLIENT_STORAGE = KeyImpl.getInstance("clientStorage");
	private static final Collection.Key SESSION_STORAGE = KeyImpl.getInstance("sessionStorage");
	private static final Collection.Key LOGIN_STORAGE = KeyImpl.getInstance("loginStorage");
	private static final Collection.Key SESSION_TYPE = KeyImpl.getInstance("sessionType");
	private static final Collection.Key SESSION_MANAGEMENT = KeyImpl.getInstance("sessionManagement");
	private static final Collection.Key SESSION_TIMEOUT = KeyImpl.getInstance("sessionTimeout");
	private static final Collection.Key CLIENT_TIMEOUT = KeyImpl.getInstance("clientTimeout");
	private static final Collection.Key SET_CLIENT_COOKIES = KeyImpl.getInstance("setClientCookies");
	private static final Collection.Key SET_DOMAIN_COOKIES = KeyImpl.getInstance("setDomainCookies");
	private static final Collection.Key SCRIPT_PROTECT = KeyImpl.getInstance("scriptProtect");
	private static final Collection.Key MAPPINGS = KeyImpl.getInstance("mappings");
	private static final Collection.Key CUSTOM_TAG_PATHS = KeyImpl.getInstance("customtagpaths");
	private static final Collection.Key COMPONENT_PATHS = KeyImpl.getInstance("componentpaths");
	private static final Collection.Key SECURE_JSON_PREFIX = KeyImpl.getInstance("secureJsonPrefix");
	private static final Collection.Key SECURE_JSON = KeyImpl.getInstance("secureJson");
	private static final Collection.Key LOCAL_MODE = KeyImpl.getInstance("localMode");
	private static final Collection.Key SESSION_CLUSTER = KeyImpl.getInstance("sessionCluster");
	private static final Collection.Key CLIENT_CLUSTER = KeyImpl.getInstance("clientCluster");
	

	private static final Collection.Key DEFAULT_DATA_SOURCE = KeyImpl.getInstance("defaultdatasource");
	private static final Collection.Key DATA_SOURCE = KeyImpl.getInstance("datasource");
	private static final Collection.Key ORM_ENABLED = KeyImpl.getInstance("ormenabled");
	private static final Collection.Key ORM_SETTINGS = KeyImpl.getInstance("ormsettings");
	public static final Collection.Key S3 = KeyImpl.getInstance("s3");
	
	
	private ComponentAccess component;
	private ConfigWebImpl ci;

	private String name=null;
	
	private boolean setClientCookies;
	private boolean setDomainCookies;
	private boolean setSessionManagement;
	private boolean setClientManagement;
	private TimeSpan sessionTimeout;
	private TimeSpan clientTimeout;
	private TimeSpan applicationTimeout;
	private int loginStorage=Scope.SCOPE_COOKIE;
	private int scriptProtect;
	private String defaultDataSource;
	private int localMode;
	private short sessionType;
	private boolean sessionCluster;
	private boolean clientCluster;
	

	private String clientStorage;
	private String sessionStorage;
	private String secureJsonPrefix="//";
	private boolean secureJson; 
	private Mapping[] mappings;
	private Mapping[] ctmappings;
	private Mapping[] cmappings;
	private Properties s3;
	
	private boolean initApplicationTimeout;
	private boolean initSessionTimeout;
	private boolean initClientTimeout;
	private boolean initSetClientCookies;
	private boolean initSetClientManagement;
	private boolean initSetDomainCookies;
	private boolean initSetSessionManagement;
	private boolean initScriptProtect;
	private boolean initClientStorage;
	private boolean initSecureJsonPrefix;
	private boolean initSecureJson;
	private boolean initSessionStorage;
	private boolean initSessionCluster;
	private boolean initClientCluster;
	private boolean initLoginStorage;
	private boolean initSessionType;
	private boolean initMappings;
	private boolean initCTMappings;
	private boolean initCMappings;
	private boolean initLocalMode;
	private boolean initS3;
	private boolean ormEnabled;
	private ORMConfiguration ormConfig;
	private String ormDatasource;
		
	public ModernApplicationContext(PageContext pc, ComponentAccess cfc, RefBoolean throwsErrorWhileInit) {
		ci = ((ConfigWebImpl)pc.getConfig());
    	setClientCookies=ci.isClientCookies();
        setDomainCookies=ci.isDomainCookies();
        setSessionManagement=ci.isSessionManagement();
        setClientManagement=ci.isClientManagement();
        sessionTimeout=ci.getSessionTimeout();
        clientTimeout=ci.getClientTimeout();
        applicationTimeout=ci.getApplicationTimeout();
        scriptProtect=ci.getScriptProtect();
        this.defaultDataSource=ci.getDefaultDataSource();
        this.localMode=ci.getLocalMode();
        this.sessionType=ci.getSessionType();
        this.sessionCluster=ci.getSessionCluster();
        this.clientCluster=ci.getClientCluster();
        /* TODO
        this.secureJsonPrefix=ci.getSecureJsonPrefix();
        this.secureJson=ci.getSecureJson();
        this.clientStorage=ci.getClientStorage();
        this.sessionStorage=ci.getSessionStorage();
        this.loginStorage=ci.getLoginStorage();
        */
        
        
		this.component=cfc;
		
		Object o;
		pc.addPageSource(component.getPageSource(), true);
		try {
			
		


			// datasource
			o = get(component,DATA_SOURCE,null);
			if(o!=null) {
				String ds = Caster.toString(o);
				this.defaultDataSource = ds;
				this.ormDatasource = ds;
			}

			// default datasource
			o=get(component,DEFAULT_DATA_SOURCE,null);
			if(o!=null) this.defaultDataSource =Caster.toString(o);
			
			/////////// ORM /////////////////////////////////
			// ormenabled
			o=get(component,ORM_ENABLED,null);
			if(o!=null && Caster.toBooleanValue(o,false)){
				this.ormEnabled=true;
				
				// settings
				o=get(component,ORM_SETTINGS,null);
				Struct settings;
				if(o instanceof Struct)	settings=(Struct) o;
				else	settings=new StructImpl();
				AppListenerUtil.setORMConfiguration(pc, this, settings);
			}
			
			throwsErrorWhileInit.setValue(false);
		}
		catch(Throwable t) {
			throwsErrorWhileInit.setValue(true);
			pc.removeLastPageSource(true);
		}
	}


	
	/**
	 * @see railo.runtime.util.ApplicationContext#hasName()
	 */
	public boolean hasName() {
		return !StringUtil.isEmpty(getName());
	}
	
	/**
	 * @see railo.runtime.util.ApplicationContext#getName()
	 */
	public String getName() {
		if(this.name==null) {
			this.name=Caster.toString(get(component,NAME,""),"");
		}
		return name;
	}

	/**
	 * @see railo.runtime.util.ApplicationContext#getLoginStorage()
	 */
	public int getLoginStorage() {
		if(!initLoginStorage) {
			String str=null;
			Object o = get(component,LOGIN_STORAGE,null);
			if(o!=null){ 
				str=Caster.toString(o,null);
				if(str!=null)loginStorage=AppListenerUtil.translateLoginStorage(str,loginStorage);
			}
			initLoginStorage=true; 
		}
		return loginStorage;
	}

	/**
	 * @see railo.runtime.util.ApplicationContext#getApplicationTimeout()
	 */
	public TimeSpan getApplicationTimeout() {
		if(!initApplicationTimeout) {
			Object o=get(component,APPLICATION_TIMEOUT,null);
			if(o!=null)applicationTimeout=Caster.toTimespan(o,applicationTimeout);
			initApplicationTimeout=true;
		}
		return applicationTimeout;
	}

	/**
	 * @see railo.runtime.util.ApplicationContext#getSessionTimeout()
	 */
	public TimeSpan getSessionTimeout() {
		if(!initSessionTimeout) {
			Object o=get(component,SESSION_TIMEOUT,null);
			if(o!=null)sessionTimeout=Caster.toTimespan(o,sessionTimeout);
			initSessionTimeout=true;
		}
		return sessionTimeout;
	}

	/**
	 * @see railo.runtime.listener.ApplicationContextPro#getClientTimeout()
	 */
	public TimeSpan getClientTimeout() {
		if(!initClientTimeout) {
			Object o=get(component,CLIENT_TIMEOUT,null);
			if(o!=null)clientTimeout=Caster.toTimespan(o,clientTimeout);
			initClientTimeout=true;
		}
		return clientTimeout;
	}

	/**
	 * @see railo.runtime.util.ApplicationContext#isSetClientCookies()
	 */
	public boolean isSetClientCookies() {
		if(!initSetClientCookies) {
			Object o = get(component,SET_CLIENT_COOKIES,null);
			if(o!=null)setClientCookies=Caster.toBooleanValue(o,setClientCookies);
			initSetClientCookies=true;
		}
		return setClientCookies;
	}

	/**
	 * @see railo.runtime.util.ApplicationContext#isSetClientManagement()
	 */
	public boolean isSetClientManagement() {
		if(!initSetClientManagement) {
			Object o = get(component,CLIENT_MANAGEMENT,null);
			if(o!=null)setClientManagement=Caster.toBooleanValue(o,setClientManagement);
			initSetClientManagement=true;
		}
		return setClientManagement;
	}

	/**
	 * @see railo.runtime.util.ApplicationContext#isSetDomainCookies()
	 */
	public boolean isSetDomainCookies() {
		if(!initSetDomainCookies) {
			Object o = get(component,SET_DOMAIN_COOKIES,null);
			if(o!=null)setDomainCookies=Caster.toBooleanValue(o,setDomainCookies);
			initSetDomainCookies=true;
		}
		return setDomainCookies;
	}

	/**
	 * @see railo.runtime.util.ApplicationContext#isSetSessionManagement()
	 */
	public boolean isSetSessionManagement() {
		if(!initSetSessionManagement) {
			Object o = get(component,SESSION_MANAGEMENT,null);
			if(o!=null)setSessionManagement=Caster.toBooleanValue(o,setSessionManagement);
			initSetSessionManagement=true; 
		}
		return setSessionManagement;
	}

	/**
	 * @see railo.runtime.util.ApplicationContext#getClientstorage()
	 */
	public String getClientstorage() {
		if(!initClientStorage) {
			Object o=get(component,CLIENT_STORAGE,null);
			if(o!=null)clientStorage=Caster.toString(o,clientStorage);
			initClientStorage=true;
		}
		return clientStorage;
	}

	/**
	 * @see railo.runtime.util.ApplicationContext#getScriptProtect()
	 */
	public int getScriptProtect() {
		if(!initScriptProtect) {
			String str=null;
			Object o = get(component,SCRIPT_PROTECT,null);
			if(o!=null){ 
				str=Caster.toString(o,null);
				if(str!=null)scriptProtect=AppListenerUtil.translateScriptProtect(str);
			}
			initScriptProtect=true; 
		}
		return scriptProtect;
	}

	/**
	 * @see railo.runtime.util.ApplicationContext#getSecureJsonPrefix()
	 */
	public String getSecureJsonPrefix() {
		if(!initSecureJsonPrefix) {
			Object o=get(component,SECURE_JSON_PREFIX,null);
			if(o!=null)secureJsonPrefix=Caster.toString(o,secureJsonPrefix);
			initSecureJsonPrefix=true;
		}
		return secureJsonPrefix;
	}

	/**
	 * @see railo.runtime.util.ApplicationContext#getSecureJson()
	 */
	public boolean getSecureJson() {
		if(!initSecureJson) {
			Object o = get(component,SECURE_JSON,null);
			if(o!=null)secureJson=Caster.toBooleanValue(o,secureJson);
			initSecureJson=true; 
		}
		return secureJson;
	}

	/**
	 * @see railo.runtime.listener.ApplicationContextPro#getSessionstorage()
	 */
	public String getSessionstorage() {
		if(!initSessionStorage) {
			Object o=get(component,SESSION_STORAGE,null);
			if(o!=null)sessionStorage=Caster.toString(o,sessionStorage);
			initSessionStorage=true;
		}
		return sessionStorage;
	}

	/**
	 * @see railo.runtime.listener.ApplicationContextPro#getSessionCluster()
	 */
	public boolean getSessionCluster() {
		if(!initSessionCluster) {
			Object o = get(component,SESSION_CLUSTER,null);
			if(o!=null)sessionCluster=Caster.toBooleanValue(o,sessionCluster);
			initSessionCluster=true; 
		}
		return sessionCluster;
	}

	/**
	 * @see railo.runtime.listener.ApplicationContextPro#getClientCluster()
	 */
	public boolean getClientCluster() {
		if(!initClientCluster) {
			Object o = get(component,CLIENT_CLUSTER,null);
			if(o!=null)clientCluster=Caster.toBooleanValue(o,clientCluster);
			initClientCluster=true; 
		}
		return clientCluster;
	}

	/**
	 * @see railo.runtime.listener.ApplicationContextPro#getSessionType()
	 */
	public short getSessionType() {
		if(!initSessionType) {
			String str=null;
			Object o = get(component,SESSION_TYPE,null);
			if(o!=null){ 
				str=Caster.toString(o,null);
				if(str!=null)sessionType=AppListenerUtil.toSessionType(str, sessionType);
			}
			initSessionType=true; 
		}
		return sessionType;
	}

	/**
	 * @see railo.runtime.util.ApplicationContext#getMappings()
	 */
	public Mapping[] getMappings() {
		if(!initMappings) {
			Object o = get(component,MAPPINGS,null);
			if(o!=null)mappings=AppListenerUtil.toMappings(ci,o,mappings);
			initMappings=true; 
		}
		return mappings;
	}

	/**
	 * @see railo.runtime.util.ApplicationContext#getCustomTagMappings()
	 */
	public Mapping[] getCustomTagMappings() {
		if(!initCTMappings) {
			Object o = get(component,CUSTOM_TAG_PATHS,null);
			if(o!=null)ctmappings=AppListenerUtil.toCustomTagMappings(ci,o,ctmappings);
			initCTMappings=true; 
		}
		return ctmappings;
	}

	/**
	 * @see railo.runtime.listener.ApplicationContextPro#getComponentMappings()
	 */
	public Mapping[] getComponentMappings() {
		if(!initCMappings) {
			Object o = get(component,COMPONENT_PATHS,null);
			if(o!=null)cmappings=AppListenerUtil.toCustomTagMappings(ci,o,cmappings);
			initCMappings=true; 
		}
		return cmappings;
	}

	/**
	 * @see railo.runtime.listener.ApplicationContextPro#getLocalMode()
	 */
	public int getLocalMode() {
		if(!initLocalMode) {
			Object o = get(component,LOCAL_MODE,null);
			if(o!=null)localMode=AppListenerUtil.toLocalMode(o, localMode);
			initLocalMode=true; 
		}
		return localMode;
	}

	/**
	 * @see railo.runtime.listener.ApplicationContextPro#getS3()
	 */
	public Properties getS3() {
		if(!initS3) {
			Object o = get(component,S3,null);
			if(o!=null && Decision.isStruct(o))s3=AppListenerUtil.toS3(Caster.toStruct(o,null));
			initS3=true; 
		}
		return s3;
	}

	/**
	 * @see railo.runtime.listener.ApplicationContextPro#getDefaultDataSource()
	 */
	public String getDefaultDataSource() {
		return defaultDataSource;
	}

	/**
	 * @see railo.runtime.listener.ApplicationContextPro#isORMEnabled()
	 */
	public boolean isORMEnabled() {
		return this.ormEnabled;
	}

	/**
	 * @see railo.runtime.listener.ApplicationContextPro#getORMDatasource()
	 */
	public String getORMDatasource() {
		return ormDatasource;
	}

	/**
	 * @see railo.runtime.listener.ApplicationContextPro#getORMConfiguration()
	 */
	public ORMConfiguration getORMConfiguration() {
		return ormConfig;
	}

	/**
	 * @see railo.runtime.listener.ApplicationContextPro#getComponent()
	 */
	public ComponentAccess getComponent() {
		return component;
	}

	/**
	 * @see railo.runtime.listener.ApplicationContextPro#getCustom(railo.runtime.type.Collection.Key)
	 */
	public Object getCustom(Key key) {
		try {
			ComponentWrap cw=ComponentWrap.toComponentWrap(Component.ACCESS_PRIVATE, component); 
			return cw.get(key,null);
		} 
		catch (Throwable t) {}
		
		return null;
	}
	
	
	



	private static Object get(ComponentAccess app, Key name,String defaultValue) {
		Member mem = app.getMember(Component.ACCESS_PRIVATE, name, true, false);
		if(mem==null) return defaultValue;
		return mem.getValue();
	}

	
//////////////////////// SETTERS /////////////////////////
	
	
	
	/* (non-Javadoc)
	 * @see railo.runtime.util.ApplicationContextPro#setApplicationTimeout(railo.runtime.type.dt.TimeSpan)
	 */
	public void setApplicationTimeout(TimeSpan applicationTimeout) {
		initApplicationTimeout=true;
		this.applicationTimeout=applicationTimeout;
	}

	/* (non-Javadoc)
	 * @see railo.runtime.util.ApplicationContextPro#setSessionTimeout(railo.runtime.type.dt.TimeSpan)
	 */
	public void setSessionTimeout(TimeSpan sessionTimeout) {
		initSessionTimeout=true;
		this.sessionTimeout=sessionTimeout;
	}

	/* (non-Javadoc)
	 * @see railo.runtime.util.ApplicationContextPro#setClientTimeout(railo.runtime.type.dt.TimeSpan)
	 */
	public void setClientTimeout(TimeSpan clientTimeout) {
		initClientTimeout=true;
		this.clientTimeout=clientTimeout;
	}

	/* (non-Javadoc)
	 * @see railo.runtime.util.ApplicationContextPro#setClientstorage(java.lang.String)
	 */
	public void setClientstorage(String clientstorage) {
		initClientStorage=true;
		this.clientStorage=clientstorage;
	}

	/* (non-Javadoc)
	 * @see railo.runtime.util.ApplicationContextPro#setSessionstorage(java.lang.String)
	 */
	public void setSessionstorage(String sessionstorage) {
		initSessionStorage=true;
		this.sessionStorage=sessionstorage;
	}

	/* (non-Javadoc)
	 * @see railo.runtime.util.ApplicationContextPro#setCustomTagMappings(railo.runtime.Mapping[])
	 */
	public void setCustomTagMappings(Mapping[] customTagMappings) {
		initCTMappings=true;
		this.ctmappings=customTagMappings;
	}

	/* (non-Javadoc)
	 * @see railo.runtime.util.ApplicationContextPro#setComponentMappings(railo.runtime.Mapping[])
	 */
	public void setComponentMappings(Mapping[] componentMappings) {
		initCMappings=true;
		this.cmappings=componentMappings;
	}

	/* (non-Javadoc)
	 * @see railo.runtime.util.ApplicationContextPro#setMappings(railo.runtime.Mapping[])
	 */
	public void setMappings(Mapping[] mappings) {
		initMappings=true;
		this.mappings=mappings;
	}

	/* (non-Javadoc)
	 * @see railo.runtime.util.ApplicationContextPro#setLoginStorage(int)
	 */
	public void setLoginStorage(int loginStorage) {
		initLoginStorage=true;
		this.loginStorage=loginStorage;
	}

	@Override
	public void setDefaultDataSource(String datasource) {
		this.defaultDataSource=datasource;
	}

	/* (non-Javadoc)
	 * @see railo.runtime.util.ApplicationContextPro#setScriptProtect(int)
	 */
	public void setScriptProtect(int scriptrotect) {
		initScriptProtect=true;
		this.scriptProtect=scriptrotect;
	}

	/* (non-Javadoc)
	 * @see railo.runtime.util.ApplicationContextPro#setSecureJson(boolean)
	 */
	public void setSecureJson(boolean secureJson) {
		initSecureJson=true;
		this.secureJson=secureJson;
	}

	/* (non-Javadoc)
	 * @see railo.runtime.util.ApplicationContextPro#setSecureJsonPrefix(java.lang.String)
	 */
	public void setSecureJsonPrefix(String secureJsonPrefix) {
		initSecureJsonPrefix=true;
		this.secureJsonPrefix=secureJsonPrefix;
	}

	/* (non-Javadoc)
	 * @see railo.runtime.util.ApplicationContextPro#setSetClientCookies(boolean)
	 */
	public void setSetClientCookies(boolean setClientCookies) {
		initSetClientCookies=true;
		this.setClientCookies=setClientCookies;
	}

	/* (non-Javadoc)
	 * @see railo.runtime.util.ApplicationContextPro#setSetClientManagement(boolean)
	 */
	public void setSetClientManagement(boolean setClientManagement) {
		initSetClientManagement=true;
		this.setClientManagement=setClientManagement;
	}

	/* (non-Javadoc)
	 * @see railo.runtime.util.ApplicationContextPro#setSetDomainCookies(boolean)
	 */
	public void setSetDomainCookies(boolean setDomainCookies) {
		initSetDomainCookies=true;
		this.setDomainCookies=setDomainCookies;
	}

	/* (non-Javadoc)
	 * @see railo.runtime.util.ApplicationContextPro#setSetSessionManagement(boolean)
	 */
	public void setSetSessionManagement(boolean setSessionManagement) {
		initSetSessionManagement=true;
		this.setSessionManagement=setSessionManagement;
	}

	/* (non-Javadoc)
	 * @see railo.runtime.util.ApplicationContextPro#setLocalMode(int)
	 */
	public void setLocalMode(int localMode) {
		initLocalMode=true;
		this.localMode=localMode;
	}

	/**
	 * @see railo.runtime.listener.ApplicationContextPro#setSessionType(short)
	 */
	public void setSessionType(short sessionType) {
		initSessionType=true;
		this.sessionType=sessionType;
	}

	/* (non-Javadoc)
	 * @see railo.runtime.util.ApplicationContextPro#setClientCluster(boolean)
	 */
	public void setClientCluster(boolean clientCluster) {
		initClientCluster=true;
		this.clientCluster=clientCluster;
	}

	/* (non-Javadoc)
	 * @see railo.runtime.util.ApplicationContextPro#setSessionCluster(boolean)
	 */
	public void setSessionCluster(boolean sessionCluster) {
		initSessionCluster=true;
		this.sessionCluster=sessionCluster;
	}

	/**
	 * @see railo.runtime.listener.ApplicationContextPro#setS3(railo.runtime.net.s3.Properties)
	 */
	public void setS3(Properties s3) {
		initS3=true;
		this.s3=s3;
	}

	/**
	 * @see railo.runtime.listener.ApplicationContextPro#setORMEnabled(boolean)
	 */
	public void setORMEnabled(boolean ormEnabled) {
		this.ormEnabled=ormEnabled;
	}

	public void setORMConfiguration(ORMConfiguration ormConfig) {
		this.ormConfig=ormConfig;
	}

	/**
	 * @see railo.runtime.listener.ApplicationContextPro#setORMDatasource(java.lang.String)
	 */
	public void setORMDatasource(String ormDatasource) {
		this.ormDatasource=ormDatasource;
	}


}
