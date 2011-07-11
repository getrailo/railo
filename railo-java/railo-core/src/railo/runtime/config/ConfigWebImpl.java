package railo.runtime.config;

import java.net.URL;
import java.util.Enumeration;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;

import org.apache.commons.collections.map.ReferenceMap;

import railo.commons.io.SystemUtil;
import railo.commons.io.log.Log;
import railo.commons.io.log.LogAndSource;
import railo.commons.io.log.LogAndSourceImpl;
import railo.commons.io.log.LogConsole;
import railo.commons.io.res.Resource;
import railo.commons.io.res.ResourceProvider;
import railo.commons.io.res.ResourcesImpl;
import railo.commons.lang.StringKeyLock;
import railo.commons.lang.StringUtil;
import railo.runtime.CFMLFactoryImpl;
import railo.runtime.Mapping;
import railo.runtime.MappingImpl;
import railo.runtime.Page;
import railo.runtime.PageContext;
import railo.runtime.PageSourceImpl;
import railo.runtime.cfx.CFXTagPool;
import railo.runtime.compiler.CFMLCompilerImpl;
import railo.runtime.debug.DebuggerPool;
import railo.runtime.engine.CFMLEngineImpl;
import railo.runtime.engine.ThreadQueueImpl;
import railo.runtime.exp.ExpressionException;
import railo.runtime.exp.PageException;
import railo.runtime.exp.SecurityException;
import railo.runtime.gateway.GatewayEngineImpl;
import railo.runtime.gateway.GatewayEntry;
import railo.runtime.lock.LockManager;
import railo.runtime.lock.LockManagerImpl;
import railo.runtime.security.SecurityManager;
import railo.runtime.security.SecurityManagerImpl;
import railo.runtime.tag.TagHandlerPool;

/**
 * Web Context
 */
public final class ConfigWebImpl extends ConfigImpl implements ServletConfig, ConfigWeb {
    
    private ServletConfig config;
    private ConfigServerImpl configServer;
    private SecurityManager securityManager;
    private final LockManager lockManager= LockManagerImpl.getInstance();
    private Resource rootDir;
    private CFMLCompilerImpl compiler=new CFMLCompilerImpl();
    private Page baseComponentPage;
	private MappingImpl serverTagMapping;
	private MappingImpl serverFunctionMapping;
	private StringKeyLock contextLock;
	private GatewayEngineImpl gatewayEngine;
    private LogAndSource gatewayLogger=null;//new LogAndSourceImpl(LogConsole.getInstance(Log.LEVEL_INFO),"");private DebuggerPool debuggerPool;
    private DebuggerPool debuggerPool;
	

    //private File deployDirectory;

    /**
     * constructor of the class
     * @param configServer
     * @param config
     * @param configDir
     * @param configFile
     * @param cloneServer 
     */
    protected ConfigWebImpl(CFMLFactoryImpl factory,ConfigServerImpl configServer, ServletConfig config, Resource configDir, Resource configFile) {
    	super(factory,configDir, configFile,configServer.getTLDs(),configServer.getFLDs());
    	this.configServer=configServer;
        this.config=config;
        factory.setConfig(this);
    	ResourceProvider frp = ResourcesImpl.getFileResourceProvider();
        
        this.rootDir=frp.getResource(config.getServletContext().getRealPath("/"));
        
        
        // Fix for tomcat
        if(this.rootDir.getName().equals(".") || this.rootDir.getName().equals(".."))
        	this.rootDir=this.rootDir.getParentResource();
    }
    
    public void reset() {
    	super.reset();
    	tagHandlerPool.reset();
    	contextLock=null;
    }
    
    /* *
     * Constructor of the class, used for configserver dummy instance
     * @param factory
     * @param configServer
     * @param configx
     * @param configDir
     * @param configFile
     * /
    protected ConfigWebImpl(CFMLFactoryImpl factory,ConfigServerImpl configServer, Resource configDir, Resource configFile,Resource rootDir) {
    	super(factory,configDir, configFile,configServer.getTLDs(),configServer.getFLDs());
    	this.configServer=configServer;
        factory.setConfig(this);
    	
        this.rootDir=rootDir;
        
        // Fix for tomcat
        if(this.rootDir.getName().equals(".") || this.rootDir.getName().equals(".."))
        	this.rootDir=this.rootDir.getParentResource();
    }*/
    
    

    /**
     * @see javax.servlet.ServletConfig#getServletName()
     */
    public String getServletName() {
        return config.getServletName();
    }

    /**
     * @see javax.servlet.ServletConfig#getServletContext()
     */
    public ServletContext getServletContext() {
        return config.getServletContext();
    }

    /**
     * @see javax.servlet.ServletConfig#getInitParameter(java.lang.String)
     */
    public String getInitParameter(String name) {
        return config.getInitParameter(name);
    }

    /**
     * @see javax.servlet.ServletConfig#getInitParameterNames()
     */
    public Enumeration getInitParameterNames() {
        return config.getInitParameterNames();
    }

    /**
     * @see railo.runtime.config.ConfigImpl#getConfigServerImpl()
     */
    public ConfigServerImpl getConfigServerImpl() {
        return configServer;
    }
    

    public ConfigServer getConfigServer() {
    	//throw new PageRuntimeException(new SecurityException("access on server config without password denied"));
        return configServer;
    }
    
    /**
     * @see railo.runtime.config.ConfigImpl#getConfigServer(java.lang.String)
     */
    public ConfigServer getConfigServer(String password) throws ExpressionException {
        if(!configServer.hasPassword())
            throw new ExpressionException("Cannot access, no password is defined");
        if(!configServer.getPassword().equalsIgnoreCase(password))
            throw new ExpressionException("No access, password is invalid");
        return configServer;
    }
    
    public String getServerId() {
        return configServer.getId();
    }

    public String getServerSecurityKey() {
        return configServer.getSecurityKey();
    }
    
    public Resource getServerConfigDir() {
        return configServer.getConfigDir();
    }
    

    /**
     * @return Returns the accessor.
     */
    public SecurityManager getSecurityManager() {
        return securityManager;
    }

    /**
     * @param securityManager The accessor to set.
     */
    protected void setSecurityManager(SecurityManager securityManager) {
    	((SecurityManagerImpl)securityManager).setRootDirectory(getRootDirectory());
        this.securityManager = securityManager;
    }
    
    /**
     * @throws SecurityException 
     * @see railo.runtime.config.ConfigImpl#getCFXTagPool()
     */
    public CFXTagPool getCFXTagPool() throws SecurityException {
        if(securityManager.getAccess(SecurityManager.TYPE_CFX_USAGE)==SecurityManager.VALUE_YES) return super.getCFXTagPool();
        throw new SecurityException("no access to cfx functionality", "disabled by security settings");
    }

    /**
     * @return Returns the rootDir.
     */
    public Resource getRootDirectory() {
        return rootDir;
    }

    /**
     * @see railo.runtime.config.Config#getUpdateType()
     */
    public String getUpdateType() {
        return configServer.getUpdateType();
    }

    /**
     * @see railo.runtime.config.Config#getUpdateLocation()
     */
    public URL getUpdateLocation() {
        return configServer.getUpdateLocation();
    }

    /**
     * @see railo.runtime.config.ConfigWeb#getLockManager()
     */
    public LockManager getLockManager() {
        return lockManager;
    }

	/**
	 * @return the compiler
	 */
	public CFMLCompilerImpl getCompiler() {
		return compiler;
	}
	
	 public Page getBaseComponentPage(PageContext pc) throws PageException {
	        if(baseComponentPage==null) {
	            baseComponentPage=((PageSourceImpl)getBaseComponentPageSource(pc)).loadPage(pc);
				
	        }
	        return baseComponentPage;
	    }
	    public void resetBaseComponentPage() {
	        baseComponentPage=null;
	    }
	    


		

	    public Mapping getServerTagMapping() {
	    	if(serverTagMapping==null){
	    		serverTagMapping=getConfigServerImpl().tagMapping.cloneReadOnly(this);
	    	}
			return serverTagMapping;
		}
	    public Mapping getServerFunctionMapping() {
	    	if(serverFunctionMapping==null){
	    		serverFunctionMapping=getConfigServerImpl().functionMapping.cloneReadOnly(this);
	    	}
			return serverFunctionMapping;
		}
	    private Map applicationMappings=new ReferenceMap();
		private TagHandlerPool tagHandlerPool=new TagHandlerPool();
		public Mapping getApplicationMapping(String virtual, String physical) {
			String key=virtual.toLowerCase()+physical.toLowerCase();
			Mapping m=(Mapping) applicationMappings.get(key);
			if(m==null){
				m=new MappingImpl(this,
					virtual,
					physical,
					null,false,true,false,false,false,true,false
					);
				applicationMappings.put(key, m);
			}
			return m;
		}

		public CFMLEngineImpl getCFMLEngineImpl() {
			return getConfigServerImpl().getCFMLEngineImpl();
		}

		public String getLabel() {
			String hash=getHash();
			String label=hash;
			Map<String, String> labels = configServer.getLabels();
			if(labels!=null) {
				String l = labels.get(hash);
				if(!StringUtil.isEmpty(l)) {
					label=l;
				}
			}
			return label;
		}
		
		public String getHash() {
			return SystemUtil.hash(getServletContext());
		}

		public StringKeyLock getContextLock() {
			if(contextLock==null) {
				contextLock=new StringKeyLock(getRequestTimeout().getMillis());
			}
			return contextLock;
		}


		protected void setGatewayEntries(Map<String, GatewayEntry> gatewayEntries) {
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
		public void setGatewayEngine(GatewayEngineImpl gatewayEngine) {
			this.gatewayEngine=gatewayEngine;
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
		/* *
		 * this is a config web that reflect the configServer, this allows to run cfml code on server level
		 * @param gatewayEngine 
		 * @return
		 * @throws PageException
		 * /
		public ConfigWeb createGatewayConfig(GatewayEngineImpl gatewayEngine) {
			QueryCacheSupport cqc = QueryCacheSupport.getInstance(this);
			CFMLEngineImpl engine = getConfigServerImpl().getCFMLEngineImpl();
			CFMLFactoryImpl factory = new CFMLFactoryImpl(engine,cqc);
			
			ServletContextDummy sContext = new ServletContextDummy(
					this,
					getRootDirectory(),
					new StructImpl(),
					new StructImpl(),
					1,1);
			ServletConfigDummy sConfig = new ServletConfigDummy(sContext,"CFMLServlet");
			ConfigWebImpl cwi = new ConfigWebImpl(
					factory,
					getConfigServerImpl(),
					sConfig,
					getConfigDir(),
					getConfigFile(),true);
			cqc.setConfigWeb(cwi);
			try {
				ConfigWebFactory.createContextFiles(getConfigDir(),sConfig);
		        ConfigWebFactory.load(getConfigServerImpl(), cwi, ConfigWebFactory.loadDocument(getConfigFile()),true);
		        ConfigWebFactory.createContextFilesPost(getConfigDir(),cwi,sConfig,true);
			}
			catch (Exception e) {
				e.printStackTrace();
			}
			
			cwi.setGatewayEngine(gatewayEngine);
			
			//cwi.setGatewayMapping(new MappingImpl(cwi,"/",gatewayEngine.getCFCDirectory().getAbsolutePath(),null,false,true,false,false,false));
			return cwi;
		}*/

		public TagHandlerPool getTagHandlerPool() {
			return tagHandlerPool;
		}

		public DebuggerPool getDebuggerPool() {
			if(debuggerPool==null){
				Resource dir = getConfigDir().getRealResource("debugger");
				dir.mkdirs();
				debuggerPool=new DebuggerPool(dir);
			}
			return debuggerPool;
		}
		

		public ThreadQueueImpl getThreadQueue() {
			return configServer.getThreadQueue();
		}

}
