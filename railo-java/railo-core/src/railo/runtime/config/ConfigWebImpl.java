package railo.runtime.config;

import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.map.ReferenceMap;
import org.xml.sax.SAXException;

import railo.commons.io.SystemUtil;
import railo.commons.io.log.Log;
import railo.commons.io.log.LogAndSource;
import railo.commons.io.log.LogAndSourceImpl;
import railo.commons.io.log.LogConsole;
import railo.commons.io.res.Resource;
import railo.commons.io.res.ResourceProvider;
import railo.commons.io.res.ResourcesImpl;
import railo.commons.lang.ClassException;
import railo.commons.lang.StringUtil;
import railo.commons.lock.KeyLock;
import railo.runtime.CFMLFactoryImpl;
import railo.runtime.Mapping;
import railo.runtime.MappingImpl;
import railo.runtime.Page;
import railo.runtime.PageContext;
import railo.runtime.PageSourceImpl;
import railo.runtime.cfx.CFXTagPool;
import railo.runtime.compiler.CFMLCompilerImpl;
import railo.runtime.debug.DebuggerPool;
import railo.runtime.engine.ThreadQueueImpl;
import railo.runtime.exp.ExpressionException;
import railo.runtime.exp.PageException;
import railo.runtime.exp.SecurityException;
import railo.runtime.gateway.GatewayEngineImpl;
import railo.runtime.gateway.GatewayEntry;
import railo.runtime.lock.LockManager;
import railo.runtime.lock.LockManagerImpl;
import railo.runtime.monitor.IntervallMonitor;
import railo.runtime.monitor.RequestMonitor;
import railo.runtime.security.SecurityManager;
import railo.runtime.security.SecurityManagerImpl;
import railo.runtime.tag.TagHandlerPool;
import railo.runtime.type.scope.Cluster;
import railo.runtime.writer.CFMLWriter;
import railo.runtime.writer.CFMLWriterImpl;
import railo.runtime.writer.CFMLWriterWhiteSpace;
import railo.runtime.writer.CFMLWriterWhiteSpacePref;
import railo.transformer.library.function.FunctionLibException;
import railo.transformer.library.tag.TagLibException;

/**
 * Web Context
 */
public final class ConfigWebImpl extends ConfigImpl implements ServletConfig, ConfigWeb {
    
    private ServletConfig config;
    private ConfigServerImpl configServer;
    private SecurityManager securityManager;
    private static final LockManager lockManager= LockManagerImpl.getInstance(false);
    private Resource rootDir;
    private CFMLCompilerImpl compiler=new CFMLCompilerImpl();
    private Page baseComponentPage;
	private MappingImpl serverTagMapping;
	private MappingImpl serverFunctionMapping;
	private KeyLock<String> contextLock;
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
    protected ConfigServerImpl getConfigServerImpl() {
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
	    private Map<String,Mapping> applicationMappings=new ReferenceMap();
		private TagHandlerPool tagHandlerPool=new TagHandlerPool();
		public Mapping getApplicationMapping(String virtual, String physical) {
			String key=virtual.toLowerCase()+physical.toLowerCase();
			Mapping m= applicationMappings.get(key);
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

		public KeyLock<String> getContextLock() {
			if(contextLock==null) {
				contextLock=new KeyLock<String>();
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

		@Override
		public int getLoginDelay() {
			return configServer.getLoginDelay();
		}

		@Override
		public boolean getLoginCaptcha() {
			return configServer.getLoginCaptcha();
		}
		
		@Override
		public Resource getSecurityDirectory(){
			return configServer.getSecurityDirectory();
		}
		
		@Override
		public boolean isMonitoringEnabled(){
			return configServer.isMonitoringEnabled();
		}
		

		
		public RequestMonitor[] getRequestMonitors(){
			return configServer.getRequestMonitors();
		}
		
		public RequestMonitor getRequestMonitor(String name) throws PageException{
			return configServer.getRequestMonitor(name);
		}
		
		public IntervallMonitor[] getIntervallMonitors(){
			return configServer.getIntervallMonitors();
		}

		public IntervallMonitor getIntervallMonitor(String name) throws PageException{
			return configServer.getIntervallMonitor(name);
		}
		
		@Override
		public void checkPermGenSpace(boolean check) {
			configServer.checkPermGenSpace(check);
		}

		@Override
		public Cluster createClusterScope() throws PageException {
			return configServer.createClusterScope();
		}

		@Override
		public boolean hasServerPassword() {
			return configServer.hasPassword();
		}
		
		public void setPassword(boolean server, String passwordOld, String passwordNew) 
			throws PageException, SAXException, ClassException, IOException, TagLibException, FunctionLibException {
	    	ConfigImpl config=server?configServer:this;
	    	    
		    if(!config.hasPassword()) { 
		        config.setPassword(passwordNew);
		        
		        ConfigWebAdmin admin = ConfigWebAdmin.newInstance(config,passwordNew);
		        admin.setPassword(passwordNew);
		        admin.store();
		    }
		    else {
		    	ConfigWebUtil.checkGeneralWriteAccess(config,passwordOld);
		        ConfigWebAdmin admin = ConfigWebAdmin.newInstance(config,passwordOld);
		        admin.setPassword(passwordNew);
		        admin.store();
		    }
		}

		@Override
		public Resource getConfigServerDir() {
			return configServer.getConfigDir();
		}

		public Map<String, String> getAllLabels() {
			return configServer.getLabels();
		}

		@Override
		public boolean allowRequestTimeout() {
			return configServer.allowRequestTimeout();
		}
		
		public CFMLWriter getCFMLWriter(HttpServletRequest req, HttpServletResponse rsp) {
			// FUTURE  move interface CFMLWriter to Loader and load dynaicly from railo-web.xml
	        if(writerType==CFML_WRITER_WS)
	            return new CFMLWriterWhiteSpace		(req,rsp,-1,false,closeConnection(),isShowVersion(),contentLength(),allowCompression());
	        else if(writerType==CFML_WRITER_REFULAR) 
	            return new CFMLWriterImpl			(req,rsp,-1,false,closeConnection(),isShowVersion(),contentLength(),allowCompression());
	        else
	            return new CFMLWriterWhiteSpacePref	(req,rsp,-1,false,closeConnection(),isShowVersion(),contentLength(),allowCompression());
	    }
}
