package railo.runtime.listener;

import java.io.Serializable;

import railo.commons.io.res.Resource;
import railo.runtime.Mapping;
import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.net.s3.Properties;
import railo.runtime.orm.ORMConfiguration;
import railo.runtime.rest.RestSettings;
import railo.runtime.type.dt.TimeSpan;

/**
 * DTO Interface for Application Context data (defined by tag application)
 */
public interface ApplicationContext extends Serializable {

    public static final int SCRIPT_PROTECT_NONE = 0;
    public static final int SCRIPT_PROTECT_FORM = 1;
    public static final int SCRIPT_PROTECT_URL = 2;
    public static final int SCRIPT_PROTECT_CGI = 4;
    public static final int SCRIPT_PROTECT_COOKIE = 8;
    public static final int SCRIPT_PROTECT_ALL = SCRIPT_PROTECT_CGI+SCRIPT_PROTECT_COOKIE+SCRIPT_PROTECT_FORM+SCRIPT_PROTECT_URL;

	/**
     * @return Returns the applicationTimeout.
     */
    public abstract TimeSpan getApplicationTimeout();

    /**
     * @return Returns the loginStorage.
     */
    public abstract int getLoginStorage();

    /**
     * @return Returns the name.
     */
    public abstract String getName();

    /**
     * @return Returns the sessionTimeout.
     */
    public abstract TimeSpan getSessionTimeout();

    /**
     * @return Returns the setClientCookies.
     */
    public abstract boolean isSetClientCookies();

    /**
     * @return Returns the setClientManagement.
     */
    public abstract boolean isSetClientManagement();

    /**
     * @return Returns the setDomainCookies.
     */
    public abstract boolean isSetDomainCookies();

    /**
     * @return Returns the setSessionManagement.
     */
    public abstract boolean isSetSessionManagement();

    /**
     * @return Returns the clientstorage.
     */
    public abstract String getClientstorage();

    /**
     * @return if application context has a name
     */
    public abstract boolean hasName();
    
    /**
     * @return return script protect setting
     */
    public int getScriptProtect();

    
    public Mapping[] getMappings();
    
    public Mapping[] getCustomTagMappings();
    

	public String getSecureJsonPrefix() ;

	public boolean getSecureJson();

	// FUTURE deprecated use instead getDefDataSource()
	public String getDefaultDataSource();
	
	public boolean isORMEnabled();

	// FUTURE deprecated use instead getDefaultDataSource()
	public String getORMDatasource();

	public ORMConfiguration getORMConfiguration();
	
	public Properties getS3();
	
	public int getLocalMode();
	
	public String getSessionstorage();

	public TimeSpan getClientTimeout();
	
	public short getSessionType();

	public boolean getSessionCluster();
	
	public String getSessionClusterKey();

	public boolean getClientCluster();

	public Mapping[] getComponentMappings();
	
	
	
	
	
	

	public void setApplicationTimeout(TimeSpan applicationTimeout);
	public void setSessionTimeout(TimeSpan sessionTimeout);
	public void setClientTimeout(TimeSpan clientTimeout);
	public void setClientstorage(String clientstorage);
	public void setSessionstorage(String sessionstorage);
	public void setCustomTagMappings(Mapping[] customTagMappings);
	public void setComponentMappings(Mapping[] componentMappings);
	public void setMappings(Mapping[] mappings);
	public void setLoginStorage(int loginstorage);
	public void setDefaultDataSource(String datasource);
	public void setScriptProtect(int scriptrotect);
	public void setSecureJson(boolean secureJson);
	public void setSecureJsonPrefix(String secureJsonPrefix);
	public void setSetClientCookies(boolean setClientCookies);
	public void setSetClientManagement(boolean setClientManagement);
	public void setSetDomainCookies(boolean setDomainCookies);
	public void setSetSessionManagement(boolean setSessionManagement);
	public void setLocalMode(int localMode);
	public void setSessionType(short sessionType);
	public void setClientCluster(boolean clientCluster);
	public void setSessionCluster(boolean sessionCluster);
	public void setSessionClusterKey(String key);
	public void setS3(Properties s3);
	public void setORMEnabled(boolean ormenabled);
	public void setORMConfiguration(ORMConfiguration ormConf);
	public void setORMDatasource(String string);

	public String getSecurityApplicationToken();
	public String getSecurityCookieDomain();
	public int getSecurityIdleTimeout();
	public void setSecuritySettings(String applicationtoken,String cookiedomain, int idletimeout);
	
	public void reinitORM(PageContext pc) throws PageException ;

	public Resource getSource(); 
	


	public boolean getTriggerComponentDataMember();
	public void setTriggerComponentDataMember(boolean triggerComponentDataMember);

	/**
	 * return the default cache name for a certain type 
	 * @param type can be one of the following constants Config.CACHE_DEFAULT_OBJECT, Config.CACHE_DEFAULT_TEMPLATE, Config.CACHE_DEFAULT_QUERY, Config.CACHE_DEFAULT_RESOURCE, Config.CACHE_DEFAULT_FUNCTION
	 * @return name of the cache defined
	 */
	public String getDefaultCacheName(int type);
	
	public void setDefaultCacheName(int type, String cacheName);

	/**
	 * merge the field with same name to array if true, otherwise to a comma separated string list
	 * @param scope scope type, one of the following: Scope.SCOPE_FORM or Scope.SCOPE_URL
	 * @return
	 */
	public boolean getSameFieldAsArray(int scope);

	public RestSettings getRestSettings();
	
	public JavaSettings getJavaSettings();
	
	public Resource[] getRestCFCLocations();
}
