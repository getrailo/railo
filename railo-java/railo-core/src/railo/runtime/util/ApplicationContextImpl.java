package railo.runtime.util;

import railo.runtime.Mapping;
import railo.runtime.config.Config;
import railo.runtime.config.ConfigImpl;
import railo.runtime.exp.ApplicationException;
import railo.runtime.exp.PageException;
import railo.runtime.type.ArrayImpl;
import railo.runtime.type.List;
import railo.runtime.type.Scope;
import railo.runtime.type.dt.TimeSpan;

/**
 * 
 */
public final class ApplicationContextImpl implements ApplicationContext {
   

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
		this.scriptProtect=translateScriptProtect(strScriptProtect);
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

	/**
	 * translate string definition of script protect to int definition
	 * @param scriptProtect
	 * @return
	 */
	public static int translateScriptProtect(String strScriptProtect) {
		strScriptProtect=strScriptProtect.toLowerCase().trim();
		
		if("none".equals(strScriptProtect)) return ApplicationContext.SCRIPT_PROTECT_NONE;
		if("no".equals(strScriptProtect)) return ApplicationContext.SCRIPT_PROTECT_NONE;
		if("false".equals(strScriptProtect)) return ApplicationContext.SCRIPT_PROTECT_NONE;
		
		if("all".equals(strScriptProtect)) return ApplicationContext.SCRIPT_PROTECT_ALL;
		if("true".equals(strScriptProtect)) return ApplicationContext.SCRIPT_PROTECT_ALL;
		if("yes".equals(strScriptProtect)) return ApplicationContext.SCRIPT_PROTECT_ALL;
		
		String[] arr = List.listToStringArray(strScriptProtect, ',');
		String item;
		int scriptProtect=0;
		for(int i=0;i<arr.length;i++) {
			item=arr[i].trim();
			if("cgi".equals(item) && (scriptProtect&ApplicationContext.SCRIPT_PROTECT_CGI)==0)
				scriptProtect+=ApplicationContext.SCRIPT_PROTECT_CGI;
			else if("cookie".equals(item) && (scriptProtect&ApplicationContext.SCRIPT_PROTECT_COOKIE)==0)
				scriptProtect+=ApplicationContext.SCRIPT_PROTECT_COOKIE;
			else if("form".equals(item) && (scriptProtect&ApplicationContext.SCRIPT_PROTECT_FORM)==0)
				scriptProtect+=ApplicationContext.SCRIPT_PROTECT_FORM;
			else if("url".equals(item) && (scriptProtect&ApplicationContext.SCRIPT_PROTECT_URL)==0)
				scriptProtect+=ApplicationContext.SCRIPT_PROTECT_URL;
			
		}
		
		return scriptProtect;
	}
	
	/**
	 * translate int definition of script protect to string definition
	 * @param scriptProtect
	 * @return
	 */
	public static String translateScriptProtect(int scriptProtect) {
		if(scriptProtect==ApplicationContext.SCRIPT_PROTECT_NONE) return "none";
		if(scriptProtect==ApplicationContext.SCRIPT_PROTECT_ALL) return "all";
		
		ArrayImpl arr=new ArrayImpl();
		if((scriptProtect&ApplicationContext.SCRIPT_PROTECT_CGI)>0) arr.add("cgi");
		if((scriptProtect&ApplicationContext.SCRIPT_PROTECT_COOKIE)>0) arr.add("cookie");
		if((scriptProtect&ApplicationContext.SCRIPT_PROTECT_FORM)>0) arr.add("form");
		if((scriptProtect&ApplicationContext.SCRIPT_PROTECT_URL)>0) arr.add("url");
		
		
		
		try {
			return List.arrayToList(arr, ",");
		} catch (PageException e) {
			return "none";
		} 
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

}