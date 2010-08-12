package railo.runtime.config;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TimeZone;

import javax.servlet.ServletException;

import org.apache.commons.collections.map.ReferenceMap;

import railo.commons.collections.HashTable;
import railo.commons.io.SystemUtil;
import railo.commons.io.log.Log;
import railo.commons.io.log.LogAndSource;
import railo.commons.io.log.LogAndSourceImpl;
import railo.commons.io.log.LogConsole;
import railo.commons.io.res.Resource;
import railo.commons.io.res.ResourceProvider;
import railo.commons.io.res.Resources;
import railo.commons.io.res.ResourcesImpl;
import railo.commons.io.res.filter.ExtensionResourceFilter;
import railo.commons.io.res.util.ResourceClassLoaderFactory;
import railo.commons.io.res.util.ResourceUtil;
import railo.commons.lang.ClassException;
import railo.commons.lang.ClassUtil;
import railo.commons.lang.Md5;
import railo.commons.lang.PhysicalClassLoader;
import railo.commons.lang.StringUtil;
import railo.commons.lang.SystemOut;
import railo.loader.TP;
import railo.loader.engine.CFMLEngine;
import railo.loader.engine.CFMLEngineFactory;
import railo.loader.util.ExtensionFilter;
import railo.runtime.CFMLFactory;
import railo.runtime.Component;
import railo.runtime.Mapping;
import railo.runtime.MappingImpl;
import railo.runtime.Page;
import railo.runtime.PageContext;
import railo.runtime.PageContextImpl;
import railo.runtime.PageSource;
import railo.runtime.PageSourceImpl;
import railo.runtime.cache.CacheConnection;
import railo.runtime.cfx.CFXTagPool;
import railo.runtime.cfx.customtag.CFXTagPoolImpl;
import railo.runtime.component.ImportDefintion;
import railo.runtime.db.DataSource;
import railo.runtime.db.DatasourceConnectionPool;
import railo.runtime.dump.DumpWriter;
import railo.runtime.dump.DumpWriterEntry;
import railo.runtime.dump.HTMLDumpWriter;
import railo.runtime.engine.ExecutionLogFactory;
import railo.runtime.engine.ThreadLocalPageContext;
import railo.runtime.exp.ApplicationException;
import railo.runtime.exp.DatabaseException;
import railo.runtime.exp.DeprecatedException;
import railo.runtime.exp.ExpressionException;
import railo.runtime.exp.PageException;
import railo.runtime.exp.PageRuntimeException;
import railo.runtime.exp.SecurityException;
import railo.runtime.extension.Extension;
import railo.runtime.extension.ExtensionProvider;
import railo.runtime.extension.ExtensionProviderImpl;
import railo.runtime.gateway.GatewayEngineImpl;
import railo.runtime.listener.ApplicationListener;
import railo.runtime.net.amf.AMFCaster;
import railo.runtime.net.amf.ClassicAMFCaster;
import railo.runtime.net.amf.ModernAMFCaster;
import railo.runtime.net.mail.Server;
import railo.runtime.net.ntp.NtpClient;
import railo.runtime.op.Caster;
import railo.runtime.orm.ORMConfiguration;
import railo.runtime.orm.ORMEngine;
import railo.runtime.schedule.Scheduler;
import railo.runtime.schedule.SchedulerImpl;
import railo.runtime.search.SearchEngine;
import railo.runtime.security.SecurityManager;
import railo.runtime.spooler.SpoolerEngine;
import railo.runtime.type.Struct;
import railo.runtime.type.StructImpl;
import railo.runtime.type.dt.TimeSpan;
import railo.runtime.type.dt.TimeSpanImpl;
import railo.runtime.type.scope.ClusterNotSupported;
import railo.runtime.type.scope.Undefined;
import railo.runtime.util.ApplicationContext;
import railo.runtime.video.VideoExecuterNotSupported;
import railo.transformer.library.function.FunctionLib;
import railo.transformer.library.function.FunctionLibException;
import railo.transformer.library.function.FunctionLibFactory;
import railo.transformer.library.function.FunctionLibFunction;
import railo.transformer.library.function.FunctionLibFunctionArg;
import railo.transformer.library.tag.TagLib;
import railo.transformer.library.tag.TagLibException;
import railo.transformer.library.tag.TagLibFactory;
import railo.transformer.library.tag.TagLibTag;
import railo.transformer.library.tag.TagLibTagAttr;
import flex.messaging.config.ConfigMap;


/**
 * Hold the definitions of the railo cold fusion configuration.
 */
public abstract class ConfigImpl implements Config {



	public static final short INSPECT_ALWAYS = 0;
	public static final short INSPECT_ONCE = 1;
	public static final short INSPECT_NEVER = 2;

	public static final int CLIENT_BOOLEAN_TRUE = 0;
	public static final int CLIENT_BOOLEAN_FALSE = 1;
	public static final int SERVER_BOOLEAN_TRUE = 2;
	public static final int SERVER_BOOLEAN_FALSE = 3;
	public static final ExtensionProvider[] RAILO_EXTENSION_PROVIDERS = new ExtensionProviderImpl[]{
		new ExtensionProviderImpl("http://www.getrailo.com/ExtensionProvider.cfc",true),
		new ExtensionProviderImpl("http://www.getrailo.org/ExtensionProvider.cfc",true)
	};
	private static final Extension[] EXTENSIONS_EMPTY = new Extension[0];
	public static final int CACHE_DEFAULT_NONE = 0;
	public static final int CACHE_DEFAULT_OBJECT = 1;
	public static final int CACHE_DEFAULT_TEMPLATE = 2;
	public static final int CACHE_DEFAULT_QUERY = 4;
	public static final int CACHE_DEFAULT_RESOURCE = 8;
	

	private PhysicalClassLoader rpcClassLoader;
	private Map datasources=new HashTable();
	private Map caches=new HashTable();
	
	private CacheConnection defaultCacheObject=null;
	private CacheConnection defaultCacheTemplate=null;
	private CacheConnection defaultCacheQuery=null;
	private CacheConnection defaultCacheResource=null;
	
	private String cacheDefaultConnectionNameObject=null;
	private String cacheDefaultConnectionNameTemplate=null;
	private String cacheDefaultConnectionNameQuery=null;
	private String cacheDefaultConnectionNameResource=null;
	
    private TagLib[] tlds=new TagLib[1];
    private FunctionLib[] flds=new FunctionLib[1];
    private FunctionLib combinedFLDs;

    private short type=SCOPE_STANDARD;
    //private File deployDirectory;
    private boolean _allowImplicidQueryCall=true;
    private boolean _mergeFormAndURL=false;

    private int _debug;
    
    private boolean suppresswhitespace = false;
    private boolean showVersion = true;
    
	private Resource tempDirectory;
    private TimeSpan clientTimeout=new TimeSpanImpl(90,0,0,0);
    private TimeSpan sessionTimeout=new TimeSpanImpl(0,0,30,0);
    private TimeSpan applicationTimeout=new TimeSpanImpl(1,0,0,0);
    private TimeSpan requestTimeout=new TimeSpanImpl(0,0,0,30);
    
    private boolean sessionManagement=true;  
    private boolean clientManagement=false;
    private boolean clientCookies=true; 
    private boolean domainCookies=false;

    private Resource configFile;
    private Resource configDir;

    private long loadTime;

    private int spoolInterval=30;
    private boolean spoolEnable=true;

    private Server[] mailServers;

    private int mailTimeout=30;

    private TimeZone timeZone;

    private String timeServer="";
    private boolean useTimeServer=true;

    private long timeOffset;
    
    //private ConnectionPool conns;

    private SearchEngine searchEngine;

    private Locale locale;

    private boolean psq=true;

    private String debugTemplate;
    private Map errorTemplates=new HashMap();

    private String password;

    private Mapping[] mappings=new Mapping[0];
    private Mapping[] customTagMappings=new Mapping[0];
    private Mapping[] componentMappings=new Mapping[0];
    
    
	private Map<String,Mapping> customTagAppMappings=new ReferenceMap(ReferenceMap.SOFT,ReferenceMap.SOFT);

    private SchedulerImpl scheduler;
    
    private CFXTagPool cfxTagPool;

    private PageSource baseComponentPageSource;
    //private Page baseComponentPage;
    private String baseComponentTemplate;
    
    
    private LogAndSource mailLogger=null;//new LogAndSourceImpl(LogConsole.getInstance(Log.LEVEL_ERROR),"");
    private LogAndSource gatewayLogger=null;//new LogAndSourceImpl(LogConsole.getInstance(Log.LEVEL_INFO),"");
    private LogAndSource threadLogger=null;//new LogAndSourceImpl(LogConsole.getInstance(Log.LEVEL_INFO),"");
    
    private LogAndSource requestTimeoutLogger=null;
    private LogAndSource applicationLogger=null;
    private LogAndSource exceptionLogger=null;
	private LogAndSource traceLogger=null;

    
    private short clientType=CLIENT_SCOPE_TYPE_COOKIE;
    
    private String componentDumpTemplate;
    private int componentDataMemberDefaultAccess=Component.ACCESS_PRIVATE;
    private boolean triggerComponentDataMember=false;
    
    
    private short sessionType=SESSION_TYPE_CFML;

    //private EmailSpooler emailSpooler;

    
    private Resource deployDirectory;

    private short compileType=RECOMPILE_NEVER;
    
    private String resourceCharset=SystemUtil.getCharset();
    private String templateCharset=SystemUtil.getCharset();
    private String webCharset="UTF-8";

	private String mailDefaultEncoding = "UTF-8";
	
	private Resource tldFile;
	private Resource fldFile;

	private Resources resources=new ResourcesImpl();

	private ApplicationListener applicationListener;
	
	private int scriptProtect=ApplicationContext.SCRIPT_PROTECT_ALL;

	//private boolean proxyEnable=false;
	private String 	proxyServer=null;
	private int 	proxyPort=80;
	private String 	proxyUsername=null;
	private String 	proxyPassword=null;


	private Resource clientScopeDir;
	private long clientScopeDirSize=1024*1024*10;

	private Resource cacheDir;
	private long cacheDirSize=1024*1024*10;


	private boolean useComponentShadow=true;

	
	private PrintWriter out=SystemUtil.PRINTWRITER_OUT;
	private PrintWriter err=SystemUtil.PRINTWRITER_ERR;

	private DatasourceConnectionPool pool=new DatasourceConnectionPool();

	private boolean doCustomTagDeepSearch=false;

	private double version=1.0D;

	private boolean closeConnection=false;
	private boolean contentLength=true;

	private boolean doLocalCustomTag=true; 

	private Struct constants=null;

	private RemoteClient[] remoteClients;

	private SpoolerEngine remoteClientSpoolerEngine;

	private Resource remoteClientDirectory;

	private LogAndSource remoteClientLog;
    
	private boolean allowURLRequestTimeout=false;
	private CFMLFactory factory;
	private boolean errorStatusCode=true;
	private int localMode=Undefined.MODE_LOCAL_OR_ARGUMENTS_ONLY_WHEN_EXISTS;
	
	private String id;
	private String securityToken;
	private String securityKey;
	private ExtensionProvider[] extensionProviders=RAILO_EXTENSION_PROVIDERS;
	private Extension[] extensions=EXTENSIONS_EMPTY;
	private boolean extensionEnabled;
	private boolean allowRealPath=true;
	//private ClassLoader classLoader;

	private DumpWriterEntry[] dmpWriterEntries;
	private Class clusterClass=ClusterNotSupported.class;//ClusterRemoteNotSupported.class;//
	private Struct remoteClientUsage;
	private Class adminSyncClass=AdminSyncNotSupported.class;
	private AdminSync adminSync;
	private String[] customTagExtensions=new String[]{"cfm","cfc"};
	private Class videoExecuterClass=VideoExecuterNotSupported.class;
	
	protected MappingImpl tagMapping;
	private Resource tagDirectory;
	private Resource functionDirectory;
	protected MappingImpl functionMapping;
	private Map amfCasterArguments;
	private Class amfCasterClass=ClassicAMFCaster.class;
	private AMFCaster amfCaster;
	private String defaultDataSource;
	private short inspectTemplate=INSPECT_ONCE;
	private String serial="";
	private GatewayEngineImpl gatewayEngine;
	private String cacheMD5;
	private boolean executionLogEnabled;
	private ExecutionLogFactory executionLogFactory;
	//private int clientScopeMaxAge=90;
	private Map<String, ORMEngine> ormengines=new HashMap<String, ORMEngine>();
	private Class<ORMEngine> ormEngineClass;
	private ORMConfiguration ormConfig;
	private ResourceClassLoaderFactory classLoaderFactory;
	
	private ImportDefintion componentDefaultImport=new ImportDefintion("org.railo.cfml","*");
	private boolean componentLocalSearch=true;
	private boolean componentRootSearch=true;
	private LogAndSource mappingLogger;
	private LogAndSource ormLogger;

	
	
	/**
	 * @return the allowURLRequestTimeout
	 */
	public boolean isAllowURLRequestTimeout() {
		return allowURLRequestTimeout;
	}

	/**
	 * @param allowURLRequestTimeout the allowURLRequestTimeout to set
	 */
	public void setAllowURLRequestTimeout(boolean allowURLRequestTimeout) {
		this.allowURLRequestTimeout = allowURLRequestTimeout;
	}


    /**
     * @see railo.runtime.config.Config#getCompileType()
     */
    public short getCompileType() {
        return compileType;
    }

    /**
     * @see railo.runtime.config.Config#reset()
     */
    public void reset() {
    	timeServer="";
        componentDumpTemplate="";
        factory.resetPageContext();
        //resources.reset();
        ormengines.clear();
    }
    
    /**
     * @see railo.runtime.config.Config#reloadTimeServerOffset()
     */
    public void reloadTimeServerOffset() {
    	timeOffset=0;
        if(useTimeServer && !StringUtil.isEmpty(timeServer,true)) {
            NtpClient ntp=new NtpClient(timeServer);
            try {
                timeOffset=ntp.getOffset();
            } catch (IOException e) {
                timeOffset=0;
            }
        }
    }

    
    /**
     * private constructor called by factory method
     * @param configDir config directory
     * @param configFile config file
     * @param id 
     * @throws FunctionLibException 
     * @throws TagLibException 
     */
    protected ConfigImpl(CFMLFactory factory,Resource configDir, Resource configFile) {
        this(factory,configDir,configFile,
        		loadTLDs() , 
        		loadFLDs());
    }


    private static FunctionLib[] loadFLDs() {
		try {
			return new FunctionLib[]{FunctionLibFactory.loadFromSystem()};
		} catch (FunctionLibException e) {
			return new FunctionLib[]{};
		}
	}

	private static TagLib[] loadTLDs() {
		try {
			return new TagLib[]{TagLibFactory.loadFromSystem()};
		} catch (TagLibException e) {
			return new TagLib[]{};
		}
	}

	public ConfigImpl(CFMLFactory factory,Resource configDir, Resource configFile, TagLib[] tlds, FunctionLib[] flds) {
		
		this.configDir=configDir;
        this.configFile=configFile;
        this.factory=factory;
        
        this.tlds=duplicate(tlds,false);
        this.flds=duplicate(flds,false);
	}


	private static TagLib[] duplicate(TagLib[] tlds, boolean deepCopy) {
		TagLib[] rst = new TagLib[tlds.length];
		for(int i=0;i<tlds.length;i++){
			rst[i]=tlds[i].duplicate(deepCopy);
		}
		return rst;
	}
	private static FunctionLib[] duplicate(FunctionLib[] flds, boolean deepCopy) {
		FunctionLib[] rst = new FunctionLib[flds.length];
		for(int i=0;i<flds.length;i++){
			rst[i]=flds[i].duplicate(deepCopy);
		}
		return rst;
	}

	/**
     * @see railo.runtime.config.Config#getScopeCascadingType()
     */
    public short getScopeCascadingType() {
        return type;
    }
    
    /**
     * @see railo.runtime.config.Config#getCFMLExtension()
     */
    public String[] getCFMLExtensions() {
        return new String[]{"cfm","cfc"};
    }
    /**
     * @see railo.runtime.config.Config#getCFCExtension()
     */
    public String getCFCExtension() {
        return "cfc";
    }

    
    /**
     * return all Function Library Deskriptors
     * @return Array of Function Library Deskriptors
     */
    public FunctionLib[] getFLDs() {
        return flds;
    }
    
    public FunctionLib getCombinedFLDs() {
    	if(combinedFLDs==null)combinedFLDs=FunctionLibFactory.combineFLDs(flds);
        return combinedFLDs;
    }
    
    /**
     * return all Tag Library Deskriptors
     * @return Array of Tag Library Deskriptors
     */
    public TagLib[] getTLDs()  {
        return tlds;
    }
    
    /**
     * @see railo.runtime.config.Config#allowImplicidQueryCall()
     */
    public boolean allowImplicidQueryCall() {
        return _allowImplicidQueryCall;
    }

    /**
     * @see railo.runtime.config.Config#mergeFormAndURL()
     */
    public boolean mergeFormAndURL() {
        return _mergeFormAndURL;
    }
    
    /**
     * @see railo.runtime.config.Config#getApplicationTimeout()
     */
    public TimeSpan getApplicationTimeout() {
        return applicationTimeout;
    }

    /**
     * @see railo.runtime.config.Config#getSessionTimeout()
     */
    public TimeSpan getSessionTimeout() {
        return sessionTimeout;
    }

    /**
     * @see railo.runtime.config.Config#getSessionTimeout()
     */
    public TimeSpan getClientTimeout() {
        return clientTimeout;
    }
    
    /**
     * @see railo.runtime.config.Config#getRequestTimeout()
     */
    public TimeSpan getRequestTimeout() {
        return requestTimeout;
    }   
    
    /**
     * @see railo.runtime.config.Config#isClientCookies()
     */
    public boolean isClientCookies() {
        return clientCookies;
    }
    
    /**
     * @see railo.runtime.config.Config#isClientManagement()
     */
    public boolean isClientManagement() {
        return clientManagement;
    }
    
    /**
     * @see railo.runtime.config.Config#isDomainCookies()
     */
    public boolean isDomainCookies() {
        return domainCookies;
    }
    
    /**
     * @see railo.runtime.config.Config#isSessionManagement()
     */
    public boolean isSessionManagement() {
        return sessionManagement;
    }
    
    /**
     * @see railo.runtime.config.Config#isMailSpoolEnable()
     */
    public boolean isMailSpoolEnable() {
        //print.ln("isMailSpoolEnable:"+spoolEnable);
        return spoolEnable;
    }
    
    /**
     * @see railo.runtime.config.Config#getMailServers()
     */
    public Server[] getMailServers() {
        return mailServers;
    }
    
    /**
     * @see railo.runtime.config.Config#getMailTimeout()
     */
    public int getMailTimeout() {
        return mailTimeout;
    }   
    
    /**
     * @see railo.runtime.config.Config#getPSQL()
     */
    public boolean getPSQL() {
        return psq;   
    }

    /**
     * @see railo.runtime.config.Config#getClassLoader()
     */
    public ClassLoader getClassLoader() {
    	return classLoaderFactory.getResourceClassLoader();   
    }

    // FUTURE add to interface
    public ClassLoader getClassLoader(Resource[] reses) throws IOException {
    	return classLoaderFactory.getResourceClassLoader(reses);   
    }
    
	/* *
	 * @return the classLoaderFactory
	
	public ResourceClassLoaderFactory getClassLoaderFactory() {
		return classLoaderFactory;
	} */

	/**
	 * @param classLoaderFactory the classLoaderFactory to set
	 */
	protected void setClassLoaderFactory(ResourceClassLoaderFactory classLoaderFactory) {
		if(this.classLoaderFactory!=null){
			classLoaderFactory.reset();
		}
		this.classLoaderFactory = classLoaderFactory;
	}

    /**
     * @see railo.runtime.config.Config#getLocale()
     */
    public Locale getLocale() {
        return locale;
    }

    /**
     * @see railo.runtime.config.Config#debug()
     */
    public boolean debug() {
    	return _debug==CLIENT_BOOLEAN_TRUE || _debug==SERVER_BOOLEAN_TRUE;
    }
    public int intDebug() {
        return _debug;
    }
    
    /**
     * @see railo.runtime.config.Config#getTempDirectory()
     */
    public Resource getTempDirectory() {
        return tempDirectory;
    }
    
    /**
     * @see railo.runtime.config.Config#getMailSpoolInterval()
     */
    public int getMailSpoolInterval() {
        return spoolInterval;
    }

    /**
     * @see railo.runtime.config.Config#getMailLogger()
     */
    public LogAndSource getMailLogger() {
    	if(mailLogger==null)mailLogger=new LogAndSourceImpl(LogConsole.getInstance(this,Log.LEVEL_ERROR),"");
		return mailLogger;
    }

    /**
     * @see railo.runtime.config.Config#getMailLogger()
     */
    public LogAndSource getGatewayLogger() {
    	if(gatewayLogger==null)gatewayLogger=new LogAndSourceImpl(LogConsole.getInstance(this,Log.LEVEL_ERROR),"");
		return gatewayLogger;
    }


    public void setGatewayLogger(LogAndSource gatewayLogger) {
    	this.gatewayLogger=gatewayLogger;
    }

    /**
     * @see railo.runtime.config.Config#getMailLogger()
     */
    public LogAndSource getThreadLogger() {
    	if(threadLogger==null)threadLogger=new LogAndSourceImpl(LogConsole.getInstance(this,Log.LEVEL_ERROR),"");
		return threadLogger;
    }


    public void setThreadLogger(LogAndSource threadLogger) {
    	this.threadLogger=threadLogger;
    }
    
    /**
     * @see railo.runtime.config.Config#getRequestTimeoutLogger()
     */
    public LogAndSource getRequestTimeoutLogger() {
    	if(requestTimeoutLogger==null)requestTimeoutLogger=new LogAndSourceImpl(LogConsole.getInstance(this,Log.LEVEL_ERROR),"");
		return requestTimeoutLogger;
    }

    /**
     * @see railo.runtime.config.Config#getTimeZone()
     */
    public TimeZone getTimeZone() {
        return timeZone;
    }
    
    /**
     * @see railo.runtime.config.Config#getTimeServerOffset()
     */
    public long getTimeServerOffset() {
        return timeOffset;
    }
    
    /**
     * @see railo.runtime.config.Config#getSearchEngine()
     */
    public SearchEngine getSearchEngine() {
        return searchEngine;
    }
    
    /**
     * @return return the Scheduler
     */
    public Scheduler getScheduler() {
        return scheduler;
    }

    /**
     * @return gets the password
     */
    protected String getPassword() {
        return password;
    }
    
    /**
     * @see railo.runtime.config.Config#hasPassword()
     */
    public boolean hasPassword() {
        return password!=null && password.length()>0;
    }
    
    /**
     * @see railo.runtime.config.Config#passwordEqual(java.lang.String)
     */
    public boolean passwordEqual(String password) {
        return this.password.equals(password);
    }
    
    /**
     * @see railo.runtime.config.Config#hasServerPassword()
     */
    public boolean hasServerPassword() {
        return getConfigServerImpl().hasPassword();
    }

    /**
     * @see railo.runtime.config.Config#getMappings()
     */
    public Mapping[] getMappings() {
        return mappings;
    }


    public PageSource getPageSource(Mapping[] mappings, String realPath,boolean onlyTopLevel) {
    	return getPageSource(mappings, realPath, onlyTopLevel, ((PageContextImpl)ThreadLocalPageContext.get()).useSpecialMappings());
    }
    
    public PageSource getPageSource(Mapping[] mappings, String realPath,boolean onlyTopLevel,boolean useSpecialMappings) {
        realPath=realPath.replace('\\','/');
        
        String lcRealPath = StringUtil.toLowerCase(realPath)+'/';
        Mapping mapping;
        
        // app.cfc mappings
        if(mappings!=null){
	        for(int i=0;i<mappings.length;i++) {
	            mapping = mappings[i];
	            //print.err(lcRealPath+".startsWith"+(mapping.getStrPhysical()));
	            if(lcRealPath.startsWith(mapping.getVirtualLowerCaseWithSlash(),0)) {
	            	return mapping.getPageSource(realPath.substring(mapping.getVirtual().length()));
	            }
	        }
        }
        
        /// special mappings
        if(useSpecialMappings && lcRealPath.startsWith("/mapping-",0)){
        	PageSource ps;
        	String virtual="/mapping-tag";
        	// tag mappings
        	Mapping[] tagMappings=(this instanceof ConfigWebImpl)?new Mapping[]{((ConfigWebImpl)this).getServerTagMapping(),getTagMapping()}:new Mapping[]{getTagMapping()};
        	if(lcRealPath.startsWith(virtual,0)){
	        	for(int i=0;i<tagMappings.length;i++) {
		            mapping = tagMappings[i];
		            //if(lcRealPath.startsWith(mapping.getVirtualLowerCaseWithSlash(),0)) {
		            	ps = mapping.getPageSource(realPath.substring(virtual.length()));
		            	if(ps.exists()) return ps;
		            //}
		        }
        	}
        	
        	// customtag mappings
        	tagMappings=getCustomTagMappings();
        	virtual="/mapping-customtag";
        	if(lcRealPath.startsWith(virtual,0)){
	        	for(int i=0;i<tagMappings.length;i++) {
		            mapping = tagMappings[i];
		            //if(lcRealPath.startsWith(mapping.getVirtualLowerCaseWithSlash(),0)) {
		            	ps = mapping.getPageSource(realPath.substring(virtual.length()));
		            	if(ps.exists()) return ps;
		            //}
		        }
        	}
        }
        
        
        // config mappings
        for(int i=0;i<this.mappings.length-1;i++) {
            mapping = this.mappings[i];
            if((!onlyTopLevel || mapping.isTopLevel()) && lcRealPath.startsWith(mapping.getVirtualLowerCaseWithSlash(),0)) {
            	return mapping.getPageSource(realPath.substring(mapping.getVirtual().length()));
            }
        }
        
        return this.mappings[this.mappings.length-1].getPageSource(realPath);
    }
    
    /**
     * @param mappings2 
     * @param realPath
     * @param alsoDefaultMapping ignore default mapping (/) or not
     * @return physical path from mapping
     */
    public Resource getPhysical(Mapping[] mappings, String realPath, boolean alsoDefaultMapping) {
        realPath=realPath.replace('\\','/');
        String lcRealPath = StringUtil.toLowerCase(realPath);
        if(!StringUtil.endsWith(lcRealPath,'/'))lcRealPath+='/';
        Mapping mapping;
        //print.out(realPath);
        
        // app.cfc mappings
        if(mappings!=null){        	
	        for(int i=0;i<mappings.length;i++) {
	            mapping = mappings[i];
	            if(lcRealPath.startsWith(mapping.getVirtualLowerCaseWithSlash(),0) && mapping.hasPhysical()) {
	            	return mapping.getPhysical().getRealResource(realPath.substring(mapping.getVirtual().length()));
	            }
	        }
	    }
        
        // config mappings
        for(int i=0;i<this.mappings.length-1;i++) {
            mapping = this.mappings[i];
            if(lcRealPath.startsWith(mapping.getVirtualLowerCaseWithSlash(),0) && mapping.hasPhysical()) {
            	return mapping.getPhysical().getRealResource(realPath.substring(mapping.getVirtual().length()));
            }
        }

        if(alsoDefaultMapping && this.mappings[this.mappings.length-1].hasPhysical())
        	return this.mappings[this.mappings.length-1].getPhysical().getRealResource(realPath);
        return null;
    }

    /**
     * @param mappings2 
     * @see railo.runtime.config.Config#toPageSource(railo.commons.io.res.Resource, railo.runtime.PageSource)
     */
    public PageSource toPageSource(Mapping[] mappings, Resource res,PageSource defaultValue) {
        Mapping mapping;
        Resource root;
        String path;
        
        // app.cfc mappings
        if(mappings!=null){
            for(int i=0;i<mappings.length;i++) {
                mapping = mappings[i];
                root=mapping.getPhysical();
                path=ResourceUtil.getPathToChild(res, root);
                if(path!=null) {
                	return mapping.getPageSource(path);
                }
            }
        }
        
        // config mappings
        for(int i=0;i<this.mappings.length;i++) {
            mapping = this.mappings[i];
            root=mapping.getPhysical();
            path=ResourceUtil.getPathToChild(res, root);
            //print.out(path+"==="+res+"="+root);
            if(path!=null) {
            	return mapping.getPageSource(path);
            }
        }
        
        // map resource to root mapping when same filesystem
        Mapping rootMapping = this.mappings[this.mappings.length-1];
        root=rootMapping.getPhysical();
        if(res.getResourceProvider().getScheme().equals(root.getResourceProvider().getScheme())){
        	String realpath="";
        	while(root!=null && !ResourceUtil.isChildOf(res, root)){
        		root=root.getParentResource();
        		realpath+="../";
        	}
        	String p2c=ResourceUtil.getPathToChild(res,root);
        	if(StringUtil.startsWith(p2c, '/') || StringUtil.startsWith(p2c, '\\') )
        		p2c=p2c.substring(1);
        	realpath+=p2c;
        	
        	return rootMapping.getPageSource(realpath);
        }
        
        return defaultValue;
    }
    
    /**
     * @see railo.runtime.config.Config#getConfigDir()
     */
    public Resource getConfigDir() {
        return configDir;
    }
    
    /**
     * @see railo.runtime.config.Config#getConfigFile()
     */
    public Resource getConfigFile() {
        return configFile;
    }

    /**
     * @see railo.runtime.config.Config#getScheduleLogger()
     */
    public LogAndSource getScheduleLogger() {
        return scheduler.getLogger();
    }
    
    /**
     * @see railo.runtime.config.Config#getApplicationLogger()
     */
    public LogAndSource getApplicationLogger() {
    	if(applicationLogger==null)applicationLogger=new LogAndSourceImpl(LogConsole.getInstance(this,Log.LEVEL_ERROR),"");
		return applicationLogger;
    }

    /**
     * sets the password
     * @param password
     */
    protected void setPassword(String password) {
        this.password=password;
    }
    
    
    /**
     * set how railo cascade scopes
     * @param type cascading type
     */
    protected void setScopeCascadingType(String type) {
        
        if(type.equalsIgnoreCase("strict")) setScopeCascadingType(SCOPE_STRICT);
        else if(type.equalsIgnoreCase("small")) setScopeCascadingType(SCOPE_SMALL);
        else if(type.equalsIgnoreCase("standard"))setScopeCascadingType(SCOPE_STANDARD);
        else if(type.equalsIgnoreCase("standart"))setScopeCascadingType(SCOPE_STANDARD);
        else setScopeCascadingType(SCOPE_STANDARD);
    }

    /**
     * set how railo cascade scopes
     * @param type cascading type
     */
    protected void setScopeCascadingType(short type) {
        this.type=type;
    }

    protected void addTag(String nameSpace, String nameSpaceSeperator,String name, String clazz){
    	for(int i=0;i<tlds.length;i++) {
        	if(tlds[i].getNameSpaceAndSeparator().equalsIgnoreCase(nameSpace+nameSpaceSeperator)){
        		TagLibTag tlt = new TagLibTag(tlds[i]);
        		tlt.setAttributeType(TagLibTag.ATTRIBUTE_TYPE_DYNAMIC);
        		tlt.setBodyContent("free");
        		tlt.setTagClass(clazz);
        		tlt.setName(name);
        		tlds[i].setTag(tlt		);
        	}
        }
    }
    
    /**
     * set the optional directory of the tag library deskriptors
     * @param fileTld directory of the tag libray deskriptors
     * @throws TagLibException
     */
    protected void setTldFile(Resource fileTld) throws TagLibException {
    	if(fileTld==null) return;
    	this.tldFile=fileTld;
    	String key;
        Map map=new HashMap();
        // First fill existing to set
        for(int i=0;i<tlds.length;i++) {
        	key=getKey(tlds[i]);
        	map.put(key,tlds[i]);
        }
    	
        TagLib tl;
        
        // now overwrite with new data
        if(fileTld.isDirectory()) {
        	Resource[] files=fileTld.listResources(new ExtensionResourceFilter("tld"));
            for(int i=0;i<files.length;i++) {
                try {
                	tl = TagLibFactory.loadFromFile(files[i]);
                	key=getKey(tl);
                	if(!map.containsKey(key))
                		map.put(key,tl);
                	else 
                		overwrite((TagLib) map.get(key),tl);
                }
                catch(TagLibException tle) {
                    SystemOut.printDate(out,"can't load tld "+files[i]);
                    tle.printStackTrace(getErrWriter());
                }
                
            }
        }
        else if(fileTld.isFile()){
        	tl = TagLibFactory.loadFromFile(fileTld);
        	key=getKey(tl);
        	if(!map.containsKey(key))
        		map.put(key,tl);
        	else overwrite((TagLib) map.get(key),tl);
        }

        // now fill back to array
        tlds=new TagLib[map.size()];
        int index=0;
        Iterator it = map.entrySet().iterator();
        while(it.hasNext()) {
        	tlds[index++]=(TagLib) ((Map.Entry)it.next()).getValue();
        }
    }
    
    protected void setTagDirectory(Resource tagDirectory) {
    	this.tagDirectory=tagDirectory;
    	
    	this.tagMapping= new MappingImpl(this,"/mapping-tag/",tagDirectory.getAbsolutePath(),null,true,true,true,true,true);
    	
    	TagLib tl=null;
        for(int i=0;i<tlds.length;i++) {
        	// TODO get core taglib
        	if(tlds[i].getNameSpaceAndSeparator().equals("cf"))tl=tlds[i];	
        }
    	
        // now overwrite with new data
        if(tagDirectory.isDirectory()) {
        	String[] files=tagDirectory.list(new ExtensionResourceFilter(new String[]{"cfm","cfc"}));
            for(int i=0;i<files.length;i++) {
            	if(tl!=null)createTag(tl, files[i]);
                    
            }
        }
        
    }
    
    public void createTag(TagLib tl,String filename) {
    	String name=toName(filename);//filename.substring(0,filename.length()-(getCFCExtension().length()+1));xxx
        
    	TagLibTag tlt = new TagLibTag(tl);
        tlt.setName(name);
        tlt.setTagClass("railo.runtime.tag.CFTagCore");
        tlt.setHandleExceptions(true);
        tlt.setBodyContent("free");
        tlt.setParseBody(false);
        tlt.setDescription("");
        tlt.setAttributeType(TagLibTag.ATTRIBUTE_TYPE_MIXED);


        TagLibTagAttr tlta = new TagLibTagAttr(tlt);
        tlta.setName("__filename");
        tlta.setRequired(true);
        tlta.setRtexpr(true);
        tlta.setType("string");
        tlta.setHidden(true);
        tlta.setDefaultValue(filename);
        tlt.setAttribute(tlta);
        
        tlta = new TagLibTagAttr(tlt);
        tlta.setName("__name");
        tlta.setRequired(true);
        tlta.setRtexpr(true);
        tlta.setHidden(true);
        tlta.setType("string");
        tlta.setDefaultValue(name);
        tlt.setAttribute(tlta);
        
        tlta = new TagLibTagAttr(tlt);
        tlta.setName("__isweb");
        tlta.setRequired(true);
        tlta.setRtexpr(true);
        tlta.setHidden(true);
        tlta.setType("boolean");
        tlta.setDefaultValue(this instanceof ConfigWeb?"true":"false");
        tlt.setAttribute(tlta);
        
        tl.setTag(tlt);
    }
    
    protected void setFunctionDirectory(Resource functionDirectory) {
    	this.functionDirectory=functionDirectory;
    	this.functionMapping= new MappingImpl(this,"/mapping-function/",functionDirectory.getAbsolutePath(),null,true,true,true,true,true);
    	FunctionLib fl=flds[flds.length-1];
        
        // now overwrite with new data
        if(functionDirectory.isDirectory()) {
        	String[] files=functionDirectory.list(new ExtensionResourceFilter(getCFMLExtensions()));
        	
            for(int i=0;i<files.length;i++) {
            	if(fl!=null)createFunction(fl, files[i]);
                    
            }
        }
        
    }
    
    public void createFunction(FunctionLib fl,String filename) {
    	PageSource ps = functionMapping.getPageSource(filename);
    	
    	String name=toName(filename);//filename.substring(0,filename.length()-(getCFMLExtensions().length()+1));
        FunctionLibFunction flf = new FunctionLibFunction(fl);
    	flf.setArgType(FunctionLibFunction.ARG_DYNAMIC);
    	flf.setCls("railo.runtime.functions.system.CFFunction");
    	flf.setName(name);
    	flf.setReturn("object");
    	FunctionLibFunctionArg arg = new FunctionLibFunctionArg(flf);
        arg.setName("__filename");
        arg.setRequired(true);
        arg.setType("string");
        arg.setHidden(true);
        arg.setDefaultValue(filename);
        flf.setArg(arg);
        
        arg = new FunctionLibFunctionArg(flf);
        arg.setName("__name");
        arg.setRequired(true);
        arg.setHidden(true);
        arg.setType("string");
        arg.setDefaultValue(name);
        flf.setArg(arg);
        
        arg = new FunctionLibFunctionArg(flf);
        arg.setName("__isweb");
        arg.setRequired(true);
        arg.setHidden(true);
        arg.setType("boolean");
        arg.setDefaultValue(this instanceof ConfigWeb?"true":"false");
        flf.setArg(arg);
    	
    	
    	
    	fl.setFunction(flf);
    }
    
    
    
    
    
    private static String toName(String filename) {
    	int pos=filename.lastIndexOf('.');
        if(pos==-1)return filename;
        return filename.substring(0,pos);
	}
    

	private void overwrite(TagLib existingTL, TagLib newTL) {
		Iterator it = newTL.getTags().entrySet().iterator();
		while(it.hasNext()){
			existingTL.setTag((TagLibTag) (((Map.Entry)it.next()).getValue()));
		}
	}

	private String getKey(TagLib tl) {
		return tl.getNameSpaceAndSeparator().toLowerCase();
	}

	/**
     * set the optional directory of the function library deskriptors
     * @param fileFld directory of the function libray deskriptors
     * @throws FunctionLibException
     */
    protected void setFldFile(Resource fileFld) throws FunctionLibException {
    	if(fileFld==null) return;
        this.fldFile=fileFld;

        Map set=new HashMap();
        String key;
        // First fill existing to set
        for(int i=0;i<flds.length;i++) {
        	key=getKey(flds[i]);
        	set.put(key,flds[i]);
        }
        
        // now overwrite with new data
        FunctionLib fl;
        if(fileFld.isDirectory()) {
            Resource[] files=fileFld.listResources(new ExtensionResourceFilter("fld"));
            for(int i=0;i<files.length;i++) {
                try {
                	fl = FunctionLibFactory.loadFromFile(files[i]);
                	key=getKey(fl);
                	
                	if(!set.containsKey(key))
                		set.put(key,fl);
                	else 
                		overwrite((FunctionLib) set.get(key),fl);
                	
                }
                catch(FunctionLibException fle) {
                    SystemOut.printDate(out,"can't load tld "+files[i]);
                    fle.printStackTrace(getErrWriter());
                }   
            }
        }
        else {
        	fl = FunctionLibFactory.loadFromFile(fileFld);
        	key=getKey(fl);

        	if(!set.containsKey(key))
        		set.put(key,fl);
        	else 
        		overwrite((FunctionLib) set.get(key),fl);
        }
        
        // now fill back to array
        flds=new FunctionLib[set.size()];
        int index=0;
        Iterator it = set.entrySet().iterator();
        while(it.hasNext()) {
        	flds[index++]=(FunctionLib) ((Map.Entry)it.next()).getValue();
        	//print.ln(fld[index-1]);
        }
        
    }
    

    

    private void overwrite(FunctionLib existingFL, FunctionLib newFL) {
		Iterator it = newFL.getFunctions().entrySet().iterator();
		while(it.hasNext()){
			existingFL.setFunction((FunctionLibFunction) (((Map.Entry)it.next()).getValue()));
		}
	}

    private String getKey(FunctionLib functionLib) {
		return functionLib.getDisplayName().toLowerCase();
	}

	/**
     * sets if it is allowed to implizit query call, call a query member witot define name of the query. 
     * @param _allowImplicidQueryCall is allowed
     */
    protected void setAllowImplicidQueryCall(boolean _allowImplicidQueryCall) {
        this._allowImplicidQueryCall=_allowImplicidQueryCall;
    }

    /**
     * sets if url and form scope will be merged
     * @param _mergeFormAndURL merge yes or no
     */
    protected void setMergeFormAndURL(boolean _mergeFormAndURL) {
        this._mergeFormAndURL=_mergeFormAndURL;
    }
    
    /**
     * @param strApplicationTimeout The applicationTimeout to set.
     * @throws PageException
     */
    void setApplicationTimeout(String strApplicationTimeout) throws PageException {
        setApplicationTimeout(Caster.toTimespan(strApplicationTimeout));
    }
    
    /**
     * @param applicationTimeout The applicationTimeout to set.
     */
    protected void setApplicationTimeout(TimeSpan applicationTimeout) {
        this.applicationTimeout = applicationTimeout;
    }
    
    /**
     * @param strSessionTimeout The sessionTimeout to set.
     * @throws PageException
     */
    protected void setSessionTimeout(String strSessionTimeout) throws PageException {
        setSessionTimeout(Caster.toTimespan(strSessionTimeout));
    }
    
    /**
     * @param sessionTimeout The sessionTimeout to set.
     */
    protected void setSessionTimeout(TimeSpan sessionTimeout) {
        this.sessionTimeout = sessionTimeout;
    }
    
    protected void setClientTimeout(String strClientTimeout) throws PageException {
    	setClientTimeout(Caster.toTimespan(strClientTimeout));
    }
    
    /**
     * @param sessionTimeout The sessionTimeout to set.
     */
    protected void setClientTimeout(TimeSpan clientTimeout) {
        this.clientTimeout = clientTimeout;
    }
    
    /**
     * @param strRequestTimeout The requestTimeout to set.
     * @throws PageException
     */
    protected void setRequestTimeout(String strRequestTimeout) throws PageException {
        setRequestTimeout(Caster.toTimespan(strRequestTimeout));
    }
    
    /**
     * @param requestTimeout The requestTimeout to set.
     */
    protected void setRequestTimeout(TimeSpan requestTimeout) {
        this.requestTimeout = requestTimeout;
    }
    
    /**
     * @param clientCookies The clientCookies to set.
     */
    protected void setClientCookies(boolean clientCookies) {
        this.clientCookies = clientCookies;
    }
    
    /**
     * @param clientManagement The clientManagement to set.
     */
    protected void setClientManagement(boolean clientManagement) {
        this.clientManagement = clientManagement;
    }
    
    /**
     * @param domainCookies The domainCookies to set.
     */
    protected void setDomainCookies(boolean domainCookies) {
        this.domainCookies = domainCookies;
    }
    
    /**
     * @param sessionManagement The sessionManagement to set.
     */
    protected void setSessionManagement(boolean sessionManagement) {
        this.sessionManagement = sessionManagement;
    }
    
    /**
     * @param spoolEnable The spoolEnable to set.
     */
    protected void setMailSpoolEnable(boolean spoolEnable) {
        //print.ln("setMailSpoolEnable:"+spoolEnable);
        this.spoolEnable = spoolEnable;
    }
    
    /**
     * @param mailTimeout The mailTimeout to set.
     */
    protected void setMailTimeout(int mailTimeout) {
        this.mailTimeout = mailTimeout;
    }

    /**
     * sets the mail logger
     * @param mailLogger
     */
    protected void setMailLogger(LogAndSource mailLogger) {
        this.mailLogger = mailLogger;
    }
    

    protected void setORMLogger(LogAndSource ormLogger) {
        this.ormLogger = ormLogger;
    }
    public LogAndSource getORMLogger() {
    	if(ormLogger==null)ormLogger=new LogAndSourceImpl(LogConsole.getInstance(this,Log.LEVEL_ERROR),"");
		
        return ormLogger;
    }

    /**
     * sets the request timeout logger
     * @param requestTimeoutLogger
     */
    protected void setRequestTimeoutLogger(LogAndSource requestTimeoutLogger) {
        this.requestTimeoutLogger=requestTimeoutLogger;
    }
    
    /**
     * @param psq (preserve single quote) 
     * sets if sql string inside a cfquery will be prederved for Single Quotes
     */
    protected void setPSQL(boolean psq) {
        this.psq=psq;
    }
    
    /**
     * set if railo make debug output or not
     * @param _debug debug or not
     */
    protected void setDebug(int _debug) {
        this._debug=_debug;
    }   
    
    /**
     * sets the temp directory
     * @param strTempDirectory temp directory
     * @throws ExpressionException
     */
    protected void setTempDirectory(String strTempDirectory) throws ExpressionException {
        setTempDirectory(resources.getResource(strTempDirectory));
    }   
    
    /**
     * sets the temp directory
     * @param tempDirectory temp directory
     * @throws ExpressionException
     */
    protected void setTempDirectory(Resource tempDirectory) throws ExpressionException {
        if(!isDirectory(tempDirectory) || !tempDirectory.isWriteable()) {
        	SystemOut.printDate(getErrWriter(), "temp directory ["+tempDirectory+"] is not writable or can not be created, using directory ["+SystemUtil.getTempDirectory()+"] instead");
        	tempDirectory=SystemUtil.getTempDirectory();
        	if(!tempDirectory.isWriteable()){
        		SystemOut.printDate(getErrWriter(), "temp directory ["+tempDirectory+"] is not writable");
        	}
        }
        this.tempDirectory=tempDirectory;
    }

    /**
     * sets the Schedule Directory
     * @param scheduleDirectory sets the schedule Directory 
     * @param logger
     * @throws PageException
     */
    protected void setScheduler(CFMLEngine engine,Resource scheduleDirectory, LogAndSource logger) throws PageException {
        if(!isDirectory(scheduleDirectory)) throw new ExpressionException("schedule task directory "+scheduleDirectory+" doesn't exist or is not a directory");
        try {
        	if(this.scheduler==null)
        		this.scheduler=new SchedulerImpl(engine,this,scheduleDirectory,logger,SystemUtil.getCharset());
        	//else
        		//this.scheduler.reinit(scheduleDirectory,logger);
        } 
        catch (Exception e) {
            throw Caster.toPageException(e);
        }
    }
    
    /**
     * @param spoolInterval The spoolInterval to set.
     */
    protected void setMailSpoolInterval(int spoolInterval) {
        this.spoolInterval = spoolInterval;
    }
    
    /**
     * sets the timezone
     * @param timeZone
     */
    protected void setTimeZone(TimeZone timeZone) {
        this.timeZone=timeZone;
    }
    
    /**
     * sets the time server
     * @param timeServer
     */
    protected void setTimeServer(String timeServer) {
        this.timeServer=timeServer;
    }

    /**
     * sets the locale
     * @param strLocale
     */
    protected void setLocale(String strLocale) {
    	if(strLocale==null) {
            this.locale=Locale.US;
        }
        else {
            try {
                this.locale=Caster.toLocale(strLocale);
                if(this.locale==null)this.locale=Locale.US;
            } catch (ExpressionException e) {
                this.locale=Locale.US;
            }
        }
    }
    
    /**
     * sets the locale
     * @param locale
     */
    protected void setLocale(Locale locale) {
        this.locale=locale;
    }

    /**
     * @param mappings The mappings to set.
     */
    protected void setMappings(Mapping[] mappings) {
        Arrays.sort(mappings,new Comparator(){ 
            public int compare(Object left, Object right) { 
                Mapping r = ((Mapping)right);
            	Mapping l = ((Mapping)left);
            	int rtn=r.getVirtualLowerCaseWithSlash().length()-l.getVirtualLowerCaseWithSlash().length();
            	if(rtn==0) return slashCount(r)-slashCount(l);
            	return rtn; 
            }

			private int slashCount(Mapping l) {
				String str=l.getVirtualLowerCaseWithSlash();
				int count=0,lastIndex=-1;
				while((lastIndex=str.indexOf('/', lastIndex))!=-1) {
					count++;
					lastIndex++;
				}
				return count;
			} 
        }); 
        this.mappings = mappings;
    }    
    

    /**
     * @param datasources The datasources to set
     */
    protected void setDataSources(Map datasources) {
        this.datasources=datasources;
    }
    /**
     * @param customTagMapping The customTagMapping to set.
     */
    protected void setCustomTagMappings(Mapping[] customTagMappings) {
    	this.customTagMappings = customTagMappings;
    }
    

    /**
     * @see railo.runtime.config.Config#getCustomTagMappings()
     */
    public Mapping[] getCustomTagMappings() {
    	return customTagMappings;
    }
    
    /**
     * @param mailServers The mailsServers to set.
     */
    protected void setMailServers(Server[] mailServers) {
        this.mailServers = mailServers;
    }
    
    /**
     * is file a directory or not, touch if not exists
     * @param directory
     * @return true if existing directory or has created new one
     */
    protected boolean isDirectory(Resource directory) {
        if(directory.exists()) return directory.isDirectory();
        try {
			directory.createDirectory(true);
			return true;
		} catch (IOException e) {
			e.printStackTrace(getErrWriter());
		}
        return false;
    }

    /**
     * @see railo.runtime.config.Config#getLoadTime()
     */
    public long getLoadTime() {
        return loadTime;
    }
    /**
     * @param loadTime The loadTime to set.
     */
    protected void setLoadTime(long loadTime) {
        this.loadTime = loadTime;
    }
    /**
     * @return Returns the configLogger.
     * /
    public Log getConfigLogger() {
        return configLogger;
    }*/

    /**
     * @see railo.runtime.config.Config#getCFXTagPool()
     */
    public CFXTagPool getCFXTagPool() throws SecurityException {
        return cfxTagPool;
    }

    /**
     * @param cfxTagPool The customTagPool to set.
     */
    protected void setCFXTagPool(CFXTagPool cfxTagPool) {
        this.cfxTagPool = cfxTagPool;
    }
    /**
     * @param cfxTagPool The customTagPool to set.
     */
    protected void setCFXTagPool(Map cfxTagPool) {
        this.cfxTagPool = new CFXTagPoolImpl(cfxTagPool);
    }

    /**
     * @see railo.runtime.config.Config#getBaseComponentTemplate()
     */
    public String getBaseComponentTemplate() {
        return baseComponentTemplate;
    }

    /**
     * @return pagesource of the base component
     */
    public PageSource getBaseComponentPageSource() {
        if(baseComponentPageSource==null) {
            baseComponentPageSource=getPageSource(null,getBaseComponentTemplate(),false,false);
        }
        return baseComponentPageSource;
    }
    
    /**
     * @param template The baseComponent template to set.
     */
    protected void setBaseComponentTemplate(String template) {
        this.baseComponentPageSource=null;
        this.baseComponentTemplate = template;
    }
    
    /**
     * sets the application logger
     * @param applicationLogger
     */
    protected void setApplicationLogger(LogAndSource applicationLogger) {
        this.applicationLogger=applicationLogger;
    }


    protected void setMappingLogger(LogAndSource mappingLogger) {
        this.mappingLogger=mappingLogger;
    }

    public LogAndSource getMappingLogger() {
    	if(mappingLogger==null)
    		mappingLogger=new LogAndSourceImpl(LogConsole.getInstance(this,Log.LEVEL_ERROR),"");
		return mappingLogger;
    }
    
    /**
     * @param clientType
     */
    protected void setClientType(short clientType) {
        this.clientType=clientType;
    }
    
    /**
     * @param strClientType
     */
    protected void setClientType(String strClientType) {
        strClientType=strClientType.trim().toLowerCase();
        if(strClientType.equals("file"))clientType=Config.CLIENT_SCOPE_TYPE_FILE;
        else if(strClientType.equals("db"))clientType=Config.CLIENT_SCOPE_TYPE_DB;
        else if(strClientType.equals("database"))clientType=Config.CLIENT_SCOPE_TYPE_DB;
        else clientType=Config.CLIENT_SCOPE_TYPE_COOKIE;
    }
    
    /**
     * @see railo.runtime.config.Config#getClientType()
     */
    public short getClientType() {
        return this.clientType;
    }
    
    /**
     * @param searchEngine The searchEngine to set.
     */
    protected void setSearchEngine(SearchEngine searchEngine) {
        this.searchEngine = searchEngine;
    }

    /**
     * @see railo.runtime.config.Config#getComponentDataMemberDefaultAccess()
     */
    public int getComponentDataMemberDefaultAccess() {
        return componentDataMemberDefaultAccess;
    }
    /**
     * @param componentDataMemberDefaultAccess The componentDataMemberDefaultAccess to set.
     */
    protected void setComponentDataMemberDefaultAccess(
            int componentDataMemberDefaultAccess) {
        this.componentDataMemberDefaultAccess = componentDataMemberDefaultAccess;
    }

    
    /**
     * @see railo.runtime.config.Config#getTimeServer()
     */
    public String getTimeServer() {
        return timeServer;
    }

    /**
     * @see railo.runtime.config.Config#getComponentDumpTemplate()
     */
    public String getComponentDumpTemplate() {
        return componentDumpTemplate;
    }
    
    /**
     * @param template The componentDump template to set.
     */
    protected void setComponentDumpTemplate(String template) {
        this.componentDumpTemplate = template;
    }

    /**
     * @return Returns the configServer Implementation.
     */
    protected abstract ConfigServerImpl getConfigServerImpl();
    
    /**
     * @see railo.runtime.config.Config#getId()
     */
    


    public String getSecurityToken() {
    	if(securityToken==null){
    		try {
    			securityToken = Md5.getDigestAsString(getConfigDir().getAbsolutePath());
			} 
	    	catch (IOException e) {
				return null;
			}
    	}
    	return securityToken;
	}

    public String getId() {
    	if(id==null){
    		id = getId(getSecurityKey(),getSecurityToken(),securityKey);
    	}
    	return id;
	}

    public static String getId(String key, String token,String defaultValue) {
    	
		try {
			return Md5.getDigestAsString(key+token);
		} 
    	catch (IOException e) {
			return defaultValue;
		}
	}
    
    
    public String getSecurityKey() {
    	return securityKey;//getServletContext().getRealPath("/");
    }

    /**
     * @see railo.runtime.config.Config#getDebugTemplate()
     */
    public String getDebugTemplate() {
        return debugTemplate;
    }
    /**
     * @param debugTemplate The debugTemplate to set.
     */
    protected void setDebugTemplate(String debugTemplate) {
        this.debugTemplate = debugTemplate;
    }

	/**
	 * @return the errorTemplate
	 */
	public String getErrorTemplate() {
		return getErrorTemplate(500);
	}
	
	public String getErrorTemplate(int statusCode) {
		return (String) errorTemplates.get(Caster.toString(statusCode));
	}

	/**
	 * @param errorTemplate the errorTemplate to set
	 */
	protected void setErrorTemplate(int statusCode,String errorTemplate) {
		this.errorTemplates.put(Caster.toString(statusCode), errorTemplate);
	}

    /**
     * @see railo.runtime.config.Config#getSessionType()
     */
    public short getSessionType() {
        return sessionType;
    }
    /**
     * @param sessionType The sessionType to set.
     */
    protected void setSessionType(short sessionType) {
        this.sessionType = sessionType;
    }
    /**
     * @param type The sessionType to set.
     */
    protected void setSessionType(String type) {
        type=type.toLowerCase().trim();
        if(type.startsWith("cfm")) setSessionType(SESSION_TYPE_CFML);
        else if(type.startsWith("j")) setSessionType(SESSION_TYPE_J2EE);
        else setSessionType(SESSION_TYPE_CFML);
    }

    /**
     * @see railo.runtime.config.Config#getUpdateType()
     */
    public abstract String getUpdateType() ;

    /**
     * @see railo.runtime.config.Config#getUpdateLocation()
     */
    public abstract URL getUpdateLocation();

    /**
     * @see railo.runtime.config.Config#getDeployDirectory()
     */
    public Resource getDeployDirectory() {
    	return deployDirectory;
    }

    /**
     * set the deploy directory, directory where railo deploy transalted cfml classes (java and class files)
     * @param strDeployDirectory deploy directory
     * @throws ExpressionException
     */
    protected void setDeployDirectory(String strDeployDirectory) throws ExpressionException {
        setDeployDirectory(resources.getResource(strDeployDirectory));
    }
    
    /**
     * set the deploy directory, directory where railo deploy transalted cfml classes (java and class files)
     * @param deployDirectory deploy directory
     * @throws ExpressionException
     * @throws ExpressionException
     */
    protected void setDeployDirectory(Resource deployDirectory) throws ExpressionException {
    	if(!isDirectory(deployDirectory)) {
            throw new ExpressionException("deploy directory "+deployDirectory+" doesn't exist or is not a directory");
        }
    	this.deployDirectory=deployDirectory;
    }
    

    /**
     * @see railo.runtime.config.Config#getRootDirectory()
     */
    public abstract Resource getRootDirectory();

    /**
     * sets the compileType value.
     * @param compileType The compileType to set.
     */
    protected void setCompileType(short compileType) {
        this.compileType = compileType;
    }

    /** FUTHER
     * Returns the value of suppresswhitespace.
     * @return value suppresswhitespace
     */
    public boolean isSuppressWhitespace() {
        return suppresswhitespace;
    }

    /** FUTHER
     * sets the suppresswhitespace value.
     * @param suppresswhitespace The suppresswhitespace to set.
     */
    protected void setSuppressWhitespace(boolean suppresswhitespace) {
        this.suppresswhitespace = suppresswhitespace;
    }

	/**
	 * @see railo.runtime.config.Config#getDefaultEncoding()
	 */
	public String getDefaultEncoding() {
		return webCharset;
	}
	
	/**
	 *
	 * @see railo.runtime.config.Config#getTemplateCharset()
	 */
	public String getTemplateCharset() {
		return templateCharset;
	}
	
	/**
	 * sets the charset to read the files
	 * @param templateCharset
	 */
	protected void setTemplateCharset(String templateCharset) {
		this.templateCharset = templateCharset;
	}

	/**
	 *
	 * @see railo.runtime.config.Config#getWebCharset()
	 */
	public String getWebCharset() {
		return webCharset;
	}
	
	/**
	 * sets the charset to read and write resources
	 * @param resourceCharset
	 */
	protected void setResourceCharset(String resourceCharset) {
		this.resourceCharset = resourceCharset;
	}

	/**
	 *
	 * @see railo.runtime.config.Config#getResourceCharset()
	 */
	public String getResourceCharset() {
		return resourceCharset;
	}
	
	/**
	 * sets the charset for the response stream
	 * @param outputEncoding
	 */
	protected void setWebCharset(String webCharset) {
		this.webCharset = webCharset;
	}

	public SecurityManager getSecurityManager() {
		return null;
	}

	/**
	 * @return the fldFile
	 */
	public Resource getFldFile() {
		return fldFile;
	}

	/**
	 * @return the tldFile
	 */
	public Resource getTldFile() {
		return tldFile;
	}
    
    /**
	 * @see railo.runtime.config.Config#getDataSources()
	 */
	public DataSource[] getDataSources() {
		Map map = getDataSourcesAsMap();
		Iterator it = map.keySet().iterator();
		DataSource[] ds = new DataSource[map.size()];
		int count=0;
		
		while(it.hasNext()) {
			ds[count++]=(DataSource) map.get(it.next());
		}
		return ds;
	}
	
	public Map getDataSourcesAsMap() {
        Map map=new HashTable();
        Iterator it = datasources.keySet().iterator();
        
        while(it.hasNext()) {
            Object key=it.next();
            if(!key.equals("_queryofquerydb"))
                map.put(key,datasources.get(key));
        }        
        return map;
    }

	/**
	 * @return the mailDefaultCharset
	 */
	public String getMailDefaultEncoding() {
		return mailDefaultEncoding;
	}

	/**
	 * @param mailDefaultCharset the mailDefaultCharset to set
	 */
	protected void setMailDefaultEncoding(String mailDefaultEncoding) {
		this.mailDefaultEncoding = mailDefaultEncoding;
	}

	protected void setDefaultResourceProvider(String strDefaultProviderClass, Map arguments) throws ClassException {
		Object o=ClassUtil.loadInstance(strDefaultProviderClass);
		if(o instanceof ResourceProvider) {
			ResourceProvider rp=(ResourceProvider) o;
			rp.init(null,arguments);
			setDefaultResourceProvider(rp);
		}
		else 
			throw new ClassException("object ["+Caster.toClassName(o)+"] must implement the interface "+ResourceProvider.class.getName());
	}

	/**
	 * @param defaultResourceProvider the defaultResourceProvider to set
	 */
	protected void setDefaultResourceProvider(ResourceProvider defaultResourceProvider) {
		resources.registerDefaultResourceProvider(defaultResourceProvider);
	}

	/**
	 * @return the defaultResourceProvider
	 */
	public ResourceProvider getDefaultResourceProvider() {
		return resources.getDefaultResourceProvider();
	}

	protected void addResourceProvider(String strProviderScheme, String strProviderClass, Map arguments) throws ClassException {
		// old buld in S3
		
		
		Object o=null;
		
		o=ClassUtil.loadInstance(strProviderClass);
		
		if(o instanceof ResourceProvider) {
			ResourceProvider rp=(ResourceProvider) o;
			rp.init(strProviderScheme,arguments);
			addResourceProvider(rp);
		}
		else 
			throw new ClassException("object ["+Caster.toClassName(o)+"] must implement the interface "+ResourceProvider.class.getName());
	}

	protected void addResourceProvider(ResourceProvider provider) {
		resources.registerResourceProvider(provider);
	}
	

	public void clearResourceProviders() {
		resources.reset();
	}
	

	/**
	 * @return return the resource providers
	 */
	public ResourceProvider[] getResourceProviders() {
		return resources.getResourceProviders();
	}

	protected void setResourceProviders(ResourceProvider[] resourceProviders) {
		for(int i=0;i<resourceProviders.length;i++) {
			resources.registerResourceProvider(resourceProviders[i]);
		}
	}


	/**
	 *
	 * @see railo.runtime.config.Config#getResource(java.lang.String)
	 */
	public Resource getResource(String path) {
		return resources.getResource(path);
	}

	/**
	 *
	 * @see railo.runtime.config.Config#getApplicationListener()
	 */
	public ApplicationListener getApplicationListener() {
		return applicationListener;//new ModernAppListener();//new ClassicAppListener();
	}

	/**
	 * @param applicationListener the applicationListener to set
	 */
	protected void setApplicationListener(ApplicationListener applicationListener) {
		this.applicationListener = applicationListener;
	}

	/**
	 * @return the exceptionLogger
	 */
	public LogAndSource getExceptionLogger() {
		if(exceptionLogger==null)exceptionLogger=new LogAndSourceImpl(LogConsole.getInstance(this,Log.LEVEL_ERROR),"");
		return exceptionLogger;
	}

	/**
	 * @return the exceptionLogger
	 */
	public LogAndSource getTraceLogger() {
		if(traceLogger==null)traceLogger=new LogAndSourceImpl(LogConsole.getInstance(this,Log.LEVEL_ERROR),"");
		return traceLogger;
	}

	/**
	 * @param exceptionLogger the exceptionLogger to set
	 */
	protected void setExceptionLogger(LogAndSource exceptionLogger) {
		this.exceptionLogger = exceptionLogger;
	}

	/**
	 * @param traceLogger the traceLogger to set
	 */
	protected void setTraceLogger(LogAndSource traceLogger) {
		this.traceLogger = traceLogger;
	}

	/**
	 * @return the scriptProtect
	 */
	public int getScriptProtect() {
		return scriptProtect;
	}

	/**
	 * @param scriptProtect the scriptProtect to set
	 */
	protected void setScriptProtect(int scriptProtect) {
		this.scriptProtect = scriptProtect;
	}

	/**
	 * @return the proxyPassword
	 */
	public String getProxyPassword() {
		return proxyPassword;
	}

	/**
	 * @param proxyPassword the proxyPassword to set
	 */
	protected void setProxyPassword(String proxyPassword) {
		this.proxyPassword = proxyPassword;
	}

	/**
	 * @return the proxyPort
	 */
	public int getProxyPort() {
		return proxyPort;
	}

	/**
	 * @param proxyPort the proxyPort to set
	 */
	protected void setProxyPort(int proxyPort) {
		this.proxyPort = proxyPort;
	}

	/**
	 * @return the proxyServer
	 */
	public String getProxyServer() {
		return proxyServer;
	}

	/**
	 * @param proxyServer the proxyServer to set
	 */
	protected void setProxyServer(String proxyServer) {
		this.proxyServer = proxyServer;
	}

	/**
	 * @return the proxyUsername
	 */
	public String getProxyUsername() {
		return proxyUsername;
	}

	/**
	 * @param proxyUsername the proxyUsername to set
	 */
	protected void setProxyUsername(String proxyUsername) {
		this.proxyUsername = proxyUsername;
	}

	/**
	 * @see railo.runtime.config.Config#isProxyEnableFor(java.lang.String)
	 */
	public boolean isProxyEnableFor(String host) {
		return false;// TODO proxyEnable;
	}

	/**
	 * @return the triggerComponentDataMember
	 */
	public boolean getTriggerComponentDataMember() {
		return triggerComponentDataMember;
	}

	/**
	 * @param triggerComponentDataMember the triggerComponentDataMember to set
	 */
	protected void setTriggerComponentDataMember(boolean triggerComponentDataMember) {
		this.triggerComponentDataMember = triggerComponentDataMember;
	}

	/**
	 *
	 * @see railo.runtime.config.Config#getClientScopeDir()
	 */
	public Resource getClientScopeDir() {
		if(clientScopeDir==null) return getConfigDir().getRealResource("client");
		return clientScopeDir;
	}
	
	

	/*public int getClientScopeMaxAge() {
		return clientScopeMaxAge;
	}
	
	public void setClientScopeMaxAge(int age) {
		this. clientScopeMaxAge=age;
	}*/
	
	

	/**
	 *
	 * @see railo.runtime.config.Config#getClientScopeDirSize()
	 */
	public long getClientScopeDirSize() {
		return clientScopeDirSize;
	}

	/**
	 * @param clientScopeDir the clientScopeDir to set
	 */
	protected void setClientScopeDir(Resource clientScopeDir) {
		this.clientScopeDir = clientScopeDir;
	}

	/**
	 * @param clientScopeDirSize the clientScopeDirSize to set
	 */
	protected void setClientScopeDirSize(long clientScopeDirSize) {
		this.clientScopeDirSize = clientScopeDirSize;
	}
	/**
	 *
	 * @see railo.runtime.config.Config#getRPCClassLoader()
	 */
	public ClassLoader getRPCClassLoader(boolean reload) throws IOException {
		
		if(rpcClassLoader!=null && !reload) return rpcClassLoader;
        
		Resource dir = getDeployDirectory().getRealResource("RPC");
		if(!dir.exists())dir.createDirectory(true);
		//rpcClassLoader = new PhysicalClassLoader(dir,getFactory().getServlet().getClass().getClassLoader());
		rpcClassLoader = new PhysicalClassLoader(dir,getClass().getClassLoader());
		return rpcClassLoader;
	}

	public void resetRPCClassLoader() {
		rpcClassLoader=null;
	}

	protected void setCacheDir(Resource cacheDir) {
		this.cacheDir=cacheDir;
	}
	
	public Resource getCacheDir() {
		return this.cacheDir;
	}

	public long getCacheDirSize() {
		return cacheDirSize;
	}

	protected void setCacheDirSize(long cacheDirSize) {
		this.cacheDirSize=cacheDirSize;
	}
	


	protected void setDumpWritersEntries(DumpWriterEntry[] dmpWriterEntries) {
		this.dmpWriterEntries=dmpWriterEntries;
	}
	
	public DumpWriterEntry[] getDumpWritersEntries() {
		return dmpWriterEntries;
	}
	
	/**
	 *
	 * @see railo.runtime.config.Config#getDefaultDumpWriter()
	 */
	public DumpWriter getDefaultDumpWriter() {
		//throw new PageRuntimeException(new ApplicationException("this method is no longer supported"));
		return getDefaultDumpWriter(HTMLDumpWriter.DEFAULT_RICH);
		
	}
	
	public DumpWriter getDefaultDumpWriter(int defaultType) {
		DumpWriterEntry[] entries = getDumpWritersEntries();
		if(entries!=null)for(int i=0;i<entries.length;i++){
			if(entries[i].getDefaultType()==defaultType) {
				return entries[i].getWriter();
			}
		}
		return new HTMLDumpWriter();
	}

	/**
	 *
	 * @see railo.runtime.config.Config#getDumpWriter(java.lang.String)
	 */
	public DumpWriter getDumpWriter(String name) throws DeprecatedException {
		throw new DeprecatedException("this method is no longer supported");
	}
	
	public DumpWriter getDumpWriter(String name,int defaultType) throws ExpressionException {
		if(StringUtil.isEmpty(name)) return getDefaultDumpWriter(defaultType);
		
		DumpWriterEntry[] entries = getDumpWritersEntries();
		for(int i=0;i<entries.length;i++){
			if(entries[i].getName().equals(name)) {
				return entries[i].getWriter();
			}
		}
		
		// error
		StringBuffer sb=new StringBuffer(); 
		for(int i=0;i<entries.length;i++){
			if(i>0)sb.append(", ");
			sb.append(entries[i].getName());
		}
		throw new ExpressionException("invalid format definition ["+name+"], valid definitions are ["+sb+"]");
	}
	
	/**
	 * @see railo.runtime.config.Config#useComponentShadow()
	 */
	public boolean useComponentShadow() {
		return useComponentShadow;
	}

	/**
	 * @param useComponentShadow the useComponentShadow to set
	 */
	protected void setUseComponentShadow(boolean useComponentShadow) {
		this.useComponentShadow = useComponentShadow;
	}
	
	public DataSource getDataSource(String datasource) throws DatabaseException {
		DataSource ds=(datasource==null)?null:(DataSource) datasources.get(datasource.toLowerCase());
		if(ds!=null) return ds;
		DatabaseException de = new DatabaseException("datasource ["+datasource+"] doesn't exist",null,null,null);
		de.setAdditional("Datasource",datasource);
		throw de;
	}

	/**
	 *
	 * @see railo.runtime.config.Config#getErrWriter()
	 */
	public PrintWriter getErrWriter() {
		return err;
	}

	/**
	 * @param err the err to set
	 */
	protected void setErr(PrintWriter err) {
		this.err = err;
	}

	/**
	 *
	 * @see railo.runtime.config.Config#getOutWriter()
	 */
	public PrintWriter getOutWriter() {
		return out;
	}

	/**
	 * @param out the out to set
	 */
	protected void setOut(PrintWriter out) {
		this.out = out;
	}

	public DatasourceConnectionPool getDatasourceConnectionPool() {
		return pool;
	}



	public boolean doLocalCustomTag() {
		return doLocalCustomTag;
	}	
	
	/**
	 * @see railo.runtime.config.Config#getCustomTagExtensions()
	 */
	public String[] getCustomTagExtensions() {
		return customTagExtensions;
	}
	
	protected void setCustomTagExtensions(String[] customTagExtensions) {
		this.customTagExtensions = customTagExtensions;
	}
	
	protected void setDoLocalCustomTag(boolean doLocalCustomTag) {
		this.doLocalCustomTag= doLocalCustomTag;
	}
	
	/**
	 *
	 * @see railo.runtime.config.Config#doCustomTagDeepSearch()
	 */
	public boolean doCustomTagDeepSearch() {
		return doCustomTagDeepSearch;
	}

	/**
	 * @param doCustomTagDeepSearch the doCustomTagDeepSearch to set
	 */
	protected void setDoCustomTagDeepSearch(boolean doCustomTagDeepSearch) {
		this.doCustomTagDeepSearch = doCustomTagDeepSearch;
	}

	protected void setVersion(double version) {
		this.version=version;
	}

	/**
	 * @return the version
	 */
	public double getVersion() {
		return version;
	}
	


	public boolean closeConnection() {
		return closeConnection;
	}

	protected void setCloseConnection(boolean closeConnection) {
		this.closeConnection=closeConnection;
	}

	public boolean contentLength() {
		return contentLength;
	}

	protected void setContentLength(boolean contentLength) {
		this.contentLength=contentLength;
	}

	/**
	 * @return the constants
	 */
	public Struct getConstants() {
		return constants;
	}

	/**
	 * @param constants the constants to set
	 */
	protected void setConstants(Struct constants) {
		this.constants = constants;
	}

	/**
	 * @return the showVersion
	 */
	public boolean isShowVersion() {
		return showVersion;
	}

	/**
	 * @param showVersion the showVersion to set
	 */
	protected void setShowVersion(boolean showVersion) {
		this.showVersion = showVersion;
	}

	protected void setRemoteClients(RemoteClient[] remoteClients) {
		this.remoteClients=remoteClients;
	}
	
	public RemoteClient[] getRemoteClients() {
		if(remoteClients==null) return new RemoteClient[0];
		return remoteClients;
	}

	protected void setSecurityKey(String securityKey) {
		this.securityKey=securityKey;
		this.id=null;
	}

	public SpoolerEngine getSpoolerEngine() {
		return remoteClientSpoolerEngine;
	}

	protected void setRemoteClientLog(LogAndSource remoteClientLog) {
		this.remoteClientLog=remoteClientLog;
	}

	protected void setRemoteClientDirectory(Resource remoteClientDirectory) {
		this.remoteClientDirectory=remoteClientDirectory;
	}

	/**
	 * @return the remoteClientDirectory
	 */
	public Resource getRemoteClientDirectory() {
		return remoteClientDirectory;
	}

	/**
	 * @return the remoteClientLog
	 */
	public LogAndSource getRemoteClientLog() {
		return remoteClientLog;
	}

	protected void setSpoolerEngine(SpoolerEngine spoolerEngine) {
		this.remoteClientSpoolerEngine=spoolerEngine;
	}

	/**
	 * @return the factory
	 */
	public CFMLFactory getFactory() {
		return factory;
	}

	
	
	/* *
	 * @return the structCase
	 * /
	public int getStructCase() {
		return structCase;
	}*/

	/* *
	 * @param structCase the structCase to set
	 * /
	protected void setStructCase(int structCase) {
		this.structCase = structCase;
	}*/
	

	/**
	 * @return if error status code will be returned or not
	 */
	public boolean getErrorStatusCode() {
		return errorStatusCode;
	}

	/**
	 * @param errorStatusCode the errorStatusCode to set
	 */
	protected void setErrorStatusCode(boolean errorStatusCode) {
		this.errorStatusCode = errorStatusCode;
	}

	/**
	 * @see railo.runtime.config.Config#getLocalMode()
	 */
	public int getLocalMode() {
		return localMode;
	}

	/**
	 * @param localMode the localMode to set
	 */
	protected void setLocalMode(int localMode) {
		this.localMode = localMode;
	}

	/**
	 * @param localMode the localMode to set
	 */
	protected void setLocalMode(String strLocalMode) {
		strLocalMode=strLocalMode.trim().toLowerCase();
		if("always".equals(strLocalMode))
			this.localMode=Undefined.MODE_LOCAL_OR_ARGUMENTS_ALWAYS;
		else if("update".equals(strLocalMode))
			this.localMode=Undefined.MODE_LOCAL_OR_ARGUMENTS_ONLY_WHEN_EXISTS;
		else
			this.localMode=Undefined.MODE_LOCAL_OR_ARGUMENTS_ONLY_WHEN_EXISTS;
	}

	public Resource getVideoDirectory() {
		// TODO take from tag <video>
		Resource dir = getConfigDir().getRealResource("video");
	    if(!dir.exists())dir.mkdirs();
	    return dir;
	}


	public Resource getExtensionDirectory() {
		// TODO take from tag <extensions>
		Resource dir = getConfigDir().getRealResource("extensions");
	    if(!dir.exists())dir.mkdirs();
	    return dir;
	}
	
	protected void setExtensionProviders(ExtensionProvider[] extensionProviders) {
		this.extensionProviders=extensionProviders;
	}

	public ExtensionProvider[] getExtensionProviders() {
		return extensionProviders;
	}

	public Extension[] getExtensions() {
		return extensions;
	}

	protected void setExtensions(Extension[] extensions) {
		
		this.extensions=extensions;
	}

	protected void setExtensionEnabled(boolean extensionEnabled) {
		this.extensionEnabled=extensionEnabled;
	}
	public boolean isExtensionEnabled() {
		return extensionEnabled;
	}

	public boolean allowRealPath() {
		return allowRealPath;
	}

	protected void setAllowRealPath(boolean allowRealPath) {
		this.allowRealPath=allowRealPath;
	}

	/**
	 * @return the classClusterScope
	 */
	public Class getClusterClass() {
		return clusterClass;
	}

	/**
	 * @param classClusterScope the classClusterScope to set
	 */
	protected void setClusterClass(Class clusterClass) {
		this.clusterClass = clusterClass;
	}

	/**
	 * @see railo.runtime.config.Config#getRemoteClientUsage()
	 */
	public Struct getRemoteClientUsage() {
		if(remoteClientUsage==null)remoteClientUsage=new StructImpl();
		return remoteClientUsage;
	}
	
	protected void setRemoteClientUsage(Struct remoteClientUsage) {
		this.remoteClientUsage=remoteClientUsage;
	}

	/**
	 * @see railo.runtime.config.Config#getAdminSyncClass()
	 */
	public Class getAdminSyncClass() {
		return adminSyncClass;
	}

	protected void setAdminSyncClass(Class adminSyncClass) {
		this.adminSyncClass=adminSyncClass;
		this.adminSync=null;
	}

	public AdminSync getAdminSync() throws ClassException {
		if(adminSync==null){
			adminSync=(AdminSync) ClassUtil.loadInstance(getAdminSyncClass());
			
		}
		return this.adminSync;
	}
	
	/**
	 * @see railo.runtime.config.Config#getVideoExecuterClass()
	 */
	public Class getVideoExecuterClass() {
		return videoExecuterClass;
	}
	
	protected void setVideoExecuterClass(Class videoExecuterClass) {
		this.videoExecuterClass=videoExecuterClass;
	}

	protected void setUseTimeServer(boolean useTimeServer) {
		this.useTimeServer=useTimeServer;
	}
	
	public boolean getUseTimeServer() {
		return useTimeServer; 
	}
	

	/**
	 * @return the tagMappings
	 */
	public Mapping getTagMapping() {
		return tagMapping;
	}
	
	public Mapping getFunctionMapping() {
		return functionMapping;
	}

	/**
	 * @return the tagDirectory
	 */
	public Resource getTagDirectory() {
		return tagDirectory;
	}

	public void setAMFCaster(String strCaster, Map args) {

		amfCasterArguments=args;
        try{
			if(StringUtil.isEmpty(strCaster) || "classic".equalsIgnoreCase(strCaster)) 
	        	amfCasterClass=ClassicAMFCaster.class;
	        else if("modern".equalsIgnoreCase(strCaster))
	        	amfCasterClass=ModernAMFCaster.class;
	        else {
	        	Class caster = ClassUtil.loadClass(strCaster);
	        	if((caster.newInstance() instanceof AMFCaster)) {
	        		amfCasterClass=caster;
	        	}
	        	else {
	        		amfCasterClass=ClassicAMFCaster.class;
	        		throw new ClassException("object ["+Caster.toClassName(caster)+"] must implement the interface "+ResourceProvider.class.getName());
	        	}
	        }
        }
        catch(Exception e){
        	e.printStackTrace();
        }
	}
	
	public void setAMFCaster(Class clazz, Map args) {
		amfCasterArguments=args;
        amfCasterClass=clazz;
	}

	public AMFCaster getAMFCaster(ConfigMap properties) throws ClassException {
		if(amfCaster==null){
			if(properties!=null){
				ConfigMap cases = properties.getPropertyAsMap("property-case", null);
		        if(cases!=null){
		        	if(!amfCasterArguments.containsKey("force-cfc-lowercase"))
		        		amfCasterArguments.put("force-cfc-lowercase",Caster.toBoolean(cases.getPropertyAsBoolean("force-cfc-lowercase", false)));
		        	if(!amfCasterArguments.containsKey("force-query-lowercase"))
		        		amfCasterArguments.put("force-query-lowercase",Caster.toBoolean(cases.getPropertyAsBoolean("force-query-lowercase", false)));
		        	if(!amfCasterArguments.containsKey("force-struct-lowercase"))
		        		amfCasterArguments.put("force-struct-lowercase",Caster.toBoolean(cases.getPropertyAsBoolean("force-struct-lowercase", false)));
		        	
		        }
		        ConfigMap access = properties.getPropertyAsMap("access", null);
		        if(access!=null){
		        	if(!amfCasterArguments.containsKey("use-mappings"))
		        		amfCasterArguments.put("use-mappings",Caster.toBoolean(access.getPropertyAsBoolean("use-mappings", false)));
		        	if(!amfCasterArguments.containsKey("method-access-level"))
		        		amfCasterArguments.put("method-access-level",access.getPropertyAsString("method-access-level","remote"));
		        }
			}
			
			amfCaster=(AMFCaster)ClassUtil.loadInstance(amfCasterClass);
			amfCaster.init(amfCasterArguments);
		}
		return amfCaster;
	}
	public Class getAMFCasterClass() {
		return amfCasterClass;
	}
	public Map getAMFCasterArguments() {
		if(amfCasterArguments==null) amfCasterArguments=new HashMap();
		return amfCasterArguments;
	}

	public String getDefaultDataSource() {
		// TODO Auto-generated method stub
		return null;
	}
	protected void setDefaultDataSource(String defaultDataSource) {
		this.defaultDataSource=defaultDataSource;
	}

	/**
	 * @return the inspectTemplate 
	 * FUTURE to interface
	 */
	public short getInspectTemplate() {
		return inspectTemplate;
	}

	/**
	 * @param inspectTemplate the inspectTemplate to set
	 * FUTURE to interface
	 */
	protected void setInspectTemplate(short inspectTemplate) {
		this.inspectTemplate = inspectTemplate;
	}

	protected void setSerialNumber(String serial) {
		this.serial=serial;
	}

	public String getSerialNumber() {
		return serial;
	}

	protected void setCaches(HashTable caches) {
		this.caches=caches;
		Iterator it = caches.entrySet().iterator();
		Map.Entry entry;
		CacheConnection cc;
		while(it.hasNext()){
			entry = (Entry) it.next();
			cc=((CacheConnection)entry.getValue());
			if(cc.getName().equalsIgnoreCase(cacheDefaultConnectionNameTemplate)){
				defaultCacheTemplate=cc;
			}
			else if(cc.getName().equalsIgnoreCase(cacheDefaultConnectionNameQuery)){
				defaultCacheQuery=cc;
			}
			else if(cc.getName().equalsIgnoreCase(cacheDefaultConnectionNameResource)){
				defaultCacheResource=cc;
			}
			else if(cc.getName().equalsIgnoreCase(cacheDefaultConnectionNameObject)){
				defaultCacheObject=cc;
			}
		}
	}

	/**
	 * @return the caches
	 *FUTURE add o interface
	 */
	public Map getCacheConnections() {
		return caches;
	}

	/**
	 * @return the defaultCache
	 * FUTURE add o interface
	 */
	public CacheConnection getCacheDefaultConnection(int type) {
		if(type==CACHE_DEFAULT_OBJECT)		return defaultCacheObject;
		if(type==CACHE_DEFAULT_TEMPLATE)	return defaultCacheTemplate;
		if(type==CACHE_DEFAULT_QUERY)		return defaultCacheQuery;
		return defaultCacheResource;
	}

	public void setCacheDefaultConnectionName(int type,String cacheDefaultConnectionName) {
		if(type==CACHE_DEFAULT_TEMPLATE)	cacheDefaultConnectionNameTemplate=cacheDefaultConnectionName;
		else if(type==CACHE_DEFAULT_OBJECT)		cacheDefaultConnectionNameObject=cacheDefaultConnectionName;
		else if(type==CACHE_DEFAULT_QUERY)		cacheDefaultConnectionNameQuery=cacheDefaultConnectionName;
		else cacheDefaultConnectionNameResource=cacheDefaultConnectionName;
	}
	public String getCacheDefaultConnectionName(int type) {
		if(type==CACHE_DEFAULT_TEMPLATE)	return cacheDefaultConnectionNameTemplate;
		if(type==CACHE_DEFAULT_OBJECT)		return cacheDefaultConnectionNameObject;
		if(type==CACHE_DEFAULT_QUERY)		return cacheDefaultConnectionNameQuery;
		return cacheDefaultConnectionNameResource;
	}

	protected void setGatewayEntries(Map gatewayEntries,Resource cfcDirectory) {
		getGatewayEngine().setCFCDirectory(cfcDirectory);
		
		try {
			getGatewayEngine().addEntries(this,gatewayEntries);
		} catch (Exception e) {
			e.printStackTrace();
		}	
	}
	public GatewayEngineImpl getGatewayEngine() {
		if(gatewayEngine==null){
			gatewayEngine=new GatewayEngineImpl(this);
		}
		return gatewayEngine;
	}

	public String getCacheMD5() { 
		return cacheMD5;
	}

	public void setCacheMD5(String cacheMD5) { 
		this.cacheMD5 = cacheMD5;
	}

	public boolean getExecutionLogEnabled() {
		return executionLogEnabled;
	}
	protected void setExecutionLogEnabled(boolean executionLogEnabled) {
		this.executionLogEnabled= executionLogEnabled;
	}

	public ExecutionLogFactory getExecutionLogFactory() {
		return executionLogFactory;
	}
	protected void setExecutionLogFactory(ExecutionLogFactory executionLogFactory) {
		this.executionLogFactory= executionLogFactory;
	}
	
	public ORMEngine resetORMEngine(PageContext pc) throws PageException {
		String name = pc.getApplicationContext().getName();
		ormengines.remove(name);
		return getORMEngine(pc);
	}
	
	public ORMEngine getORMEngine(PageContext pc) throws PageException {
		String name = pc.getApplicationContext().getName();
		
		ORMEngine engine = ormengines.get(name);
		if(engine==null){
			try {
				engine=(ORMEngine)ClassUtil.loadInstance(ormEngineClass);
				engine.init(pc);
				ormengines.put(name,engine);
			}
			catch (ClassException e) {
				e.printStackTrace();
			}
		}
		
		return engine; 
	}
	
	public Class<ORMEngine> getORMEngineClass() {
		return ormEngineClass; 
	}

	public String[] getInstalledPatches() throws PageException {
		CFMLEngineFactory factory = getConfigServerImpl().getCFMLEngine().getCFMLEngineFactory();
    	
		// FUTURE make direct call
		//String[] patches = factory.getInstalledPatches();
		
		try{
			Method getInstalledPatches = factory.getClass().getMethod("getInstalledPatches", new Class[]{});
			return (String[]) getInstalledPatches.invoke(factory, new Object[]{});
		}
		catch(NoSuchMethodException e){
			try {
				return getInstalledPatches(factory);
			} catch (Exception e1) {
				throw Caster.toPageException(e1);
			}
		}
		catch(Throwable t){
			throw Caster.toPageException(t);
		}
	}
	
	/**
	 * FUTURE add to interface
	 * @return the componentMappings
	 */
	public Mapping[] getComponentMappings() {
		return componentMappings;
	}
	
	// FUTURE remove from interface
	public Mapping getComponentMapping() {
		throw new PageRuntimeException(new ApplicationException("this method is no longer supported"));
	}

	/**
	 * @param componentMappings the componentMappings to set
	 */
	protected void setComponentMappings(Mapping[] componentMappings) {
		this.componentMappings = componentMappings;
	}
	
	// FUTURE remove this
	private String getCoreExtension() throws ServletException {
    	URL res = new TP().getClass().getResource("/core/core.rcs");
        if(res!=null) return "rcs";
        
        res = new TP().getClass().getResource("/core/core.rc");
        if(res!=null) return "rc";
        
        throw new ServletException("missing core file");
	}
	
	// FUTURE remove this
	private String[] getInstalledPatches(CFMLEngineFactory factory) throws ServletException, IOException {
		File patchDir = new File(factory.getResourceRoot(),"patches");
        if(!patchDir.exists())patchDir.mkdirs();
        
		File[] patches=patchDir.listFiles(new ExtensionFilter(new String[]{"."+getCoreExtension()}));
        
        List<String> list=new ArrayList<String>();
        String name;
        int extLen=getCoreExtension().length()+1;
        for(int i=0;i<patches.length;i++) {
        	name=patches[i].getName();
        	name=name.substring(0, name.length()-extLen);
        	 list.add(name);
        }
        String[] arr = list.toArray(new String[list.size()]);
    	Arrays.sort(arr);
        return arr;
	}

	protected void setORMEngineClass(Class<ORMEngine> ormEngineClass) {
		this.ormEngineClass=ormEngineClass;
	}

	protected void setORMConfig(ORMConfiguration ormConfig) {
		this.ormConfig=ormConfig;
	}

	public ORMConfiguration getORMConfig() {
		return ormConfig;
	}

	public Mapping createCustomTagAppMappings(String virtual, String physical) {
		Mapping m=customTagAppMappings.get(physical.toLowerCase());
		
		if(m==null){
			m=new MappingImpl(
				this,virtual,
				physical,
				null,false,true,false,false,false,true
				);
			customTagAppMappings.put(physical.toLowerCase(),m);
		}
		
		return m;
	}

	
	
	private Map<String,PageSource> pages=null;//new ArrayList<Page>();
	
	
	public Page getCachedPage(PageContext pc,String pathWithCFC) throws PageException {
		if(pages==null) return null; 
		
		PageSource ps = pages.get(pathWithCFC.toLowerCase());
		if(ps==null) return null;
		return ((PageSourceImpl)ps).loadPage(pc,pc.getConfig(),null);
	}
	
	public void putCachedPageSource(String pathWithCFC,PageSource ps) {
		if(pages==null) pages=new HashMap<String, PageSource>();//new ReferenceMap(ReferenceMap.SOFT,ReferenceMap.SOFT); 
		pages.put(pathWithCFC.toLowerCase(),ps);
	}
	
	public Struct listComponentCache() {
		Struct sct=new StructImpl();
		if(pages==null) return sct; 
		Iterator<Entry<String, PageSource>> it = pages.entrySet().iterator();
		
		Entry<String, PageSource> entry;
		while(it.hasNext()){
			entry = it.next();
			sct.setEL(entry.getKey(),entry.getValue().getDisplayPath());
		}
		return sct;
	}
	
	public void clearComponentCache() {
		if(pages==null) return; 
		pages.clear();
	}
	
	

	public ImportDefintion getComponentDefaultImport() {
		return componentDefaultImport;
	}
	protected void setComponentDefaultImport(String str) {
		ImportDefintion cdi = ImportDefintion.getInstance(str, null);
		if(cdi!=null)this.componentDefaultImport= cdi;
	}

    /**
	 * @return the componentLocalSearch
	 */
	public boolean getComponentLocalSearch() {
		return componentLocalSearch;
	}

	/**
	 * @param componentLocalSearch the componentLocalSearch to set
	 */
	protected void setComponentLocalSearch(boolean componentLocalSearch) {
		this.componentLocalSearch = componentLocalSearch;
	}

    /**
	 * @return the componentLocalSearch
	 */
	public boolean getComponentRootSearch() {
		return componentRootSearch;
	}

	/**
	 * @param componentLocalSearch the componentLocalSearch to set
	 */
	protected void setComponentRootSearch(boolean componentRootSearch) {
		this.componentRootSearch = componentRootSearch;
	}
}