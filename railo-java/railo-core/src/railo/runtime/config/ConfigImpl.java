package railo.runtime.config;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TimeZone;

import org.apache.commons.collections.map.ReferenceMap;

import railo.commons.digest.Hash;
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
import railo.commons.io.res.type.compress.Compress;
import railo.commons.io.res.type.compress.CompressResource;
import railo.commons.io.res.type.compress.CompressResourceProvider;
import railo.commons.io.res.util.ResourceClassLoader;
import railo.commons.io.res.util.ResourceUtil;
import railo.commons.lang.ClassException;
import railo.commons.lang.ClassUtil;
import railo.commons.lang.ExceptionUtil;
import railo.commons.lang.Md5;
import railo.commons.lang.PhysicalClassLoader;
import railo.commons.lang.StringUtil;
import railo.commons.lang.SystemOut;
import railo.commons.net.IPRange;
import railo.loader.engine.CFMLEngine;
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
import railo.runtime.component.ImportDefintionImpl;
import railo.runtime.customtag.InitFile;
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
import railo.runtime.listener.AppListenerUtil;
import railo.runtime.listener.ApplicationContext;
import railo.runtime.listener.ApplicationListener;
import railo.runtime.net.amf.AMFCaster;
import railo.runtime.net.amf.ClassicAMFCaster;
import railo.runtime.net.amf.ModernAMFCaster;
import railo.runtime.net.mail.Server;
import railo.runtime.net.ntp.NtpClient;
import railo.runtime.net.proxy.ProxyData;
import railo.runtime.op.Caster;
import railo.runtime.orm.ORMConfiguration;
import railo.runtime.orm.ORMEngine;
import railo.runtime.rest.RestSettingImpl;
import railo.runtime.rest.RestSettings;
import railo.runtime.schedule.Scheduler;
import railo.runtime.schedule.SchedulerImpl;
import railo.runtime.search.SearchEngine;
import railo.runtime.security.SecurityManager;
import railo.runtime.spooler.SpoolerEngine;
import railo.runtime.type.Struct;
import railo.runtime.type.StructImpl;
import railo.runtime.type.UDF;
import railo.runtime.type.dt.TimeSpan;
import railo.runtime.type.dt.TimeSpanImpl;
import railo.runtime.type.scope.Cluster;
import railo.runtime.type.scope.ClusterNotSupported;
import railo.runtime.type.scope.Undefined;
import railo.runtime.type.util.ArrayUtil;
import railo.runtime.type.util.KeyConstants;
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
import static railo.runtime.db.DatasourceManagerImpl.QOQ_DATASOURCE_NAME;


/**
 * Hold the definitions of the railo configuration.
 */
public abstract class ConfigImpl implements Config {

	public static final short INSPECT_UNDEFINED = 4;// FUTURE move to Config; Hibernate Extension has hardcoded this 4, do not change!!!!


	public static final int CLIENT_BOOLEAN_TRUE = 0;
	public static final int CLIENT_BOOLEAN_FALSE = 1;
	public static final int SERVER_BOOLEAN_TRUE = 2;
	public static final int SERVER_BOOLEAN_FALSE = 3;

	public static final int DEBUG_DATABASE = 1;
	public static final int DEBUG_EXCEPTION = 2;
	public static final int DEBUG_TRACING = 4;
	public static final int DEBUG_TIMER = 8;
	public static final int DEBUG_IMPLICIT_ACCESS = 16;
	public static final int DEBUG_QUERY_USAGE = 32;
	
	
	
	
	public static final ExtensionProvider[] RAILO_EXTENSION_PROVIDERS = new ExtensionProviderImpl[]{
		new ExtensionProviderImpl("http://www.getrailo.com/ExtensionProvider.cfc",true),
		new ExtensionProviderImpl("http://www.getrailo.org/ExtensionProvider.cfc",true)
	};
	private static final Extension[] EXTENSIONS_EMPTY = new Extension[0];
	
	public static final int AMF_CONFIG_TYPE_XML = 1;
	public static final int AMF_CONFIG_TYPE_MANUAL = 2;

	public static final int MODE_CUSTOM = 1;
	public static final int MODE_STRICT = 2;
	

	public static final int CFML_WRITER_REFULAR=1;
	public static final int CFML_WRITER_WS=2;
	public static final int CFML_WRITER_WS_PREF=3;


	public static final String DEFAULT_STORAGE_SESSION = "memory";
	public static final String DEFAULT_STORAGE_CLIENT = "cookie";
	
	
	private int mode=MODE_CUSTOM;

	private PhysicalClassLoader rpcClassLoader;
	private Map<String,DataSource> datasources=new HashMap<String,DataSource>();
	private Map<String,CacheConnection> caches=new HashMap<String, CacheConnection>();

	private CacheConnection defaultCacheFunction=null;
	private CacheConnection defaultCacheObject=null;
	private CacheConnection defaultCacheTemplate=null;
	private CacheConnection defaultCacheQuery=null;
	private CacheConnection defaultCacheResource=null;

	private String cacheDefaultConnectionNameFunction=null;
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
    private int debugLogOutput=SERVER_BOOLEAN_FALSE;
    private int debugOptions=0;

    private boolean suppresswhitespace = false;
    private boolean suppressContent = false;
    private boolean showVersion = false;
    
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
	private String sessionStorage=DEFAULT_STORAGE_SESSION;
	private String clientStorage=DEFAULT_STORAGE_CLIENT;
	

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

    private boolean psq=false;
    private boolean debugShowUsage;

    private Map<String,String> errorTemplates=new HashMap<String,String>();

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
    private boolean restList=false;
    //private boolean restAllowChanges=false;
    
    private LogAndSource mailLogger=null;//new LogAndSourceImpl(LogConsole.getInstance(Log.LEVEL_ERROR),"");
    private LogAndSource restLogger=null;//new LogAndSourceImpl(LogConsole.getInstance(Log.LEVEL_ERROR),"");
    private LogAndSource threadLogger=null;//new LogAndSourceImpl(LogConsole.getInstance(Log.LEVEL_INFO),"");
    
    private LogAndSource requestTimeoutLogger=null;
    private LogAndSource applicationLogger=null;
    private LogAndSource deployLogger=null;
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
    
    private String resourceCharset=SystemUtil.getCharset().name();
    private String templateCharset=SystemUtil.getCharset().name();
    private String webCharset="UTF-8";

	private String mailDefaultEncoding = "UTF-8";
	
	private Resource tldFile;
	private Resource fldFile;

	private Resources resources=new ResourcesImpl();

	private ApplicationListener applicationListener;
	
	private int scriptProtect=ApplicationContext.SCRIPT_PROTECT_ALL;

	//private boolean proxyEnable=false;
	private ProxyData proxy =null;


	private Resource clientScopeDir;
	private Resource sessionScopeDir;
	private long clientScopeDirSize=1024*1024*10;
	private long sessionScopeDirSize=1024*1024*10;

	private Resource cacheDir;
	private long cacheDirSize=1024*1024*10;


	private boolean useComponentShadow=true;

	
	private PrintWriter out=SystemUtil.getPrintWriter(SystemUtil.OUT);
	private PrintWriter err=SystemUtil.getPrintWriter(SystemUtil.ERR);

	private DatasourceConnectionPool pool=new DatasourceConnectionPool();

	private boolean doCustomTagDeepSearch=false;
	private boolean doComponentTagDeepSearch=false;

	private double version=1.0D;

	private boolean closeConnection=false;
	private boolean contentLength=true;
	private boolean allowCompression=false;
	

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
	//private Resource functionDirectory;
	protected MappingImpl functionMapping;
	private Map amfCasterArguments;
	private Class amfCasterClass=ClassicAMFCaster.class;
	private AMFCaster amfCaster;
	//private String defaultDataSource;
	private short inspectTemplate=INSPECT_ONCE;
	private String serial="";
	private String cacheMD5;
	private boolean executionLogEnabled;
	private ExecutionLogFactory executionLogFactory;
	//private int clientScopeMaxAge=90;
	private Map<String, ORMEngine> ormengines=new HashMap<String, ORMEngine>();
	private Class<ORMEngine> ormEngineClass;
	private ORMConfiguration ormConfig;
	//private ResourceClassLoaderFactory classLoaderFactory;
	private ResourceClassLoader resourceCL;
	
	private ImportDefintion componentDefaultImport=new ImportDefintionImpl("org.railo.cfml","*");
	private boolean componentLocalSearch=true;
	private boolean componentRootSearch=true;
	private LogAndSource mappingLogger;
	private LogAndSource ormLogger;
	private boolean useComponentPathCache=true;
	private boolean useCTPathCache=true;
	private int amfConfigType=AMF_CONFIG_TYPE_XML;
	private LogAndSource scopeLogger;
	private railo.runtime.rest.Mapping[] restMappings;
	
	protected int writerType=CFML_WRITER_REFULAR;
	private long configFileLastModified;
	private boolean checkForChangesInConfigFile;
	private String apiKey=null;
	
	
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


    @Override
    public short getCompileType() {
        return compileType;
    }

    @Override
    public void reset() {
    	timeServer="";
        componentDumpTemplate="";
        factory.resetPageContext();
        //resources.reset();
        ormengines.clear();
        compressResources.clear();
        clearFunctionCache();
        clearCTCache();
        clearComponentCache();
        //clearComponentMetadata();
    }
    
    @Override
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
     * @param factory
     * @param configDir - config directory
     * @param configFile - config file
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
	
	public long lastModified() {
        return configFileLastModified;
    }
	
	protected void setLastModified() {
		this.configFileLastModified=configFile.lastModified();
    }
	

    

	@Override
    public short getScopeCascadingType() {
        return type;
    }
    
    @Override
    public String[] getCFMLExtensions() {
        return Constants.CFML_EXTENSION;
    }
    @Override
    public String getCFCExtension() {
        return Constants.CFC_EXTENSION;
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
    
    @Override
    public boolean allowImplicidQueryCall() {
        return _allowImplicidQueryCall;
    }

    @Override
    public boolean mergeFormAndURL() {
        return _mergeFormAndURL;
    }
    
    @Override
    public TimeSpan getApplicationTimeout() {
        return applicationTimeout;
    }

    @Override
    public TimeSpan getSessionTimeout() {
        return sessionTimeout;
    }

    @Override
    public TimeSpan getClientTimeout() {
        return clientTimeout;
    }
    
    @Override
    public TimeSpan getRequestTimeout() {
        return requestTimeout;
    }   
    
    @Override
    public boolean isClientCookies() {
        return clientCookies;
    }
    
    @Override
    public boolean isClientManagement() {
        return clientManagement;
    }
    
    @Override
    public boolean isDomainCookies() {
        return domainCookies;
    }
    
    @Override
    public boolean isSessionManagement() {
        return sessionManagement;
    }
    
    @Override
    public boolean isMailSpoolEnable() {
        //print.ln("isMailSpoolEnable:"+spoolEnable);
        return spoolEnable;
    }
    
    @Override
    public Server[] getMailServers() {
    	if(mailServers==null) mailServers=new Server[0];
        return mailServers;
    }
    
    @Override
    public int getMailTimeout() {
        return mailTimeout;
    }   
    
    @Override
    public boolean getPSQL() {
        return psq;   
    }

    @Override
    public ClassLoader getClassLoader() {
    	return getResourceClassLoader();   
    }
    public ResourceClassLoader getResourceClassLoader() {
    	if(resourceCL==null) throw new RuntimeException("no RCL defined yet!");
    	return resourceCL;   
    }

    @Override
    public ClassLoader getClassLoader(Resource[] reses) throws IOException {
    	// FUTURE @deprected use instead PageContext.getClassLoader(Resource[] reses);
    	//PageContextImpl pci=(PageContextImpl) ThreadLocalPageContext.get();
    	//if(pci==null) 
    		throw new RuntimeException("this method is no longer suported");
    	//return pci.getClassLoader(reses);
    	////return getResourceClassLoader().getCustomResourceClassLoader(reses);   
    }
    
	/* *
	 * @return the classLoaderFactory
	
	public ResourceClassLoaderFactory getClassLoaderFactory() {
		return classLoaderFactory;
	} */

	/* *
	 * @param classLoaderFactory the classLoaderFactory to set
	/
    protected void setClassLoaderFactory(ResourceClassLoaderFactory classLoaderFactory) {
		if(this.classLoaderFactory!=null){
			classLoaderFactory.reset();
		}
		this.classLoaderFactory = classLoaderFactory;
	} */
    
    protected void setResourceClassLoader(ResourceClassLoader resourceCL) {
    	this.resourceCL=resourceCL;
	}

    @Override
    public Locale getLocale() {
        return locale;
    }

    @Override
    public boolean debug() {
    	return _debug==CLIENT_BOOLEAN_TRUE || _debug==SERVER_BOOLEAN_TRUE;
    }
    
    public boolean debugLogOutput() {
    	return debug() && debugLogOutput==CLIENT_BOOLEAN_TRUE || debugLogOutput==SERVER_BOOLEAN_TRUE;
    }

    public int intDebug() {
        return _debug;
    }

    public int intDebugLogOutput() {
        return debugLogOutput;
    }
    
    @Override
    public Resource getTempDirectory() {
    	if(tempDirectory==null) return SystemUtil.getTempDirectory();
        return tempDirectory;
    }
    
    @Override
    public int getMailSpoolInterval() {
        return spoolInterval;
    }

    @Override
    public LogAndSource getMailLogger() {
    	if(mailLogger==null)mailLogger=new LogAndSourceImpl(LogConsole.getInstance(this,Log.LEVEL_ERROR),"");
		return mailLogger;
    }
    

    public LogAndSource getRestLogger() {
    	if(restLogger==null)restLogger=new LogAndSourceImpl(LogConsole.getInstance(this,Log.LEVEL_ERROR),"");
		return restLogger;
    }

    public LogAndSource getThreadLogger() {
    	if(threadLogger==null)threadLogger=new LogAndSourceImpl(LogConsole.getInstance(this,Log.LEVEL_ERROR),"");
		return threadLogger;
    }


    public void setThreadLogger(LogAndSource threadLogger) {
    	this.threadLogger=threadLogger;
    }
    
    @Override
    public LogAndSource getRequestTimeoutLogger() {
    	if(requestTimeoutLogger==null)requestTimeoutLogger=new LogAndSourceImpl(LogConsole.getInstance(this,Log.LEVEL_ERROR),"");
		return requestTimeoutLogger;
    }

    @Override
    public TimeZone getTimeZone() {
        return timeZone;
    }
    
    @Override
    public long getTimeServerOffset() {
        return timeOffset;
    }
    
    @Override
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
     * @return gets the password as hash
     */
    protected String getPassword() {
        return password;
    }
    
    protected boolean isPasswordEqual(String password, boolean hashIfNecessary) {
    	if(this.password.equals(password)) return true;
    	if(!hashIfNecessary) return false;
    	try {
    		return this.password.equals(ConfigWebFactory.hash(password));
		}
		catch (IOException e) {
			e.printStackTrace();
			return false;
		}
    }
    
    @Override
    public boolean hasPassword() {
        return !StringUtil.isEmpty(password);
    }
    
    @Override
    public boolean passwordEqual(String password) {
        return isPasswordEqual(password,true);
    }

    @Override
    public Mapping[] getMappings() {
        return mappings;
    }
    
    public railo.runtime.rest.Mapping[] getRestMappings() {
    	if(restMappings==null) restMappings=new railo.runtime.rest.Mapping[0];
        return restMappings;
    }
  
    protected void setRestMappings(railo.runtime.rest.Mapping[] restMappings) {
    	
    	// make sure only one is default
    	boolean hasDefault=false;
    	railo.runtime.rest.Mapping m;
    	for(int i=0;i<restMappings.length;i++){
    		m=restMappings[i];
    		if(m.isDefault()) {
    			if(hasDefault) m.setDefault(false);
    			hasDefault=true;
    		}
    	}
    	
        this.restMappings= restMappings;
    }


    public PageSource getPageSource(Mapping[] mappings, String realPath,boolean onlyTopLevel) {
    	throw new PageRuntimeException(new DeprecatedException("method not supported"));
    }
    
    public PageSource getPageSourceExisting(PageContext pc,Mapping[] mappings, String realPath,boolean onlyTopLevel,boolean useSpecialMappings, boolean useDefaultMapping, boolean onlyPhysicalExisting) {
        realPath=realPath.replace('\\','/');
        String lcRealPath = StringUtil.toLowerCase(realPath)+'/';
        Mapping mapping;
        PageSource ps;

        if(mappings!=null){
	        for(int i=0;i<mappings.length;i++) {
	            mapping = mappings[i];
	            //print.err(lcRealPath+".startsWith"+(mapping.getStrPhysical()));
	            if(lcRealPath.startsWith(mapping.getVirtualLowerCaseWithSlash(),0)) {
	            	ps= mapping.getPageSource(realPath.substring(mapping.getVirtual().length()));
	            	if(onlyPhysicalExisting) {
	            		if(ps.physcalExists())return ps;
	            	}
	            	else if(ps.exists()) return ps;
	            }
	        }
        }
        
        /// special mappings
        if(useSpecialMappings && lcRealPath.startsWith("/mapping-",0)){
        	String virtual="/mapping-tag";
        	// tag mappings
        	Mapping[] tagMappings=(this instanceof ConfigWebImpl)?new Mapping[]{((ConfigWebImpl)this).getServerTagMapping(),getTagMapping()}:new Mapping[]{getTagMapping()};
        	if(lcRealPath.startsWith(virtual,0)){
	        	for(int i=0;i<tagMappings.length;i++) {
		            mapping = tagMappings[i];
		            //if(lcRealPath.startsWith(mapping.getVirtualLowerCaseWithSlash(),0)) {
		            	ps = mapping.getPageSource(realPath.substring(virtual.length()));
		            	if(onlyPhysicalExisting) {
		            		if(ps.physcalExists())return ps;
		            	}
		            	else if(ps.exists()) return ps;
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
		            	if(onlyPhysicalExisting) {
		            		if(ps.physcalExists())return ps;
		            	}
		            	else if(ps.exists()) return ps;
		            //}
		        }
        	}
        }
        
        // component mappings (only used for gateway)
        if(pc!=null && ((PageContextImpl)pc).isGatewayContext()) {
        	boolean isCFC=getCFCExtension().equalsIgnoreCase(ResourceUtil.getExtension(realPath, null));
            if(isCFC) {
	        	Mapping[] cmappings = getComponentMappings();
	        	for(int i=0;i<cmappings.length;i++) {
	        		ps = cmappings[i].getPageSource(realPath);
	            	if(onlyPhysicalExisting) {
	            		if(ps.physcalExists())return ps;
	            	}
	            	else if(ps.exists()) return ps;
	            }
        	}
        }
        
        // config mappings
        for(int i=0;i<this.mappings.length-1;i++) {
            mapping = this.mappings[i];
            if((!onlyTopLevel || mapping.isTopLevel()) && lcRealPath.startsWith(mapping.getVirtualLowerCaseWithSlash(),0)) {
            	ps= mapping.getPageSource(realPath.substring(mapping.getVirtual().length()));
            	if(onlyPhysicalExisting) {
            		if(ps.physcalExists())return ps;
            	}
            	else if(ps.exists()) return ps;
            }
        }
        
        if(useDefaultMapping){
        	ps= this.mappings[this.mappings.length-1].getPageSource(realPath);
        	if(onlyPhysicalExisting) {
        		if(ps.physcalExists())return ps;
        	}
        	else if(ps.exists()) return ps;
        }
        return null;
    }
    

    
    public PageSource[] getPageSources(PageContext pc,Mapping[] mappings, String realPath,boolean onlyTopLevel,boolean useSpecialMappings, boolean useDefaultMapping) {
        realPath=realPath.replace('\\','/');
        String lcRealPath = StringUtil.toLowerCase(realPath)+'/';
        Mapping mapping;

        PageSource ps;
        List<PageSource> list=new ArrayList<PageSource>();
    	
        if(mappings!=null){
	        for(int i=0;i<mappings.length;i++) {
	            mapping = mappings[i];
	            //print.err(lcRealPath+".startsWith"+(mapping.getStrPhysical()));
	            if(lcRealPath.startsWith(mapping.getVirtualLowerCaseWithSlash(),0)) {
	            	list.add(mapping.getPageSource(realPath.substring(mapping.getVirtual().length())));
	            }
	        }
        }
        
        /// special mappings
        if(useSpecialMappings && lcRealPath.startsWith("/mapping-",0)){
        	String virtual="/mapping-tag";
        	// tag mappings
        	Mapping[] tagMappings=(this instanceof ConfigWebImpl)?new Mapping[]{((ConfigWebImpl)this).getServerTagMapping(),getTagMapping()}:new Mapping[]{getTagMapping()};
        	if(lcRealPath.startsWith(virtual,0)){
	        	for(int i=0;i<tagMappings.length;i++) {
		            ps=tagMappings[i].getPageSource(realPath.substring(virtual.length()));
		            if(ps.exists()) list.add(ps);
		        }
        	}
        	
        	// customtag mappings
        	tagMappings=getCustomTagMappings();
        	virtual="/mapping-customtag";
        	if(lcRealPath.startsWith(virtual,0)){
	        	for(int i=0;i<tagMappings.length;i++) {
		            ps=tagMappings[i].getPageSource(realPath.substring(virtual.length()));
		            if(ps.exists()) list.add(ps);
		        }
        	}
        }
        
        // component mappings (only used for gateway)
        if(pc!=null && ((PageContextImpl)pc).isGatewayContext()) {
        	boolean isCFC=getCFCExtension().equalsIgnoreCase(ResourceUtil.getExtension(realPath, null));
            if(isCFC) {
	        	Mapping[] cmappings = getComponentMappings();
	        	for(int i=0;i<cmappings.length;i++) {
	        		ps=cmappings[i].getPageSource(realPath);
	        		if(ps.exists()) list.add(ps);
	            }
        	}
        }
        
        // config mappings
        for(int i=0;i<this.mappings.length-1;i++) {
            mapping = this.mappings[i];
            if((!onlyTopLevel || mapping.isTopLevel()) && lcRealPath.startsWith(mapping.getVirtualLowerCaseWithSlash(),0)) {
            	list.add(mapping.getPageSource(realPath.substring(mapping.getVirtual().length())));
            }
        }
        
        if(useDefaultMapping){
        	list.add(this.mappings[this.mappings.length-1].getPageSource(realPath));
        }
        return list.toArray(new PageSource[list.size()]); 
    }
    
    /**
     * @param mappings
     * @param realPath
     * @param alsoDefaultMapping ignore default mapping (/) or not
     * @return physical path from mapping
     */
    public Resource getPhysical(Mapping[] mappings, String realPath, boolean alsoDefaultMapping) {
    	throw new PageRuntimeException(new DeprecatedException("method not supported"));
    }

    public Resource[] getPhysicalResources(PageContext pc,Mapping[] mappings, String realPath,boolean onlyTopLevel,boolean useSpecialMappings, boolean useDefaultMapping) {
    	// now that archives can be used the same way as physical resources, there is no need anymore to limit to that
    	throw new PageRuntimeException(new DeprecatedException("method not supported"));
    	/*PageSource[] pages = getPageSources(pc, mappings, realPath, onlyTopLevel, useSpecialMappings, useDefaultMapping);
    	List<Resource> list=new ArrayList<Resource>();
    	Resource res;
    	for(int i=0;i<pages.length;i++) {
    		if(!pages[i].getMapping().hasPhysical()) continue;
    		res=pages[i].getPhyscalFile();
    		if(res!=null) list.add(res);
    	}
    	return list.toArray(new Resource[list.size()]);*/
    }
    

    public Resource getPhysicalResourceExisting(PageContext pc,Mapping[] mappings, String realPath,boolean onlyTopLevel,boolean useSpecialMappings, boolean useDefaultMapping) {
    	// now that archives can be used the same way as physical resources, there is no need anymore to limit to that
    	throw new PageRuntimeException(new DeprecatedException("method not supported"));
    	/*PageSource ps = getPageSourceExisting(pc, mappings, realPath, onlyTopLevel, useSpecialMappings, useDefaultMapping,true);
    	if(ps==null) return null;
    	return ps.getPhyscalFile();*/
    }

    public PageSource toPageSource(Mapping[] mappings, Resource res,PageSource defaultValue) {
        Mapping mapping;
        String path;
        
        // app-cfc mappings
        if(mappings!=null){
            for(int i=0;i<mappings.length;i++) {
                mapping = mappings[i];
                
            // Physical
               if(mapping.hasPhysical()) {
               	path=ResourceUtil.getPathToChild(res, mapping.getPhysical());
                   if(path!=null) {
                   	return mapping.getPageSource(path);
                   }
               }
           // Archive
               if(mapping.hasArchive() && res.getResourceProvider() instanceof CompressResourceProvider) {
            	   Resource archive = mapping.getArchive();
            	   CompressResource cr = ((CompressResource) res);
            	   if(archive.equals(cr.getCompressResource())) {
            		   return mapping.getPageSource(cr.getCompressPath());
            	   }
               }
            }
        }
        
        // config mappings
        for(int i=0;i<this.mappings.length;i++) {
            mapping = this.mappings[i];
            	
         // Physical
            if(mapping.hasPhysical()) {
            	path=ResourceUtil.getPathToChild(res, mapping.getPhysical());
                if(path!=null) {
                	return mapping.getPageSource(path);
                }
            }
        // Archive
            if(mapping.hasArchive() && res.getResourceProvider() instanceof CompressResourceProvider) {
        		Resource archive = mapping.getArchive();
        		CompressResource cr = ((CompressResource) res);
        		if(archive.equals(cr.getCompressResource())) {
        			return mapping.getPageSource(cr.getCompressPath());
        		}
            }
        }
        
    // map resource to root mapping when same filesystem
        Mapping rootMapping = this.mappings[this.mappings.length-1];
        Resource root;
        if(rootMapping.hasPhysical() && 
        		res.getResourceProvider().getScheme().equals((root=rootMapping.getPhysical()).getResourceProvider().getScheme())) {
	        
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
        // MUST better impl than this
        if(this instanceof ConfigWebImpl) {
        	Resource parent = res.getParentResource();
        	if(parent!=null && !parent.equals(res)) {
        		Mapping m = ((ConfigWebImpl)this).getApplicationMapping("/", parent.getAbsolutePath());
        		return m.getPageSource(res.getName());
        	}
        }
        
		
     // Archive
        // MUST check archive
        return defaultValue;
    }
    
    @Override
    public Resource getConfigDir() {
        return configDir;
    }
    
    @Override
    public Resource getConfigFile() {
        return configFile;
    }

    @Override
    public LogAndSource getScheduleLogger() {
    	return scheduler.getLogger();
    }
    
    @Override
    public LogAndSource getApplicationLogger() {
    	if(applicationLogger==null)applicationLogger=new LogAndSourceImpl(LogConsole.getInstance(this,Log.LEVEL_ERROR),"");
		return applicationLogger;
    }
    
    public LogAndSource getDeployLogger() {
    	if(deployLogger==null){
    		deployLogger=new LogAndSourceImpl(LogConsole.getInstance(this,Log.LEVEL_INFO),"");
    	}
		return deployLogger;
    }
    
    public LogAndSource getScopeLogger() {
    	if(scopeLogger==null)scopeLogger=new LogAndSourceImpl(LogConsole.getInstance(this,Log.LEVEL_ERROR),"");
		return scopeLogger;
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
        Map<String,TagLib> map=new HashMap<String,TagLib>();
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
                		overwrite(map.get(key),tl);
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
        	else overwrite(map.get(key),tl);
        }

        // now fill back to array
        tlds=new TagLib[map.size()];
        int index=0;
        Iterator<TagLib> it = map.values().iterator();
        while(it.hasNext()) {
        	tlds[index++]=it.next();
        }
    }
    
    public TagLib getCoreTagLib(){
    	for(int i=0;i<tlds.length;i++) {
        	if(tlds[i].getNameSpaceAndSeparator().equals("cf"))return tlds[i];	
        }
    	throw new RuntimeException("no core taglib found"); // this should never happen
    }
    
    protected void setTagDirectory(Resource tagDirectory) {
    	this.tagDirectory=tagDirectory;
    	
    	this.tagMapping= new MappingImpl(this,"/mapping-tag/",tagDirectory.getAbsolutePath(),null,ConfigImpl.INSPECT_NEVER,true,true,true,true,false,true,null);
    	
    	TagLib tl=getCoreTagLib();
    	
        // now overwrite with new data
        if(tagDirectory.isDirectory()) {
        	String[] files=tagDirectory.list(new ExtensionResourceFilter(new String[]{"cfm","cfc"}));
            for(int i=0;i<files.length;i++) {
            	if(tl!=null)createTag(tl, files[i]);
                    
            }
        }
        
    }
    
    public void createTag(TagLib tl,String filename) {// Jira 1298
    	String name=toName(filename);//filename.substring(0,filename.length()-(getCFCExtension().length()+1));
        
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
    	//this.functionDirectory=functionDirectory;
    	this.functionMapping= new MappingImpl(this,"/mapping-function/",functionDirectory.getAbsolutePath(),null,ConfigImpl.INSPECT_NEVER,true,true,true,true,false,true,null);
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
    	//PageSource ps = functionMapping.getPageSource(filename);
    	
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
		Iterator<TagLibTag> it = newTL.getTags().values().iterator();
		while(it.hasNext()){
			existingTL.setTag(it.next());
		}
	}

	private String getKey(TagLib tl) {
		return tl.getNameSpaceAndSeparator().toLowerCase();
	}
	
	protected void setFldFile(Resource fileFld) throws FunctionLibException {
		// merge all together (backward compatibility)
        if(flds.length>1)for(int i=1;i<flds.length;i++) {
        	overwrite(flds[0], flds[i]);
        }
        flds=new FunctionLib[]{flds[0]};
        
		
		if(fileFld==null) return;
        this.fldFile=fileFld;

        
        // overwrite with addional functions
        FunctionLib fl;
        if(fileFld.isDirectory()) {
            Resource[] files=fileFld.listResources(new ExtensionResourceFilter("fld"));
            for(int i=0;i<files.length;i++) {
                try {
                	fl = FunctionLibFactory.loadFromFile(files[i]);
                	overwrite(flds[0],fl);
                	
                }
                catch(FunctionLibException fle) {
                    SystemOut.printDate(out,"can't load fld "+files[i]);
                    fle.printStackTrace(getErrWriter());
                }   
            }
        }
        else {
        	fl = FunctionLibFactory.loadFromFile(fileFld);
        	overwrite(flds[0],fl);
        }
    }

	/*
    protected void setFldFileOld(Resource fileFld) throws FunctionLibException {
    	if(fileFld==null) return;
        this.fldFile=fileFld;

        Map<String,FunctionLib> map=new LinkedHashMap<String,FunctionLib>();
        String key;
        // First fill existing to set
        for(int i=0;i<flds.length;i++) {
        	key=getKey(flds[i]);
        	map.put(key,flds[i]);
        }
        
        // now overwrite with new data
        FunctionLib fl;
        if(fileFld.isDirectory()) {
            Resource[] files=fileFld.listResources(new ExtensionResourceFilter("fld"));
            for(int i=0;i<files.length;i++) {
                try {
                	fl = FunctionLibFactory.loadFromFile(files[i]);
                	key=getKey(fl);
                	// for the moment we only need one fld, so it is always overwrite, when you remove this make sure you get no conflicts with duplicates
                	if(map.containsKey(key)) 
                		overwrite(map.get(key),fl);
                	else 
                		map.put(key,fl);
                		
                	
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

        	// for the moment we only need one fld, so it is always overwrite, when you remove this make sure you get no conflicts with duplicates
        	if(map.containsKey(key))
        		overwrite(map.get(key),fl);
        	else 
        		map.put(key,fl);
        }
        
        // now fill back to array
        flds=new FunctionLib[map.size()];
        int index=0;
        Iterator<FunctionLib> it = map.values().iterator();
        while(it.hasNext()) {
        	flds[index++]= it.next();
        }
    }*/
    

    

    private void overwrite(FunctionLib existingFL, FunctionLib newFL) {
		Iterator<FunctionLibFunction> it = newFL.getFunctions().values().iterator();
		while(it.hasNext()){
			existingFL.setFunction(it.next());
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
     * @param clientTimeout The sessionTimeout to set.
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
    
    protected void setDebugLogOutput(int debugLogOutput) {
        this.debugLogOutput=debugLogOutput;
    }   
    
    /**
     * sets the temp directory
     * @param strTempDirectory temp directory
     * @throws ExpressionException
     */
    protected void setTempDirectory(String strTempDirectory, boolean flush) throws ExpressionException {
        setTempDirectory(resources.getResource(strTempDirectory),flush);
    }   
    
    /**
     * sets the temp directory
     * @param tempDirectory temp directory
     * @throws ExpressionException
     */
    protected void setTempDirectory(Resource tempDirectory, boolean flush) throws ExpressionException {
        if(!isDirectory(tempDirectory) || !tempDirectory.isWriteable()) {
        	SystemOut.printDate(getErrWriter(), "temp directory ["+tempDirectory+"] is not writable or can not be created, using directory ["+SystemUtil.getTempDirectory()+"] instead");
        	tempDirectory=SystemUtil.getTempDirectory();
        	if(!tempDirectory.isWriteable()){
        		SystemOut.printDate(getErrWriter(), "temp directory ["+tempDirectory+"] is not writable");
        	}
        }
        if(flush)ResourceUtil.removeChildrenEL(tempDirectory);// start with a empty temp directory
        this.tempDirectory=tempDirectory;
    }

    /**
     * sets the Schedule Directory
     * @param scheduleDirectory sets the schedule Directory 
     * @param logger
     * @throws PageException
     */
    protected void setScheduler(CFMLEngine engine,Resource scheduleDirectory, LogAndSource logger) throws PageException {
        if(scheduleDirectory==null) {
        	if(this.scheduler==null) this.scheduler=new SchedulerImpl(engine,"<?xml version=\"1.0\"?>\n<schedule></schedule>",this,logger);
        	return;
        }
    	
    	
        if(!isDirectory(scheduleDirectory)) throw new ExpressionException("schedule task directory "+scheduleDirectory+" doesn't exist or is not a directory");
        try {
        	if(this.scheduler==null)
        		this.scheduler=new SchedulerImpl(engine,this,scheduleDirectory,logger,SystemUtil.getCharset().name());
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
    protected void setDataSources(Map<String,DataSource> datasources) {
        this.datasources=datasources;
    }

    /**
     * @param customTagMappings The customTagMapping to set.
     */
    protected void setCustomTagMappings(Mapping[] customTagMappings) {
    	this.customTagMappings = customTagMappings;
    }

    @Override
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
     * is file a directory or not, touch if not exist
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

    @Override
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

    @Override
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

    @Override
    public String getBaseComponentTemplate() {
        return baseComponentTemplate;
    }

    /**
     * @return pagesource of the base component
     */
    public PageSource getBaseComponentPageSource() {
        return getBaseComponentPageSource(ThreadLocalPageContext.get());
    }

    public PageSource getBaseComponentPageSource(PageContext pc) {
        if(baseComponentPageSource==null) {
        	baseComponentPageSource=PageSourceImpl.best(getPageSources(pc,null,getBaseComponentTemplate(),false,false,true));
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

    protected void setDeployLogger(LogAndSource deployLogger) {
        this.deployLogger=deployLogger;
    }

    protected void setScopeLogger(LogAndSource scopeLogger) {
        this.scopeLogger=scopeLogger;
    }

    protected void setMappingLogger(LogAndSource mappingLogger) {
        this.mappingLogger=mappingLogger;
    }
    
    protected void setRestLogger(LogAndSource restLogger) {
        this.restLogger=restLogger;
    }


    protected void setRestList(boolean restList) {
        this.restList=restList;
    }

    public boolean getRestList() {
        return restList;
    }

    /*protected void setRestAllowChanges(boolean restAllowChanges) {
        this.restAllowChanges=restAllowChanges;
    }

    public boolean getRestAllowChanges() {
        return restAllowChanges;
    }*/

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
    
    @Override
    public short getClientType() {
        return this.clientType;
    }
    
    /**
     * @param searchEngine The searchEngine to set.
     */
    protected void setSearchEngine(SearchEngine searchEngine) {
        this.searchEngine = searchEngine;
    }

    @Override
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

    
    @Override
    public String getTimeServer() {
        return timeServer;
    }

    @Override
    public String getComponentDumpTemplate() {
        return componentDumpTemplate;
    }
    
    /**
     * @param template The componentDump template to set.
     */
    protected void setComponentDumpTemplate(String template) {
        this.componentDumpTemplate = template;
    }

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

    @Override
    public String getId() {
    	if(id==null){
    		id = getId(getSecurityKey(),getSecurityToken(),false,securityKey);
    	}
    	return id;
	}

    public static String getId(String key, String token,boolean addMacAddress,String defaultValue) {
    	
		try {
			if(addMacAddress){// because this was new we could swutch to a new ecryption // FUTURE cold we get rid of the old one?
				return Hash.sha256(key+";"+token+":"+SystemUtil.getMacAddress());
			}
			return Md5.getDigestAsString(key+token);
		} 
    	catch (Throwable t) {
			return defaultValue;
		}
	}
    
    public String getSecurityKey() {
    	return securityKey;
    }

    @Override
    public String getDebugTemplate() {
    	throw new PageRuntimeException(new DeprecatedException("no longer supported, use instead getDebugEntry(ip, defaultValue)"));
    }

	@Override
	public String getErrorTemplate(int statusCode) {
		return errorTemplates.get(Caster.toString(statusCode));
	}

	/**
	 * @param errorTemplate the errorTemplate to set
	 */
	protected void setErrorTemplate(int statusCode,String errorTemplate) {
		this.errorTemplates.put(Caster.toString(statusCode), errorTemplate);
	}

    @Override
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

    @Override
    public abstract String getUpdateType() ;

    @Override
    public abstract URL getUpdateLocation();

    @Override
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

    @Override
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

    public boolean isSuppressContent() {
        return suppressContent;
    }
    
    protected void setSuppressContent(boolean suppressContent) {
        this.suppressContent = suppressContent;
    }

	@Override
	public String getDefaultEncoding() {
		return webCharset;
	}
	
	@Override
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

	@Override
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

	@Override
	public String getResourceCharset() {
		return resourceCharset;
	}
	
	/**
	 * sets the charset for the response stream
	 * @param webCharset
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
    
    @Override
	public DataSource[] getDataSources() {
		Map<String, DataSource> map = getDataSourcesAsMap();
		Iterator<DataSource> it = map.values().iterator();
		DataSource[] ds = new DataSource[map.size()];
		int count=0;
		
		while(it.hasNext()) {
			ds[count++]=it.next();
		}
		return ds;
	}
	
	public Map<String,DataSource> getDataSourcesAsMap() {
        Map<String,DataSource> map=new HashMap<String, DataSource>();
        Iterator<Entry<String, DataSource>> it = datasources.entrySet().iterator();
        Entry<String, DataSource> entry;
        while(it.hasNext()) {
            entry = it.next();
            if(!entry.getKey().equals(QOQ_DATASOURCE_NAME))
                map.put(entry.getKey(),entry.getValue());
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
	 * @param mailDefaultEncoding the mailDefaultCharset to set
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

	protected void setDefaultResourceProvider(Class defaultProviderClass, Map arguments) throws ClassException {
		Object o=ClassUtil.loadInstance(defaultProviderClass);
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
		Object o=ClassUtil.loadInstance(strProviderClass);
		
		if(o instanceof ResourceProvider) {
			ResourceProvider rp=(ResourceProvider) o;
			rp.init(strProviderScheme,arguments);
			addResourceProvider(rp);
		}
		else 
			throw new ClassException("object ["+Caster.toClassName(o)+"] must implement the interface "+ResourceProvider.class.getName());
	}

	protected void addResourceProvider(String strProviderScheme, Class providerClass, Map arguments) throws ClassException {
		Object o=ClassUtil.loadInstance(providerClass);
		
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


	@Override
	public Resource getResource(String path) {
		return resources.getResource(path);
	}

	@Override
	public ApplicationListener getApplicationListener() {
		return applicationListener;
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
	public ProxyData getProxyData() {
		return proxy;
	}

	/**
	 * @param proxy the proxyPassword to set
	 */
	protected void setProxyData(ProxyData proxy) {
		this.proxy = proxy;
	}

	@Override
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

	@Override
	public Resource getClientScopeDir() {
		if(clientScopeDir==null) clientScopeDir=getConfigDir().getRealResource("client-scope");
		return clientScopeDir;
	}

	public Resource getSessionScopeDir() {
		if(sessionScopeDir==null) sessionScopeDir=getConfigDir().getRealResource("session-scope");
		return sessionScopeDir;
	}

	/*public int getClientScopeMaxAge() {
		return clientScopeMaxAge;
	}
	
	public void setClientScopeMaxAge(int age) {
		this. clientScopeMaxAge=age;
	}*/

	@Override
	public long getClientScopeDirSize() {
		return clientScopeDirSize;
	}
	public long getSessionScopeDirSize() {
		return sessionScopeDirSize;
	}

	/**
	 * @param clientScopeDir the clientScopeDir to set
	 */
	protected void setClientScopeDir(Resource clientScopeDir) {
		this.clientScopeDir = clientScopeDir;
	}
	
	protected void setSessionScopeDir(Resource sessionScopeDir) {
		this.sessionScopeDir = sessionScopeDir;
	}

	/**
	 * @param clientScopeDirSize the clientScopeDirSize to set
	 */
	protected void setClientScopeDirSize(long clientScopeDirSize) {
		this.clientScopeDirSize = clientScopeDirSize;
	}

	@Override
	public ClassLoader getRPCClassLoader(boolean reload) throws IOException {
		
		if(rpcClassLoader!=null && !reload) return rpcClassLoader;
        
		Resource dir = getDeployDirectory().getRealResource("RPC");
		if(!dir.exists())dir.createDirectory(true);
		//rpcClassLoader = new PhysicalClassLoader(dir,getFactory().getServlet().getClass().getClassLoader());
		rpcClassLoader = new PhysicalClassLoader(dir,getClassLoader());
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
	
	@Override
	public DumpWriter getDefaultDumpWriter(int defaultType) {
		DumpWriterEntry[] entries = getDumpWritersEntries();
		if(entries!=null)for(int i=0;i<entries.length;i++){
			if(entries[i].getDefaultType()==defaultType) {
				return entries[i].getWriter();
			}
		}
		return new HTMLDumpWriter();
	}

	@Override
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
	
	@Override
	public boolean useComponentShadow() {
		return useComponentShadow;
	}

	public boolean useComponentPathCache() {
		return useComponentPathCache;
	}
	
	public boolean useCTPathCache() {
		return useCTPathCache;
	}
	
	public void flushComponentPathCache() {
		if(componentPathCache!=null)componentPathCache.clear();
	}
	
	public void flushCTPathCache() {
		if(ctPatchCache!=null)ctPatchCache.clear();
	}
	

	protected void setUseCTPathCache(boolean useCTPathCache) {
		this.useCTPathCache = useCTPathCache;
	}
	protected void setUseComponentPathCache(boolean useComponentPathCache) {
		this.useComponentPathCache = useComponentPathCache;
	}

	/**
	 * @param useComponentShadow the useComponentShadow to set
	 */
	protected void setUseComponentShadow(boolean useComponentShadow) {
		this.useComponentShadow = useComponentShadow;
	}
	
	@Override
	public DataSource getDataSource(String datasource) throws DatabaseException {
		DataSource ds=(datasource==null)?null:(DataSource) datasources.get(datasource.toLowerCase());
		if(ds!=null) return ds;
		
		
		// create error detail
		DatabaseException de = new DatabaseException("datasource ["+datasource+"] doesn't exist",null,null,null);
		de.setDetail(ExceptionUtil.createSoundexDetail(datasource,datasources.keySet().iterator(),"datasource names"));
		de.setAdditional(KeyConstants._Datasource,datasource);
		throw de;
	}
	
	@Override
	public DataSource getDataSource(String datasource, DataSource defaultValue) {
		DataSource ds=(datasource==null)?null:(DataSource) datasources.get(datasource.toLowerCase());
		if(ds!=null) return ds;
		return defaultValue;
	}

	@Override
	public PrintWriter getErrWriter() {
		return err;
	}

	/**
	 * @param err the err to set
	 */
	protected void setErr(PrintWriter err) {
		this.err = err;
	}

	@Override
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
	
	@Override
	public String[] getCustomTagExtensions() {
		return customTagExtensions;
	}
	
	protected void setCustomTagExtensions(String[] customTagExtensions) {
		this.customTagExtensions = customTagExtensions;
	}
	
	protected void setDoLocalCustomTag(boolean doLocalCustomTag) {
		this.doLocalCustomTag= doLocalCustomTag;
	}
	

	public boolean doComponentDeepSearch() {
		return doComponentTagDeepSearch;
	}
	
	protected void setDoComponentDeepSearch(boolean doComponentTagDeepSearch) {
		this.doComponentTagDeepSearch = doComponentTagDeepSearch;
	}
	
	@Override
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
	

	public boolean allowCompression() {
		return allowCompression;
	}
	protected void setAllowCompression(boolean allowCompression) {
		this.allowCompression= allowCompression;
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

	@Override
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
	 * @param strLocalMode the localMode to set
	 */
	protected void setLocalMode(String strLocalMode) {
		this.localMode=AppListenerUtil.toLocalMode(strLocalMode,this.localMode);
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
	 * @param clusterClass the classClusterScope to set
	 */
	protected void setClusterClass(Class clusterClass) {
		this.clusterClass = clusterClass;
	}

	@Override
	public Struct getRemoteClientUsage() {
		if(remoteClientUsage==null)remoteClientUsage=new StructImpl();
		return remoteClientUsage;
	}
	
	protected void setRemoteClientUsage(Struct remoteClientUsage) {
		this.remoteClientUsage=remoteClientUsage;
	}

	@Override
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
	
	@Override
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
	
	public void setAMFConfigType(String strDeploy) {
		if(!StringUtil.isEmpty(strDeploy)){
			if("xml".equalsIgnoreCase(strDeploy))amfConfigType=AMF_CONFIG_TYPE_XML;
			else if("manual".equalsIgnoreCase(strDeploy))amfConfigType=AMF_CONFIG_TYPE_MANUAL;
		}
	}
	public void setAMFConfigType(int amfDeploy) {
		this.amfConfigType=amfDeploy;
	}
	public int getAMFConfigType() {
		return amfConfigType;
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
		//this.defaultDataSource=defaultDataSource;
	}

	/**
	 * @return the inspectTemplate 
	 */
	public short getInspectTemplate() {
		return inspectTemplate;
	}

	/**
	 * @param inspectTemplate the inspectTemplate to set
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

	protected void setCaches(Map<String,CacheConnection> caches) {
		this.caches=caches;
		Iterator<Entry<String, CacheConnection>> it = caches.entrySet().iterator();
		Entry<String, CacheConnection> entry;
		CacheConnection cc;
		while(it.hasNext()){
			entry = it.next();
			cc=entry.getValue();
			if(cc.getName().equalsIgnoreCase(cacheDefaultConnectionNameTemplate)){
				defaultCacheTemplate=cc;
			}
			else if(cc.getName().equalsIgnoreCase(cacheDefaultConnectionNameFunction)){
				defaultCacheFunction=cc;
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
	
	@Override
	public Map<String,CacheConnection> getCacheConnections() {
		return caches;
	}

	@Override
	public CacheConnection getCacheDefaultConnection(int type) {
		if(type==CACHE_DEFAULT_FUNCTION)	return defaultCacheFunction;
		if(type==CACHE_DEFAULT_OBJECT)		return defaultCacheObject;
		if(type==CACHE_DEFAULT_TEMPLATE)	return defaultCacheTemplate;
		if(type==CACHE_DEFAULT_QUERY)		return defaultCacheQuery;
		if(type==CACHE_DEFAULT_RESOURCE)	return defaultCacheResource;
		return null;
	}

	protected void setCacheDefaultConnectionName(int type,String cacheDefaultConnectionName) {
		if(type==CACHE_DEFAULT_FUNCTION)		cacheDefaultConnectionNameFunction=cacheDefaultConnectionName;
		else if(type==CACHE_DEFAULT_OBJECT)		cacheDefaultConnectionNameObject=cacheDefaultConnectionName;
		else if(type==CACHE_DEFAULT_TEMPLATE)	cacheDefaultConnectionNameTemplate=cacheDefaultConnectionName;
		else if(type==CACHE_DEFAULT_QUERY)		cacheDefaultConnectionNameQuery=cacheDefaultConnectionName;
		else if(type==CACHE_DEFAULT_RESOURCE)	cacheDefaultConnectionNameResource=cacheDefaultConnectionName;
	}
	
	@Override
	public String getCacheDefaultConnectionName(int type) {
		if(type==CACHE_DEFAULT_FUNCTION)	return cacheDefaultConnectionNameFunction;
		if(type==CACHE_DEFAULT_OBJECT)		return cacheDefaultConnectionNameObject;
		if(type==CACHE_DEFAULT_TEMPLATE)	return cacheDefaultConnectionNameTemplate;
		if(type==CACHE_DEFAULT_QUERY)		return cacheDefaultConnectionNameQuery;
		if(type==CACHE_DEFAULT_RESOURCE)	return cacheDefaultConnectionNameResource;
		return null;
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
	
	public ORMEngine resetORMEngine(PageContext pc, boolean force) throws PageException {
		//String name = pc.getApplicationContext().getName();
		//ormengines.remove(name);
		ORMEngine e = getORMEngine(pc);
		e.reload(pc,force);
		return e;
	}
	
	public ORMEngine getORMEngine(PageContext pc) throws PageException {
		String name = pc.getApplicationContext().getName();
		
		ORMEngine engine = ormengines.get(name);
		if(engine==null){
			//try {
			Throwable t=null;
			
			try {
				engine=(ORMEngine)ClassUtil.loadInstance(ormEngineClass);
				engine.init(pc);
			}
			catch (ClassException ce) {
				t=ce;	
			}
			catch (NoClassDefFoundError ncfe) {
				t=ncfe;
			}
			
			if(t!=null) {
				
				
				// try to load orm jars
				//if(JarLoader.changed(pc.getConfig(), Admin.ORM_JARS))
				//	throw new ApplicationException(
				//		"cannot initialize ORM Engine ["+ormEngineClass.getName()+"], make sure you have added all the required jar files");
				ApplicationException ae = new ApplicationException(
							"cannot initialize ORM Engine ["+ormEngineClass.getName()+"], make sure you have added all the required jar files");
				
				ae.setStackTrace(t.getStackTrace());
				ae.setDetail(t.getMessage());
				
				
			
			}
				ormengines.put(name,engine);
			/*}
			catch (PageException pe) {
				throw pe;
			}*/
		}
		
		return engine; 
	}
	
	public Class<ORMEngine> getORMEngineClass() {
		return ormEngineClass; 
	}
	
	@Override
	public Mapping[] getComponentMappings() {
		return componentMappings;
	}

	/**
	 * @param componentMappings the componentMappings to set
	 */
	protected void setComponentMappings(Mapping[] componentMappings) {
		this.componentMappings = componentMappings;
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
				null,ConfigImpl.INSPECT_UNDEFINED,true,false,false,false,true,true,null
				);
			customTagAppMappings.put(physical.toLowerCase(),m);
		}
		
		return m;
	}


	private Map<String,PageSource> componentPathCache=null;//new ArrayList<Page>();
	private Map<String,InitFile> ctPatchCache=null;//new ArrayList<Page>();
	private Map<String,UDF> udfCache=new ReferenceMap();

	
	public Page getCachedPage(PageContext pc,String pathWithCFC) throws PageException {
		if(componentPathCache==null) return null; 
		
		PageSource ps = componentPathCache.get(pathWithCFC.toLowerCase());
		if(ps==null) return null;
		return ((PageSourceImpl)ps).loadPage(pc,(Page)null);
	}
	
	public void putCachedPageSource(String pathWithCFC,PageSource ps) {
		if(componentPathCache==null) componentPathCache=Collections.synchronizedMap(new HashMap<String, PageSource>());//MUSTMUST new ReferenceMap(ReferenceMap.SOFT,ReferenceMap.SOFT); 
		componentPathCache.put(pathWithCFC.toLowerCase(),ps);
	}
	
	public InitFile getCTInitFile(PageContext pc,String key) {
		if(ctPatchCache==null) return null; 
		
		InitFile initFile = ctPatchCache.get(key.toLowerCase());
		if(initFile!=null){
			if(MappingImpl.isOK(initFile.getPageSource()))return initFile;
			ctPatchCache.remove(key.toLowerCase());
		}
		return null;
	}
	
	public void putCTInitFile(String key,InitFile initFile) {
		if(ctPatchCache==null) ctPatchCache=Collections.synchronizedMap(new HashMap<String, InitFile>());//MUSTMUST new ReferenceMap(ReferenceMap.SOFT,ReferenceMap.SOFT); 
		ctPatchCache.put(key.toLowerCase(),initFile);
	}

	public Struct listCTCache() {
		Struct sct=new StructImpl();
		if(ctPatchCache==null) return sct; 
		Iterator<Entry<String, InitFile>> it = ctPatchCache.entrySet().iterator();
		
		Entry<String, InitFile> entry;
		while(it.hasNext()){
			entry = it.next();
			sct.setEL(entry.getKey(),entry.getValue().getPageSource().getDisplayPath());
		}
		return sct;
	}
	
	public void clearCTCache() {
		if(ctPatchCache==null) return; 
		ctPatchCache.clear();
	}

	public void clearFunctionCache() {
		udfCache.clear();
	}

	public UDF getFromFunctionCache(String key) {
		return udfCache.get(key);
	}

	public void putToFunctionCache(String key,UDF udf) {
		udfCache.put(key, udf);
	}
	
	public Struct listComponentCache() {
		Struct sct=new StructImpl();
		if(componentPathCache==null) return sct; 
		Iterator<Entry<String, PageSource>> it = componentPathCache.entrySet().iterator();
		
		Entry<String, PageSource> entry;
		while(it.hasNext()){
			entry = it.next();
			sct.setEL(entry.getKey(),entry.getValue().getDisplayPath());
		}
		return sct;
	}
	
	public void clearComponentCache() {
		if(componentPathCache==null) return; 
		componentPathCache.clear();
	}

	public ImportDefintion getComponentDefaultImport() {
		return componentDefaultImport;
	}

	protected void setComponentDefaultImport(String str) {
		ImportDefintion cdi = ImportDefintionImpl.getInstance(str, null);
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
	 * @param componentRootSearch the componentLocalSearch to set
	 */
	protected void setComponentRootSearch(boolean componentRootSearch) {
		this.componentRootSearch = componentRootSearch;
	}

	private final Map compressResources= new ReferenceMap(ReferenceMap.SOFT,ReferenceMap.SOFT);


	public Compress getCompressInstance(Resource zipFile, int format, boolean caseSensitive) {
		Compress compress=(Compress) compressResources.get(zipFile.getPath());
		if(compress==null) {
			compress=new Compress(zipFile,format,caseSensitive);
			compressResources.put(zipFile.getPath(), compress);
		}
		return compress;
	}

	public boolean getSessionCluster() {
		return false;
	}

	public boolean getClientCluster() {
		return false;
	}
	
	public String getClientStorage() {
		return clientStorage;
	}
	
	public String getSessionStorage() {
		return sessionStorage;
	}
	
	protected void setClientStorage(String clientStorage) {
		this.clientStorage = clientStorage;
	}
	
	protected void setSessionStorage(String sessionStorage) {
		this.sessionStorage = sessionStorage;
	}
	
	
	
	private Map<String,ComponentMetaData> componentMetaData=null;
	public ComponentMetaData getComponentMetadata(String key) {
		if(componentMetaData==null) return null;
		return componentMetaData.get(key.toLowerCase());
	}

	public void putComponentMetadata(String key,ComponentMetaData data) {
		if(componentMetaData==null) componentMetaData=new HashMap<String, ComponentMetaData>();
		componentMetaData.put(key.toLowerCase(),data);
	}
	
	public void clearComponentMetadata() {
		if(componentMetaData==null) return; 
		componentMetaData.clear();
	}
	
	public static class ComponentMetaData {

		public final Struct meta;
		public final long lastMod;

		public ComponentMetaData(Struct meta, long lastMod) {
			this.meta=meta;
			this.lastMod=lastMod;
		}
	}
 
	private DebugEntry[] debugEntries;
	protected void setDebugEntries(DebugEntry[] debugEntries) {
		this.debugEntries=debugEntries;
	}

	public DebugEntry[] getDebugEntries() {
		if(debugEntries==null)debugEntries=new DebugEntry[0];
		return debugEntries;
	}
	
	public DebugEntry getDebugEntry(String ip, DebugEntry defaultValue) {
		if(debugEntries.length==0) return defaultValue;
		short[] sarr;

		try {
			sarr = IPRange.toShortArray(ip);
		} catch (IOException e) {
			return defaultValue;
		}

		for(int i=0;i<debugEntries.length;i++){
			if(debugEntries[i].getIpRange().inRange(sarr)) return debugEntries[i];
		}
		
		return defaultValue;
	}

	private int debugMaxRecordsLogged=10;
	protected void setDebugMaxRecordsLogged(int debugMaxRecordsLogged) {
		this.debugMaxRecordsLogged=debugMaxRecordsLogged;
	}

	public int getDebugMaxRecordsLogged() {
		return debugMaxRecordsLogged;
	}

	private boolean dotNotationUpperCase=true;
	protected void setDotNotationUpperCase(boolean dotNotationUpperCase) {
		this.dotNotationUpperCase=dotNotationUpperCase;
	}

	public boolean getDotNotationUpperCase() {
		return dotNotationUpperCase;
	}

	private boolean getSupressWSBeforeArg=true;
	protected void setSupressWSBeforeArg(boolean getSupressWSBeforeArg) {
		this.getSupressWSBeforeArg=getSupressWSBeforeArg;
	}

	public boolean getSupressWSBeforeArg() {
		return getSupressWSBeforeArg;
	}

	private RestSettings restSetting=new RestSettingImpl(false,UDF.RETURN_FORMAT_JSON);
	protected void setRestSetting(RestSettings restSetting){
		this.restSetting= restSetting;
	}
	
	@Override
	public RestSettings getRestSetting(){
		return restSetting; 
	}

	protected void setMode(int mode) {
		this.mode=mode;
	}

	public int getMode() {
		return mode;
	}

	// do not move to Config interface, do instead getCFMLWriterClass
	protected void setCFMLWriterType(int writerType) {
		this.writerType=writerType;
	}

	// do not move to Config interface, do instead setCFMLWriterClass
	public int getCFMLWriterType() {
		return writerType;
	}

	private boolean bufferOutput=true;


	private int externalizeStringGTE=-1;
	public boolean getBufferOutput() {
		return bufferOutput;
	}

	protected void setBufferOutput(boolean bufferOutput) {
		this.bufferOutput= bufferOutput;
	}

	public int getDebugOptions() {
		return debugOptions;
	}
	
	public boolean hasDebugOptions(int debugOption) {
		return (debugOptions&debugOption)>0  ;
	}
	
	protected void setDebugOptions(int debugOptions) {
		this.debugOptions = debugOptions;
	}

	public static Mapping[] getAllMappings(PageContext pc) {
		List<Mapping> list=new ArrayList<Mapping>();
		getAllMappings(list,pc.getConfig().getMappings());
		getAllMappings(list,pc.getConfig().getCustomTagMappings());
		getAllMappings(list,pc.getConfig().getComponentMappings());
		getAllMappings(list,pc.getApplicationContext().getMappings());
		return list.toArray(new Mapping[list.size()]);
	}
	
	public static Mapping[] getAllMappings(ConfigWeb cw) {
		List<Mapping> list=new ArrayList<Mapping>();
		getAllMappings(list,cw.getMappings());
		getAllMappings(list,cw.getCustomTagMappings());
		getAllMappings(list,cw.getComponentMappings());
		return list.toArray(new Mapping[list.size()]);
	}

	private static void getAllMappings(List<Mapping> list, Mapping[] mappings) {
		if(!ArrayUtil.isEmpty(mappings))for(int i=0;i<mappings.length;i++)	{
			list.add(mappings[i]);
		}
	}

	protected void setCheckForChangesInConfigFile(boolean checkForChangesInConfigFile) {
		this.checkForChangesInConfigFile=checkForChangesInConfigFile;
	}

	public boolean checkForChangesInConfigFile() {
		return checkForChangesInConfigFile;
	}


    public abstract int getLoginDelay();

    public abstract boolean getLoginCaptcha();

    public abstract boolean getFullNullSupport();

    public abstract Cluster createClusterScope() throws PageException;

	protected void setApiKey(String apiKey) {
		this.apiKey=apiKey;
	}
	
	public String getApiKey() {
		return apiKey;
	}

	protected void setExternalizeStringGTE(int externalizeStringGTE) {
		this.externalizeStringGTE=externalizeStringGTE;
	}
	public int getExternalizeStringGTE() {
		return externalizeStringGTE;
	}
	
}