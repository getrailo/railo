package railo.runtime.config;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import railo.commons.io.log.LogAndSource;
import railo.commons.io.res.Resource;
import railo.commons.io.res.ResourceProvider;
import railo.runtime.CFMLFactory;
import railo.runtime.Mapping;
import railo.runtime.PageContext;
import railo.runtime.PageSource;
import railo.runtime.cache.CacheConnection;
import railo.runtime.cfx.CFXTagPool;
import railo.runtime.db.DataSource;
import railo.runtime.dump.DumpWriter;
import railo.runtime.engine.ThreadQueue;
import railo.runtime.exp.PageException;
import railo.runtime.extension.Extension;
import railo.runtime.extension.ExtensionProvider;
import railo.runtime.listener.ApplicationListener;
import railo.runtime.monitor.IntervallMonitor;
import railo.runtime.monitor.RequestMonitor;
import railo.runtime.net.mail.Server;
import railo.runtime.net.proxy.ProxyData;
import railo.runtime.rest.RestSettings;
import railo.runtime.schedule.Scheduler;
import railo.runtime.search.SearchEngine;
import railo.runtime.security.SecurityManager;
import railo.runtime.spooler.SpoolerEngine;
import railo.runtime.type.Struct;
import railo.runtime.type.dt.TimeSpan;

/**
 * interface for Config Object 
 */
public interface Config {

    /**
     * Define a strict scope cascading
     */
    public static final short SCOPE_STRICT = 0;

    /**
     * Define a small scope cascading
     */
    public static final short SCOPE_SMALL = 1;

    /**
     * Define a standart scope cascading (like other cf versions)
     */
    public static final short SCOPE_STANDARD = 2;

    /**
     * Field <code>CLIENT_SCOPE_TYPE_COOKIE</code>
     */
    public static final short CLIENT_SCOPE_TYPE_COOKIE = 0;

    /**
     * Field <code>CLIENT_SCOPE_TYPE_FILE</code>
     */
    public static final short CLIENT_SCOPE_TYPE_FILE = 1;

    /**
     * Field <code>CLIENT_SCOPE_TYPE_DB</code>
     */
    public static final short CLIENT_SCOPE_TYPE_DB = 2;

    /**
     * Field <code>SESSION_TYPE_CFML</code>
     */
    public static final short SESSION_TYPE_CFML = 0;

    /**
     * Field <code>SESSION_TYPE_J2EE</code>
     */
    public static final short SESSION_TYPE_J2EE = 1;
    

    /**
     * Field <code>RECOMPILE_NEVER</code>
     */
    public static final short RECOMPILE_NEVER = 0;
    /**
     * Field <code>RECOMPILE_AT_STARTUP</code>
     */
    public static final short RECOMPILE_AFTER_STARTUP = 1;
    /**
     * Field <code>RECOMPILE_ALWAYS</code>
     */
    public static final short RECOMPILE_ALWAYS = 2;
    

	public static final short INSPECT_ALWAYS = 0;
	public static final short INSPECT_ONCE = 1;
	public static final short INSPECT_NEVER = 2;
	// Hibernate Extension has hardcoded this 4, do not change!!!!

    /*public static final int CUSTOM_TAG_MODE_NONE = 0;
    public static final int CUSTOM_TAG_MODE_CLASSIC = 1;
    public static final int CUSTOM_TAG_MODE_MODERN = 2;
    public static final int CUSTOM_TAG_MODE_CLASSIC_MODERN = 4;
    public static final int CUSTOM_TAG_MODE_MODERN_CLASSIC = 8;
    */
	
	public static final int CACHE_DEFAULT_NONE = 0;
	public static final int CACHE_DEFAULT_OBJECT = 1;
	public static final int CACHE_DEFAULT_TEMPLATE = 2;
	public static final int CACHE_DEFAULT_QUERY = 4;
	public static final int CACHE_DEFAULT_RESOURCE = 8;
	public static final int CACHE_DEFAULT_FUNCTION = 16;
	public static final int CACHE_DEFAULT_INCLUDE = 32;

	
	
	public short getInspectTemplate();
    
	public String getDefaultDataSource();

    /**
     * return how railo cascade scopes
     * @return type of cascading
     */
    public abstract short getScopeCascadingType();

    /**
     * return cfml extesnion
     * @return cfml extension
     */
    public abstract String[] getCFMLExtensions();
    
    public abstract String getCFCExtension();

    /**
     * return the mapping to custom tag directory
     * @return custom tag directory
     */
    public abstract Mapping[] getCustomTagMappings();

    /**
     * return if it is allowed to implizid query call, call a query member witot define name of the query. 
     * @return is allowed
     */
    public abstract boolean allowImplicidQueryCall();

    /**e merged
     * return if url and form scope will b
     * @return merge or not
     */
    public abstract boolean mergeFormAndURL();
    

    /**
     * @return Returns the application Timeout.
     */
    public abstract TimeSpan getApplicationTimeout();

    /**
     * @return Returns the session Timeout.
     */
    public abstract TimeSpan getSessionTimeout();
    
    /**
     * @return Returns the client Timeout.
     */
    public TimeSpan getClientTimeout();

    /**
     * @return Returns the request Timeout.
     */
    public abstract TimeSpan getRequestTimeout();

    /**
     * @return Returns the clientCookies.
     */
    public abstract boolean isClientCookies();

    /**
     * @return Returns the clientManagement.
     */
    public abstract boolean isClientManagement();

    /**
     * @return Returns the domainCookies.
     */
    public abstract boolean isDomainCookies();

    /**
     * @return Returns the sessionManagement.
     */
    public abstract boolean isSessionManagement();

    /**
     * @return Returns the spoolEnable.
     */
    public abstract boolean isMailSpoolEnable();

    /**
     * @return Returns the mailTimeout.
     */
    public abstract int getMailTimeout();

    /**
     * @return preserve single quotes in cfquery tag or not
     */
    public abstract boolean getPSQL();

    /**
     * @return Returns the locale.
     */
    public abstract Locale getLocale();

    /**
     * return if debug output will be generated
     * @return debug or not
     */
    public abstract boolean debug();

    /**
     * return the temp directory
     * @return temp directory
     */
    public abstract Resource getTempDirectory();

    /**
     * @return Returns the spoolInterval.
     */
    public abstract int getMailSpoolInterval();

    /**
     * @return returns the time zone for this 
     */
    public abstract TimeZone getTimeZone();

    /**
     * @return returns the offset from the timeserver to local time 
     */
    public abstract long getTimeServerOffset();

    /**
     * @return return if a password is set
     */
    public abstract boolean hasPassword();

    /**
     * @param password 
     * @return return if a password is set
     */
    public abstract boolean passwordEqual(String password);

    /**
     * @return return if a password is set
     */
    public abstract boolean hasServerPassword();

    /**
     * @return Returns the mappings.
     */
    public abstract Mapping[] getMappings();

    /**
     * @return Returns the configDir.
     */
    public abstract Resource getConfigDir();

    /**
     * @return Returns the configFile.
     */
    public abstract Resource getConfigFile();

    /**
     * @return Returns the loadTime.
     */
    public abstract long getLoadTime();

    /**
     * @return Returns the baseComponent.
     */
    public abstract String getBaseComponentTemplate();

    /**
     * @return returns the client type
     */
    public abstract short getClientType();

    /**
     * @return Returns the componentDataMemberDefaultAccess.
     */
    public abstract int getComponentDataMemberDefaultAccess();

    /**
     * @return Returns the timeServer.
     */
    public abstract String getTimeServer();

    /**
     * @return Returns the componentDump.
     */
    public abstract String getComponentDumpTemplate();
    
    /**
     * @return id of the config 
     */
    public abstract String getId();

    /**
     * @return Returns the debug Template.
     * @deprecated use instead <code>getDebugEntry(ip, defaultValue)</code>
     */
    public abstract String getDebugTemplate();

    /**
     * @return Returns the error Template for given status code.
     */
    public abstract String getErrorTemplate(int statusCode);

    /**
     * @return Returns the sessionType.
     */
    public abstract short getSessionType();

    /**
     * @return returns the charset for the response and request
     */
    public abstract String getWebCharset(); // FUTURE return Charset
    /**
     * @return returns the charset used to read cfml files
     */
    public abstract String getTemplateCharset(); // FUTURE return Charset
    /**
     * @return returns the charset used to read and write resources
     */
    public abstract String getResourceCharset(); // FUTURE return Charset

    /**
     * @return returns the default encoding for mail
     */
    public String getMailDefaultEncoding(); // FUTURE return Charset?
    
    /**
     * @return returns update type (auto or manual)
     */
    public abstract String getUpdateType();

    /**
     * @return returns URL for update
     */
    public abstract URL getUpdateLocation();

    /**
     * return directory, where railo deploy translated cfml classes (java and class files)
     * @return deploy directory
     */
    public abstract Resource getDeployDirectory();

    /**
     * @return Returns the rootDir.
     */
    public abstract Resource getRootDirectory();

    /**
     * @return Returns the accessor.
     */
    public abstract SecurityManager getSecurityManager();
   
    /**
     * @return Returns the cfxTagPool.
     * @throws PageException 
     */
    public abstract CFXTagPool getCFXTagPool() throws PageException;
    
    /**
     * @return returns the application logger
     */
    public abstract LogAndSource getApplicationLogger();  // FUTURE deprecated, use instead getLogger()


    /**
     * @return returns the exception logger
     */
    public abstract LogAndSource getExceptionLogger();  // FUTURE deprecated, use instead getLogger()

    /**
     * @return returns the trace logger
     */
    public abstract LogAndSource getTraceLogger();  // FUTURE deprecated, use instead getLogger()
    
    /**
     * @param password
     * @return ConfigServer
     * @throws PageException
     */ 
    public abstract ConfigServer getConfigServer(String password) throws PageException;
    
    /**
     * @return Returns the mailLogger.
     */
    public abstract LogAndSource getMailLogger(); // FUTURE deprecated, use instead getLogger()
    
    /**
     * @return Returns the request timeout Directory.
     */
    public LogAndSource getRequestTimeoutLogger();  // FUTURE deprecated, use instead getLogger()
    
    /**
     * @return returns schedule logger
     */
    public LogAndSource getScheduleLogger(); // FUTURE deprecated, use instead getLogger()
    
    /**
     * reload the time offset to a time server 
     */
    public void reloadTimeServerOffset();
    
    /**
     * reset config
     */
    public void reset();
    
    /**
     * @return return the search Storage
     */
    public SearchEngine getSearchEngine();
    
    /**
     * @return return the Scheduler
     */
    public Scheduler getScheduler();
    
    /**
     * @return return all defined Mail Servers
     */
    public Server[] getMailServers();

    /**
     * return the compile type of this context
     */
    public short getCompileType();
    
    /**
     * return the all datasources
    */
    public DataSource[] getDataSources();
    

	/**
	 * @param path get a resource that match this path
	 * @return resource matching path
	 */
	public Resource getResource(String path);


	/**
	 * return current application listener
	 * @return application listener
	 */
	public ApplicationListener getApplicationListener();
	

	/**
	 * @return the scriptProtect
	 */
	public int getScriptProtect();


	/**
	 * return default proxy setting password
	 * @return the password for proxy
	 */
	public ProxyData getProxyData();
	
	/**
	 * return if proxy is enabled or not
	 * @return is proxy enabled
	 */
	public boolean isProxyEnableFor(String host);
	
	/**
	 * @return the triggerComponentDataMember
	 */
	public boolean getTriggerComponentDataMember();
	
	public RestSettings getRestSetting();
	
	public abstract Resource getClientScopeDir();

	public abstract long getClientScopeDirSize();

	public abstract ClassLoader getRPCClassLoader(boolean reload) throws IOException;
	
	public Resource getCacheDir();

	public long getCacheDirSize();
	
	public Map<String,CacheConnection> getCacheConnections();
	
	/**
	 * get default cache connection for a specific type
	 * @param type default type, one of the following (CACHE_DEFAULT_NONE, CACHE_DEFAULT_OBJECT, CACHE_DEFAULT_TEMPLATE, CACHE_DEFAULT_QUERY, CACHE_DEFAULT_RESOURCE)
	 * @return matching Cache Connection
	 */
	public CacheConnection getCacheDefaultConnection(int type);

	/**
	 * get name of a default cache connection for a specific type
	 * @param type default type, one of the following (CACHE_DEFAULT_NONE, CACHE_DEFAULT_OBJECT, CACHE_DEFAULT_TEMPLATE, CACHE_DEFAULT_QUERY, CACHE_DEFAULT_RESOURCE)
	 * @return name of matching Cache Connection
	 */
	public String getCacheDefaultConnectionName(int type);
	
	/**
	 * returns the default DumpWriter  
	 * @param defaultType
	 * @return default DumpWriter
	 */
	public abstract DumpWriter getDefaultDumpWriter(int defaultType);

	/**
	 * returns the DumpWriter matching key
	 * @param key key for DumpWriter
	 * @param defaultType
	 * @return matching DumpWriter
	 * @throws PageException if there is no DumpWriter for this key
	 */
	public abstract DumpWriter getDumpWriter(String key,int defaultType) throws PageException;

	/**
	 * returns the DumpWriter matching key
	 * @param key key for DumpWriter
	 * @return matching DumpWriter
	 * @deprecated use instead <code>getDumpWriter(String key,int defaultType)</code>
	 * @throws PageException if there is no DumpWriter for this key
	 */
	public abstract DumpWriter getDumpWriter(String key) throws PageException;
	
	
	/**
	 * define if components has a "shadow" in the component variables scope or not.
	 * @return
	 */
	public abstract boolean useComponentShadow();
	

    /* *
     * return a database connection hold inside by a datasource definition
     * @param datasource definiti0on of the datasource
     * @param user username to connect
     * @param pass password to connect
     * @return datasource connnection
     * @throws PageException
     */
    //public DatasourceConnection getConnection(String datasource, String user, String pass) throws PageException;

    /* *
     * @return returns the ConnectionPool
     */

	public Mapping[] getComponentMappings();

	public abstract boolean doCustomTagDeepSearch();


	/**
	 * @return returns the error print writer stream
	 */
	public abstract PrintWriter getErrWriter();
	
	/**
	 * @return returns the out print writer stream
	 */
	public abstract PrintWriter getOutWriter();

	/**
	 * define if railo search in local directory for custom tags or not
	 * @return search in local dir?
	 */
	public abstract boolean doLocalCustomTag();
	
	public String[] getCustomTagExtensions();
	
	/**
	 * @return if error status code will be returned or not
	 */
	public boolean getErrorStatusCode();
	

	public abstract int getLocalMode();
	
	/**
	 * @return return the class defined for the cluster scope
	 */
	public Class getClusterClass();
	
	/**
	 * @return classloader of ths context
	 */
	public ClassLoader getClassLoader();
	
	// FUTURE @deprected use instead PageContext.getClassLoader(Resource[] reses);
	public ClassLoader getClassLoader(Resource[] reses) throws IOException;
	
	public Resource getExtensionDirectory();
	
	public ExtensionProvider[] getExtensionProviders();
	
	public Extension[] getExtensions();
	
	public PageSource getBaseComponentPageSource();
	
	public boolean allowRelPath();
	
	public Struct getConstants();
	
	public DataSource getDataSource(String datasource) throws PageException;
	
	public DataSource getDataSource(String datasource, DataSource defaultValue);
	
	public Map getDataSourcesAsMap();
	
	public String getDefaultEncoding();
	
	public ResourceProvider getDefaultResourceProvider();
	
	public CFMLFactory getFactory();
	
	public boolean isExtensionEnabled();

	public Resource getFldFile();

	/**
	 * @return the tldFile
	 */
	public Resource getTldFile();
	
	/**
	 * get PageSource of the first Mapping that match the given criteria
	 * @param mappings per application mappings
	 * @param relPath path to get PageSource for
	 * @param onlyTopLevel checks only toplevel mappings
	 * @deprecated use instead getPageSources or getPageSourceExisting
	 */
	public PageSource getPageSource(Mapping[] mappings, String relPath,boolean onlyTopLevel);
	
	/**
	 * return existing PageSource that match the given criteria, if there is no PageSource null is returned.
	 * @param pc current PageContext
	 * @param mappings per application mappings
	 * @param relPath path to get PageSource for
	 * @param onlyTopLevel checks only toplevel mappings
	 * @param useSpecialMappings invoke special mappings like "mapping-tag" or "mapping-customtag"
	 * @param useDefaultMapping also invoke the always existing default mapping "/"
	 */
	public PageSource getPageSourceExisting(PageContext pc,Mapping[] mappings, String relPath,boolean onlyTopLevel,boolean useSpecialMappings, boolean useDefaultMapping, boolean onlyPhysicalExisting);
	
	/**
	 * get all PageSources that match the given criteria
	 * @param pc current PageContext
	 * @param mappings per application mappings
	 * @param relPath path to get PageSource for
	 * @param onlyTopLevel checks only toplevel mappings
	 * @param useSpecialMappings invoke special mappings like "mapping-tag" or "mapping-customtag"
	 * @param useDefaultMapping also invoke the always existing default mapping "/"
	 */
	public PageSource[] getPageSources(PageContext pc,Mapping[] mappings, String relPath,boolean onlyTopLevel,boolean useSpecialMappings, boolean useDefaultMapping); // FUTURE add boolean useComponentMappings
	
	/**
	 * get Resource of the first Mapping that match the given criteria
	 * @param mappings per application mappings
	 * @param relPath path to get PageSource for
	 * @param onlyTopLevel checks only toplevel mappings
	 * @deprecated use instead getPhysicalResources or getPhysicalResourceExisting
	 */
	public Resource getPhysical(Mapping[] mappings, String relPath, boolean alsoDefaultMapping);
    
	/**
	 * get all Resources that match the given criteria
	 * @param pc current PageContext
	 * @param mappings per application mappings
	 * @param relPath path to get PageSource for
	 * @param onlyTopLevel checks only toplevel mappings
	 * @param useSpecialMappings invoke special mappings like "mapping-tag" or "mapping-customtag"
	 * @param useDefaultMapping also invoke the always existing default mapping "/"
	 */
	public Resource[] getPhysicalResources(PageContext pc,Mapping[] mappings, String relPath,boolean onlyTopLevel,boolean useSpecialMappings, boolean useDefaultMapping);
	
	/**
	 * return existing Resource that match the given criteria, if there is no Resource null is returned.
	 * @param pc current PageContext
	 * @param mappings per application mappings
	 * @param relPath path to get Resource for
	 * @param onlyTopLevel checks only toplevel mappings
	 * @param useSpecialMappings invoke special mappings like "mapping-tag" or "mapping-customtag"
	 * @param useDefaultMapping also invoke the always existing default mapping "/"
	 */
	public Resource getPhysicalResourceExisting(PageContext pc,Mapping[] mappings, String relPath,boolean onlyTopLevel,boolean useSpecialMappings, boolean useDefaultMapping);
	    
    
    public Resource getRemoteClientDirectory();
    
	public LogAndSource getRemoteClientLog(); // FUTURE deprecated, use instead getLogger()
	
	public RemoteClient[] getRemoteClients();
	
	public SpoolerEngine getSpoolerEngine();
	
	public ResourceProvider[] getResourceProviders();
	
	public double getVersion();
	
	public Resource getVideoDirectory();
	
	//public String getVideoProviderLocation();
	
	public boolean isShowVersion();
	
	public boolean isSuppressWhitespace();
	
	//public boolean isVideoAgreementAccepted();
	
	public Struct getRemoteClientUsage();
	
	public Class getAdminSyncClass();
	
	public Class getVideoExecuterClass();
	
	public ThreadQueue getThreadQueue();
	
	public boolean getSessionCluster();

	public boolean getClientCluster();
	
	public Resource getSecurityDirectory();
	
	public boolean isMonitoringEnabled();
	
	public RequestMonitor[] getRequestMonitors();
	
	public RequestMonitor getRequestMonitor(String name) throws PageException;
	
	public IntervallMonitor[] getIntervallMonitors();

	public IntervallMonitor getIntervallMonitor(String name) throws PageException;
	
	/**
	 * if free permspace gen is lower than 10000000 bytes, railo shrinks all classloaders 
	 * @param cs
	 */
    public void checkPermGenSpace(boolean check);
    
    public boolean allowRequestTimeout();
}