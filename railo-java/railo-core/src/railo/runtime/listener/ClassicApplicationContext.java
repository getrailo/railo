package railo.runtime.listener;

import java.util.HashMap;
import java.util.Map;

import railo.commons.io.res.Resource;
import railo.commons.lang.StringUtil;
import railo.runtime.Mapping;
import railo.runtime.PageContext;
import railo.runtime.config.Config;
import railo.runtime.exp.ApplicationException;
import railo.runtime.exp.PageException;
import railo.runtime.net.s3.Properties;
import railo.runtime.net.s3.PropertiesImpl;
import railo.runtime.op.Duplicator;
import railo.runtime.orm.ORMConfiguration;
import railo.runtime.rest.RestSettings;
import railo.runtime.type.dt.TimeSpan;
import railo.runtime.type.scope.Scope;

/**
 * 
 */
public class ClassicApplicationContext extends ApplicationContextSupport {

	private static final long serialVersionUID = 940663152793150953L;

	private String name;
	private String sessionClusterKey;
    private boolean setClientCookies;
    private boolean setDomainCookies;
    private boolean setSessionManagement;
    private boolean setClientManagement;
    private TimeSpan sessionTimeout=null; 
	private TimeSpan clientTimeout;
    private TimeSpan applicationTimeout=null;
    private int loginStorage=-1;
    private String clientstorage;
    private String sessionstorage;
	private int scriptProtect;
	private Mapping[] mappings;
	private Mapping[] ctmappings;
	private Mapping[] cmappings;
	private boolean secureJson;
	private String secureJsonPrefix="//";
	private boolean isDefault;
	private String defaultDataSource;
	private boolean ormEnabled;
	private String ormdatasource;
	private ORMConfiguration config;
	private Properties s3;
	

	private int localMode;
	private short sessionType;
    private boolean sessionCluster;
    private boolean clientCluster;
	private Resource source;
	private boolean triggerComponentDataMember;
	private Map<Integer,String> defaultCaches=new HashMap<Integer, String>();
	private Map<Integer,Boolean> sameFieldAsArrays=new HashMap<Integer, Boolean>();

	private RestSettings restSettings;

	private Resource[] restCFCLocations;

	private JavaSettingsImpl javaSettings;

    
    /**
     * constructor of the class
     * @param config
     */
    public ClassicApplicationContext(Config config,String name,boolean isDefault, Resource source) {
    	this.name=name;
    	setClientCookies=config.isClientCookies();
        setDomainCookies=config.isDomainCookies();
        setSessionManagement=config.isSessionManagement();
        setClientManagement=config.isClientManagement();
        sessionTimeout=config.getSessionTimeout();
        clientTimeout=config.getClientTimeout();
        applicationTimeout=config.getApplicationTimeout();
        loginStorage=Scope.SCOPE_COOKIE;
        scriptProtect=config.getScriptProtect();
        this.isDefault=isDefault;
        this.defaultDataSource=config.getDefaultDataSource();
        this.localMode=config.getLocalMode();
        this.sessionType=config.getSessionType();
        this.sessionCluster=config.getSessionCluster();
        this.clientCluster=config.getClientCluster();
        this.source=source;
        this.triggerComponentDataMember=config.getTriggerComponentDataMember();
        this.restSettings=config.getRestSetting();
        this.javaSettings=new JavaSettingsImpl();
    }
    
    /**
     * Constructor of the class, only used by duplicate method
     */
    private ClassicApplicationContext() {
    	
    }
    

	public ApplicationContext duplicate() {
		ClassicApplicationContext dbl = new ClassicApplicationContext();
		
		
		dbl.name=name;
		dbl.setClientCookies=setClientCookies;
		dbl.setDomainCookies=setDomainCookies;
		dbl.setSessionManagement=setSessionManagement;
		dbl.setClientManagement=setClientManagement;
		dbl.sessionTimeout=sessionTimeout;
		dbl.clientTimeout=clientTimeout;
		dbl.applicationTimeout=applicationTimeout;
		dbl.loginStorage=loginStorage;
		dbl.clientstorage=clientstorage;
		dbl.sessionstorage=sessionstorage;
		dbl.scriptProtect=scriptProtect;
		dbl.mappings=mappings;
		dbl.ctmappings=ctmappings;
		dbl.cmappings=cmappings;
		dbl.secureJson=secureJson;
		dbl.secureJsonPrefix=secureJsonPrefix;
		dbl.isDefault=isDefault;
		dbl.defaultDataSource=defaultDataSource;
		dbl.applicationtoken=applicationtoken;
		dbl.cookiedomain=cookiedomain;
		dbl.idletimeout=idletimeout;
		dbl.localMode=localMode;
		dbl.sessionType=sessionType;
		dbl.triggerComponentDataMember=triggerComponentDataMember;
		dbl.restSettings=restSettings;
		dbl.defaultCaches=Duplicator.duplicateMap(defaultCaches, new HashMap<Integer, String>(),false );
		dbl.sameFieldAsArrays=Duplicator.duplicateMap(sameFieldAsArrays, new HashMap<Integer, Boolean>(),false );
		
		dbl.ormEnabled=ormEnabled;
		dbl.config=config;
		dbl.ormdatasource=ormdatasource;
		dbl.sessionCluster=sessionCluster;
		dbl.clientCluster=clientCluster;
		dbl.source=source;
		
		return dbl;
	}
    
    
    /**
     * @see railo.runtime.util.IApplicationContext#getApplicationTimeout()
     */
    public TimeSpan getApplicationTimeout() {
        return applicationTimeout;
    }
    /**
     * @param applicationTimeout The applicationTimeout to set.
     */
    public void setApplicationTimeout(TimeSpan applicationTimeout) {
        this.applicationTimeout = applicationTimeout;
    }
    /**
     * @see railo.runtime.util.IApplicationContext#getLoginStorage()
     */
    public int getLoginStorage() {
        return loginStorage;
    }
    /**
     * @param loginStorage The loginStorage to set.
     */
    public void setLoginStorage(int loginStorage) {
        this.loginStorage = loginStorage;
    }
    
    public void setLoginStorage(String strLoginStorage) throws ApplicationException {
    	setLoginStorage(AppListenerUtil.translateLoginStorage(strLoginStorage));
    }
    
    
    
    /**
     * @see railo.runtime.util.IApplicationContext#getFullName()
     */
    public String getName() {
        return name;
    }
    /**
     * @param name The name to set.
     */
    public void setName(String name) {
        this.name = name;
    }
    /**
     * @see railo.runtime.util.IApplicationContext#getSessionTimeout()
     */
    public TimeSpan getSessionTimeout() {
        return sessionTimeout;
    }
    
    /**
     * @param sessionTimeout The sessionTimeout to set.
     */
    public void setSessionTimeout(TimeSpan sessionTimeout) {
        this.sessionTimeout = sessionTimeout;
    }


    public TimeSpan getClientTimeout() {
        return clientTimeout;
    }
    
    /**
     * @param sessionTimeout The sessionTimeout to set.
     */
    public void setClientTimeout(TimeSpan clientTimeout) {
        this.clientTimeout = clientTimeout;
    }
    
    /**
     * @see railo.runtime.util.IApplicationContext#isSetClientCookies()
     */
    public boolean isSetClientCookies() {
        return setClientCookies;
    }
    /**
     * @param setClientCookies The setClientCookies to set.
     */
    public void setSetClientCookies(boolean setClientCookies) {
        this.setClientCookies = setClientCookies;
    }
    /**
     * @see railo.runtime.util.IApplicationContext#isSetClientManagement()
     */
    public boolean isSetClientManagement() {
        return setClientManagement;
    }
    /**
     * @param setClientManagement The setClientManagement to set.
     */
    public void setSetClientManagement(boolean setClientManagement) {
        this.setClientManagement = setClientManagement;
    }
    /**
     * @see railo.runtime.util.IApplicationContext#isSetDomainCookies()
     */
    public boolean isSetDomainCookies() {
        return setDomainCookies;
    }
    /**
     * @param setDomainCookies The setDomainCookies to set.
     */
    public void setSetDomainCookies(boolean setDomainCookies) {
        this.setDomainCookies = setDomainCookies;
    }
    /**
     * @see railo.runtime.util.IApplicationContext#isSetSessionManagement()
     */
    public boolean isSetSessionManagement() {
        return setSessionManagement;
    }
    /**
     * @param setSessionManagement The setSessionManagement to set.
     */
    public void setSetSessionManagement(boolean setSessionManagement) {
        this.setSessionManagement = setSessionManagement;
    }
    /**
     * @see railo.runtime.util.IApplicationContext#getClientstorage()
     */
    public String getClientstorage() {
        return clientstorage;
    }
    public String getSessionstorage() {
        return sessionstorage;
    }
    /**
     * @param clientstorage The clientstorage to set.
     */
    public void setClientstorage(String clientstorage) {
        this.clientstorage = clientstorage;
    }
    public void setSessionstorage(String sessionstorage) {
        this.sessionstorage = sessionstorage;
    }

    /**
     * @see railo.runtime.util.IApplicationContext#hasName()
     */
    public boolean hasName() {
        return name!=null;
    }
    
    /**
     * @param scriptProtect The scriptProtect to set.
     */
    public void setScriptProtect(int scriptProtect) {
		this.scriptProtect=scriptProtect;
	}

	/**
	 * @see railo.runtime.util.ApplicationContext#getScriptProtect()
	 */
	public int getScriptProtect() {
		//if(isDefault)print.err("get:"+scriptProtect);
		return scriptProtect;
	}

	


	public void setMappings(Mapping[] mappings) {
		if(mappings.length>0)this.mappings=mappings;
	}

	/**
	 * @return the mappings
	 */
	public Mapping[] getMappings() {
		return mappings;
	}

	public void setCustomTagMappings(Mapping[] ctmappings) {
		this.ctmappings=ctmappings;
	}

	public Mapping[] getCustomTagMappings() {
		return ctmappings;
	}

	public void setComponentMappings(Mapping[] cmappings) {
		this.cmappings=cmappings;
	}

	public Mapping[] getComponentMappings() {
		return cmappings;
	}

	public void setSecureJson(boolean secureJson) {
		this.secureJson=secureJson;
	}
	
	public void setSecureJsonPrefix(String secureJsonPrefix) {
		this.secureJsonPrefix=secureJsonPrefix;
	}

	/**
	 * @return the secureJson
	 */
	public boolean getSecureJson() {
		return secureJson;
	}

	/**
	 * @return the secureJsonPrefix
	 */
	public String getSecureJsonPrefix() {
		return secureJsonPrefix;
	}

	 /**
	 * @return the defaultDataSource
	 */
	public String getDefaultDataSource() {
		return defaultDataSource;
	}

	/**
	 * @param defaultDataSource the defaultDataSource to set
	 */
	public void setDefaultDataSource(String defaultDataSource) {
		this.defaultDataSource = defaultDataSource;
	}
	
	public void setORMDataSource(String ormdatasource) {
		this.ormdatasource = ormdatasource;
	}

	public boolean isORMEnabled() {
		return ormEnabled;
	}

	public String getORMDatasource() {
		return ormdatasource;
	}

	public ORMConfiguration getORMConfiguration() {
		return config;
	}
	public void setORMConfiguration(ORMConfiguration config) {
		this.config= config;
	}

	public void setORMEnabled(boolean ormEnabled) {
		this.ormEnabled=ormEnabled;
	}

	/**
	 * @return the s3
	 */
	public Properties getS3() {
		if(s3==null) s3=new PropertiesImpl();
		return s3;
	}

	/**
	 * @return the localMode
	 */
	public int getLocalMode() {
		return localMode;
	}


	/**
	 * @param localMode the localMode to set
	 */
	public void setLocalMode(int localMode) {
		this.localMode = localMode;
	}



    /**
	 * @return the sessionType
	 */
	public short getSessionType() {
		return sessionType;
	}

    /**
	 * @return the sessionType
	 */
	public void setSessionType(short sessionType) {
		this.sessionType= sessionType;
	}


	/**
	 * @return the sessionCluster
	 */
	public boolean getSessionCluster() {
		return sessionCluster;
	}


	/**
	 * @param sessionCluster the sessionCluster to set
	 */
	public void setSessionCluster(boolean sessionCluster) {
		this.sessionCluster = sessionCluster;
	}
	
	
	public void setSessionClusterKey(String key) {
		this.sessionClusterKey = key;
	}


	/**
	 * @return the clientCluster
	 */
	public boolean getClientCluster() {
		return clientCluster;
	}


	/**
	 * @param clientCluster the clientCluster to set
	 */
	public void setClientCluster(boolean clientCluster) {
		this.clientCluster = clientCluster;
	}


	public void setS3(Properties s3) {
		this.s3=s3;
	}

	/**
	 * @see railo.runtime.listener.ApplicationContext#setORMDatasource(java.lang.String)
	 */
	public void setORMDatasource(String ormdatasource) {
		this.ormdatasource=ormdatasource;
	}

	@Override
	public void reinitORM(PageContext pc) throws PageException {
		// do nothing
	}

	@Override
	public Resource getSource() {
		return source;
	}

	@Override
	public boolean getTriggerComponentDataMember() {
		return triggerComponentDataMember;
	}

	public String getSessionClusterKey() {
		if(this.sessionClusterKey == null)
			return this.name;
		return this.sessionClusterKey;
	}

	@Override
	public void setTriggerComponentDataMember(boolean triggerComponentDataMember) {
		this.triggerComponentDataMember=triggerComponentDataMember;
	}

	@Override
	public void setDefaultCacheName(int type,String name) {
		if(StringUtil.isEmpty(name,true)) return;
		defaultCaches.put(type, name.trim());
	}
	
	@Override
	public String getDefaultCacheName(int type) {
		return defaultCaches.get(type);
	}

	public void setSameFieldAsArray(int scope, boolean sameFieldAsArray) {
		sameFieldAsArrays.put(scope, sameFieldAsArray);
	}
	
	
	@Override
	public boolean getSameFieldAsArray(int scope) {
		Boolean b= sameFieldAsArrays.get(scope);
		if(b==null) return false;
		return b.booleanValue();
	}

	@Override
	public RestSettings getRestSettings() {
		return restSettings;
	}

	public void setRestSettings(RestSettings restSettings) {
		this.restSettings=restSettings;
	}
	

	public void setRestCFCLocations(Resource[] restCFCLocations) {
		this.restCFCLocations = restCFCLocations;
	}

	@Override
	public Resource[] getRestCFCLocations() {
		return restCFCLocations;
	}

	@Override
	public JavaSettings getJavaSettings() {
		return javaSettings;
	}
}