package railo.runtime.engine;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import railo.commons.collections.HashTable;
import railo.commons.io.FileUtil;
import railo.commons.io.IOUtil;
import railo.commons.io.SystemUtil;
import railo.commons.io.res.Resource;
import railo.commons.io.res.ResourceProvider;
import railo.commons.io.res.ResourcesImpl;
import railo.commons.io.res.util.ResourceUtil;
import railo.commons.io.res.util.ResourceUtilImpl;
import railo.commons.lang.StringUtil;
import railo.commons.lang.SystemOut;
import railo.commons.lang.types.RefBoolean;
import railo.commons.lang.types.RefBooleanImpl;
import railo.intergral.fusiondebug.server.FDControllerImpl;
import railo.loader.engine.CFMLEngine;
import railo.loader.engine.CFMLEngineFactory;
import railo.runtime.CFMLFactory;
import railo.runtime.CFMLFactoryImpl;
import railo.runtime.Info;
import railo.runtime.PageContext;
import railo.runtime.config.ConfigServerFactory;
import railo.runtime.config.ConfigServerImpl;
import railo.runtime.config.ConfigWeb;
import railo.runtime.config.ConfigWebFactory;
import railo.runtime.config.ConfigWebImpl;
import railo.runtime.exp.PageException;
import railo.runtime.exp.PageServletException;
import railo.runtime.op.CastImpl;
import railo.runtime.op.CreationImpl;
import railo.runtime.op.DecisionImpl;
import railo.runtime.op.ExceptonImpl;
import railo.runtime.op.OperationImpl;
import railo.runtime.query.QueryCache;
import railo.runtime.query.QueryCacheImpl;
import railo.runtime.util.BlazeDSImpl;
import railo.runtime.util.Cast;
import railo.runtime.util.Creation;
import railo.runtime.util.Decision;
import railo.runtime.util.Excepton;
import railo.runtime.util.HTTPUtil;
import railo.runtime.util.HTTPUtilImpl;
import railo.runtime.util.Operation;
import railo.runtime.util.ZipUtil;
import railo.runtime.util.ZipUtilImpl;
import railo.runtime.video.VideoUtil;
import railo.runtime.video.VideoUtilImpl;

import com.intergral.fusiondebug.server.FDControllerFactory;

/**
 * The CFMl Engine
 */
public final class CFMLEngineImpl implements CFMLEngine {
    
    
    private static Map initContextes=new HashTable();
    private static Map contextes=new HashTable();
    private static ConfigServerImpl configServer=null;
    private static CFMLEngineImpl engine=null;
    //private ServletConfig config;
    private CFMLEngineFactory factory;
    private AMFEngine amfEngine=new AMFEngine();
    private RefBoolean controlerState=new RefBooleanImpl(true);
	private boolean allowRequestTimeout=true;
    
    //private static CFMLEngineImpl engine=new CFMLEngineImpl();

    private CFMLEngineImpl(CFMLEngineFactory factory) {
    	this.factory=factory; 
        SystemOut.printDate(SystemUtil.PRINTWRITER_OUT,"Start CFML Controller");
        Controler controler = new Controler(getConfigServerImpl(),initContextes,5*1000,controlerState);
        controler.setDaemon(true);
        controler.setPriority(Thread.MIN_PRIORITY);
        controler.start();  
        
        //this.config=config; 
    }

    /**
     * get singelton instance of the CFML Engine
     * @param factory
     * @return CFMLEngine
     */
    public static synchronized CFMLEngine getInstance(CFMLEngineFactory factory) {
    	if(engine==null) {
    		engine=new CFMLEngineImpl(factory);
        }
        return engine;
    }
    
    /**
     * get singelton instance of the CFML Engine, throwsexception when not already init
     * @param factory
     * @return CFMLEngine
     */
    public static synchronized CFMLEngine getInstance() throws ServletException {
    	if(engine!=null) return engine;
    	throw new ServletException("CFML Engine is not loaded");
    }
    
    /**
     * @see railo.loader.engine.CFMLEngine#addServletConfig(javax.servlet.ServletConfig)
     */
    public void addServletConfig(ServletConfig config) throws ServletException {
    	String real=config.getServletContext().getRealPath("/");
        if(!initContextes.containsKey(real)) {             
        	CFMLFactory jspFactory = loadJSPFactory(getConfigServerImpl(),config);
            initContextes.put(real,jspFactory);
        }        
    }
    
    private ConfigServerImpl getConfigServerImpl() {
        if(configServer==null) {
            try {
            	ResourceProvider frp = ResourcesImpl.getFileResourceProvider();
            	
            	Resource context = frp.getResource(factory.getResourceRoot().getAbsolutePath()).getRealResource("context");
            	
                configServer=ConfigServerFactory.newInstance(
                        this,
                        initContextes,
                        contextes,
                        context);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return configServer;
    }
    
    private  CFMLFactoryImpl loadJSPFactory(ConfigServerImpl configServer, ServletConfig sg) throws ServletException {
    	try {
            // Load Config
            Resource configDir=getConfigDirectory(sg);
            
            QueryCache queryCache=new QueryCacheImpl();
            CFMLFactoryImpl factory=new CFMLFactoryImpl(this,queryCache);
            ConfigWebImpl config=ConfigWebFactory.newInstance(factory,configServer,configDir,sg);
            factory.setConfig(config);
            return factory;
        }
        catch (Exception e) {
            ServletException se= new ServletException(e.getMessage());
            se.setStackTrace(e.getStackTrace());
            throw se;
        } 
        
    }   

    /**
     * loads Configuration File from System, from init Parameter from web.xml
     * @param sg
     * @return return path to directory
     */
    private Resource getConfigDirectory(ServletConfig sg) {
        ServletContext sc=sg.getServletContext();
        String strConfig=sg.getInitParameter("configuration");
        if(strConfig==null)strConfig=sg.getInitParameter("railo-web-directory");
        if(strConfig==null)strConfig="{web-root-directory}/WEB-INF/railo/";
        
        strConfig=SystemUtil.parsePlaceHolder(strConfig,sc);
        
        ResourceProvider frp = ResourcesImpl.getFileResourceProvider();
        Resource root = frp.getResource(sc.getRealPath("/"));
        Resource configDir=ResourceUtil.createResource(root.getRealResource(strConfig), FileUtil.LEVEL_PARENT_FILE,FileUtil.TYPE_DIR);
        
        if(configDir==null) {
            configDir=ResourceUtil.createResource(frp.getResource(strConfig), FileUtil.LEVEL_GRAND_PARENT_FILE,FileUtil.TYPE_DIR);
        }
        
        if(!configDir.exists()){
        	try {
				configDir.createDirectory(true);
			} 
        	catch (IOException e) {}
        }
        return configDir;
    }
    
    /**
     * @see railo.loader.engine.CFMLEngine#getCFMLFactory(javax.servlet.ServletContext, javax.servlet.ServletConfig, javax.servlet.http.HttpServletRequest)
     */
    
    public CFMLFactory getCFMLFactory(ServletContext srvContext, ServletConfig srvConfig,HttpServletRequest req) throws ServletException {
    	//SystemOut.printDate("Call:"+req.getServletPath());
	    
        String real=srvContext.getRealPath("/");
        ConfigServerImpl cs = getConfigServerImpl();
    	
        
        // Load JspFactory
        CFMLFactoryImpl factory=null;
        Object o=contextes.get(real);
        if(o==null) {
            //int size=sn.getContextCount();
            //if(size!=-1 && size <= contextes.size())
                //throw new ServletException("the maximum size of "+size+" web contextes is reached, " +"to have more contexes upgrade your railo version, already contextes in use are ["+getContextList()+"]");
            o=initContextes.get(real);
            if(o!=null) {
                factory=(CFMLFactoryImpl) o;
            }
            else {
                factory=loadJSPFactory(cs,srvConfig);
                initContextes.put(real,factory);
            }
            contextes.put(real,factory);
            
            try {
            	String cp = req.getContextPath();
            	if(cp==null)cp="";
				factory.setURL(new URL(req.getScheme(),req.getServerName(),req.getServerPort(),cp));
			} 
            catch (MalformedURLException e) {
				e.printStackTrace();
			}
            //
            
        }
        else {
            factory=(CFMLFactoryImpl) o;
        }
        return factory;
    }
    
    /**
     * @see railo.loader.engine.CFMLEngine#serviceCFML(javax.servlet.http.HttpServlet, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    public void serviceCFML(HttpServlet servlet, HttpServletRequest req, HttpServletResponse rsp) 
        throws ServletException, IOException {
    	CFMLFactory factory=getCFMLFactory(servlet.getServletContext(), servlet.getServletConfig(), req);
        PageContext pc = factory.getRailoPageContext(servlet,req,rsp,null,false,-1,false);
        try {
        	pc.execute(req.getServletPath(),false);
        } 
        catch (PageException pe) {
			throw new PageServletException(pe);
		}
        finally {
            factory.releaseRailoPageContext(pc);
            FDControllerFactory.notifyPageComplete();
        }   
        
    }

	public void serviceFile(HttpServlet servlet, HttpServletRequest req, HttpServletResponse rsp) throws ServletException, IOException {
		CFMLFactory factory=getCFMLFactory(servlet.getServletContext(), servlet.getServletConfig(), req);
        ConfigWeb config = factory.getConfig();
        Resource res = ((ConfigWebImpl)config).getPhysical(null,req.getServletPath(),true);
        
		//print.out(req.getServletPath()+":"+res+":"+res.exists());
    	if(!res.exists()) {
    		rsp.sendError(404);
    	}
    	else {
    		rsp.setContentLength((int)res.length());
    		String mt = servlet.getServletContext().getMimeType(req.getServletPath());
    		if(!StringUtil.isEmpty(mt))rsp.setContentType(mt);
    		IOUtil.copy(res, rsp.getOutputStream(), true);
    	}
	}
    

    /*private String getContextList() {
        return List.arrayToList((String[])contextes.keySet().toArray(new String[contextes.size()]),", ");
    }*/

    /**
     * @see railo.loader.engine.CFMLEngine#getVersion()
     */
    public String getVersion() {
        return Info.getVersionAsString();
    }

    /**
     * @see railo.loader.engine.CFMLEngine#getUpdateType()
     */
    public String getUpdateType() {
        return getConfigServerImpl().getUpdateType();
    }

    /**
     * @see railo.loader.engine.CFMLEngine#getUpdateLocation()
     */
    public URL getUpdateLocation() {
        return getConfigServerImpl().getUpdateLocation();
    }

    /**
     * @see railo.loader.engine.CFMLEngine#can(int, java.lang.String)
     */
    public boolean can(int type, String password) {
        return getConfigServerImpl().passwordEqual(password);
    }

    public CFMLEngineFactory getCFMLEngineFactory() {
        return factory;
    }

    public void serviceAMF(HttpServlet servlet, HttpServletRequest req, HttpServletResponse rsp) throws ServletException, IOException {
        amfEngine.service(servlet,req,rsp);
    }

    /**
     * @see railo.loader.engine.CFMLEngine#reset()
     */
    public void reset() {
    	reset(null);
    }
    
    /**
     * @see railo.loader.engine.CFMLEngine#reset(String)
     */
    public void reset(String configId) {
        
        
        CFMLFactoryImpl cfmlFactory;
        //ScopeContext scopeContext;
        
        Iterator it = contextes.keySet().iterator();
        while(it.hasNext()) {
            cfmlFactory=(CFMLFactoryImpl)contextes.get(it.next());
            if(configId!=null && !configId.equals(cfmlFactory.getConfigWebImpl().getId())) continue;
            	
            // scopes
            cfmlFactory.getScopeContext().clear();
            
            // PageContext
            cfmlFactory.resetPageContext();
            
            // Query Cache
            cfmlFactory.getQueryCache().clear();
            
            // Controller
            controlerState.setValue(false);
        }
    }
    
    /**
     * @see railo.loader.engine.CFMLEngine#getCastUtil()
     */
    public Cast getCastUtil() {
        return CastImpl.getInstance();
    }

    /**
     * @see railo.loader.engine.CFMLEngine#getOperatonUtil()
     */
    public Operation getOperatonUtil() {
        return OperationImpl.getInstance();
    }

    /**
     * @see railo.loader.engine.CFMLEngine#getDecisionUtil()
     */
    public Decision getDecisionUtil() {
        return DecisionImpl.getInstance();
    }

    /**
     * @see railo.loader.engine.CFMLEngine#getExceptionUtil()
     */
    public Excepton getExceptionUtil() {
        return ExceptonImpl.getInstance();
    }

    /**
     * @see railo.loader.engine.CFMLEngine#getCreationUtil()
     */
    public Creation getCreationUtil() {
        return CreationImpl.getInstance();
    }

	/**
	 * @see railo.loader.engine.CFMLEngine#getBlazeDSUtil()
	 */
	public Object getBlazeDSUtil() {
		return new BlazeDSImpl();
	}

	/**
	 * @see railo.loader.engine.CFMLEngine#getFDController()
	 */
	public Object getFDController() {
		engine.allowRequestTimeout(false);
		return new FDControllerImpl(engine);
	}

	public Map getCFMLFactories() {
		return initContextes;
	}

	/**
	 * @see railo.loader.engine.CFMLEngine#getResourceUtil()
	 */
	public railo.runtime.util.ResourceUtil getResourceUtil() {
		return ResourceUtilImpl.getInstance();
	}

	/**
	 * @see railo.loader.engine.CFMLEngine#getHTTPUtil()
	 */
	public HTTPUtil getHTTPUtil() {
		return HTTPUtilImpl.getInstance();
	}

	/**
	 * @see railo.loader.engine.CFMLEngine#getThreadPageContext()
	 */
	public PageContext getThreadPageContext() {
		return ThreadLocalPageContext.get();
	}

	/**
	 * @see railo.loader.engine.CFMLEngine#getVideoUtil()
	 */
	public VideoUtil getVideoUtil() {
		return VideoUtilImpl.getInstance();
	}

	/**
	 * @see railo.loader.engine.CFMLEngine#getZipUtil()
	 */
	public ZipUtil getZipUtil() {
		return ZipUtilImpl.getInstance();
	}

	/**
	 * @see railo.loader.engine.CFMLEngine#getState()
	 */
	public String getState() {
		return Info.getStateAsString();
	}

	public void allowRequestTimeout(boolean allowRequestTimeout) {
		this.allowRequestTimeout=allowRequestTimeout;
	}

	public boolean allowRequestTimeout() {
		return allowRequestTimeout;
	}
}