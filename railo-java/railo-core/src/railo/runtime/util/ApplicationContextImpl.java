package railo.runtime.util;

import railo.runtime.Mapping;
import railo.runtime.config.Config;
import railo.runtime.config.ConfigImpl;
import railo.runtime.exp.ApplicationException;
import railo.runtime.listener.ApplicationContextUtil;
import railo.runtime.orm.ORMConfiguration;
import railo.runtime.type.Scope;
import railo.runtime.type.dt.TimeSpan;

/**
 * 
 */
public class ApplicationContextImpl implements ApplicationContextPro {
   

	private String name;
    private boolean setClientCookies;
    private boolean setDomainCookies;
    private boolean setSessionManagement;
    private boolean setClientManagement;
    private TimeSpan sessionTimeout=null; 
    private TimeSpan applicationTimeout=null;
    private int loginStorage=-1;
    private String clientstorage;
	private int scriptProtect;
	private Mapping[] mappings;
	private Mapping[] ctmappings;
	private boolean secureJson;
	private String secureJsonPrefix="//";
	private boolean isDefault;
	private String defaultDataSource;
	private boolean ormEnabled;
	private String ormdatasource;
	private ORMConfiguration config;
    
    /**
     * constructor of the class
     * @param config
     */
    public ApplicationContextImpl(Config config, boolean isDefault) {
        setClientCookies=config.isClientCookies();
        setDomainCookies=config.isDomainCookies();
        setSessionManagement=config.isSessionManagement();
        setClientManagement=config.isClientManagement();
        sessionTimeout=config.getSessionTimeout();
        applicationTimeout=config.getApplicationTimeout();
        loginStorage=Scope.SCOPE_COOKIE;
        scriptProtect=config.getScriptProtect();
        this.isDefault=isDefault;
        this.defaultDataSource=((ConfigImpl)config).getDefaultDataSource();
        
    }
    
    /**
     * Constructor of the class, only used by duplicate method
     */
    private ApplicationContextImpl() {
    	
    }
    

	public ApplicationContext duplicate() {
		ApplicationContextImpl dbl = new ApplicationContextImpl();
		dbl.name=name;
		dbl.setClientCookies=setClientCookies;
		dbl.setDomainCookies=setDomainCookies;
		dbl.setSessionManagement=setSessionManagement;
		dbl.setClientManagement=setClientManagement;
		dbl.sessionTimeout=sessionTimeout;
		dbl.applicationTimeout=applicationTimeout;
		dbl.loginStorage=loginStorage;
		dbl.clientstorage=clientstorage;
		dbl.scriptProtect=scriptProtect;
		dbl.mappings=mappings;
		dbl.ctmappings=ctmappings;
		dbl.secureJson=secureJson;
		dbl.secureJsonPrefix=secureJsonPrefix;
		dbl.isDefault=isDefault;
		dbl.defaultDataSource=defaultDataSource;
		
		dbl.ormEnabled=ormEnabled;
		dbl.config=config;
		dbl.ormdatasource=ormdatasource;

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
    	strLoginStorage=strLoginStorage.toLowerCase().trim();
        if(strLoginStorage.equals("session"))setLoginStorage(Scope.SCOPE_SESSION);
        else if(strLoginStorage.equals("cookie"))setLoginStorage(Scope.SCOPE_COOKIE);
        else throw new ApplicationException("invalid loginStorage definition ["+strLoginStorage+"], valid values are [session,cookie]");
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
    /**
     * @param clientstorage The clientstorage to set.
     */
    public void setClientstorage(String clientstorage) {
        this.clientstorage = clientstorage;
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
    public void setScriptProtect(String strScriptProtect) {
		this.scriptProtect=ApplicationContextUtil.translateScriptProtect(strScriptProtect);
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
		//if(config==null)print.dumpStack();
		this.config= config;
	}

	public void setORMEnabled(boolean ormEnabled) {
		this.ormEnabled=ormEnabled;
	}


}