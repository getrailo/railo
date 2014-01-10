package railo.runtime.engine;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;

import railo.cli.servlet.HTTPServletImpl;
import railo.commons.collection.MapFactory;
import railo.commons.io.FileUtil;
import railo.commons.io.IOUtil;
import railo.commons.io.SystemUtil;
import railo.commons.io.res.Resource;
import railo.commons.io.res.ResourceProvider;
import railo.commons.io.res.ResourcesImpl;
import railo.commons.io.res.util.ResourceUtil;
import railo.commons.io.res.util.ResourceUtilImpl;
import railo.commons.lang.Pair;
import railo.commons.lang.StringUtil;
import railo.commons.lang.SystemOut;
import railo.commons.lang.types.RefBoolean;
import railo.commons.lang.types.RefBooleanImpl;
import railo.commons.net.HTTPUtil;
import railo.intergral.fusiondebug.server.FDControllerImpl;
import railo.loader.engine.CFMLEngine;
import railo.loader.engine.CFMLEngineFactory;
import railo.loader.engine.CFMLEngineWrapper;
import railo.loader.util.Util;
import railo.runtime.CFMLFactory;
import railo.runtime.CFMLFactoryImpl;
import railo.runtime.Info;
import railo.runtime.PageContext;
import railo.runtime.PageSource;
import railo.runtime.config.ConfigServer;
import railo.runtime.config.ConfigServerFactory;
import railo.runtime.config.ConfigServerImpl;
import railo.runtime.config.ConfigWeb;
import railo.runtime.config.ConfigWebFactory;
import railo.runtime.config.ConfigWebImpl;
import railo.runtime.exp.ApplicationException;
import railo.runtime.exp.PageException;
import railo.runtime.exp.PageServletException;
import railo.runtime.net.http.HTTPServletRequestWrap;
import railo.runtime.net.http.HttpServletRequestDummy;
import railo.runtime.net.http.HttpServletResponseDummy;
import railo.runtime.net.http.ReqRspUtil;
import railo.runtime.op.CastImpl;
import railo.runtime.op.Caster;
import railo.runtime.op.CreationImpl;
import railo.runtime.op.DecisionImpl;
import railo.runtime.op.ExceptonImpl;
import railo.runtime.op.OperationImpl;
import railo.runtime.query.QueryCacheSupport;
import railo.runtime.type.StructImpl;
import railo.runtime.util.BlazeDSImpl;
import railo.runtime.util.Cast;
import railo.runtime.util.Creation;
import railo.runtime.util.Decision;
import railo.runtime.util.Excepton;
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
	
	
	private static Map<String,CFMLFactory> initContextes=MapFactory.<String,CFMLFactory>getConcurrentMap();
    private static Map<String,CFMLFactory> contextes=MapFactory.<String,CFMLFactory>getConcurrentMap();
    private static ConfigServerImpl configServer=null;
    private static CFMLEngineImpl engine=null;
    //private ServletConfig config;
    private CFMLEngineFactory factory;
    private AMFEngine amfEngine=new AMFEngine();
    private final RefBoolean controlerState=new RefBooleanImpl(true);
	private boolean allowRequestTimeout=true;
	private Monitor monitor;
	private List<ServletConfig> servletConfigs=new ArrayList<ServletConfig>();
	private long uptime; 
	
    
    //private static CFMLEngineImpl engine=new CFMLEngineImpl();

    private CFMLEngineImpl(CFMLEngineFactory factory) {
    	this.factory=factory; 
    	CFMLEngineFactory.registerInstance(this);// patch, not really good but it works
        ConfigServerImpl cs = getConfigServerImpl();
    	
        SystemOut.printDate(SystemUtil.getPrintWriter(SystemUtil.OUT),"Start CFML Controller");
        Controler controler = new Controler(cs,initContextes,5*1000,controlerState);
        controler.setDaemon(true);
        controler.setPriority(Thread.MIN_PRIORITY);
        controler.start();

        touchMonitor(cs);  
        this.uptime=System.currentTimeMillis();
        //this.config=config; 
    }


	public void touchMonitor(ConfigServerImpl cs) {
		if(monitor!=null && monitor.isAlive()) return; 
		monitor = new Monitor(cs,controlerState); 
        monitor.setDaemon(true);
        monitor.setPriority(Thread.MIN_PRIORITY);
        monitor.start(); 
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
    
    @Override
    public void addServletConfig(ServletConfig config) throws ServletException {
    	servletConfigs.add(config);
    	String real=ReqRspUtil.getRootPath(config.getServletContext());
        if(!initContextes.containsKey(real)) {             
        	CFMLFactory jspFactory = loadJSPFactory(getConfigServerImpl(),config,initContextes.size());
            initContextes.put(real,jspFactory);
        }        
    }
    
    // FUTURE add to public interface
    public ConfigServer getConfigServer(String password) throws PageException {
    	getConfigServerImpl().checkAccess(password);
    	return getConfigServerImpl();
    }

    // FUTURE add to public interface
    public ConfigServer getConfigServer(String key, long timeNonce) throws PageException {
    	configServer.checkAccess(key,timeNonce);
    	return configServer;
    }

    private ConfigServerImpl getConfigServerImpl() {
    	if(configServer==null) {
            try {
            	ResourceProvider frp = ResourcesImpl.getFileResourceProvider();
            	Resource context = frp.getResource(factory.getResourceRoot().getAbsolutePath()).getRealResource("context");
            	//CFMLEngineFactory.registerInstance(this);// patch, not really good but it works
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
    
    private  CFMLFactoryImpl loadJSPFactory(ConfigServerImpl configServer, ServletConfig sg, int countExistingContextes) throws ServletException {
    	try {
            // Load Config
    		RefBoolean isCustomSetting=new RefBooleanImpl();
            Resource configDir=getConfigDirectory(sg,configServer,countExistingContextes,isCustomSetting);
            
            QueryCacheSupport queryCache=QueryCacheSupport.getInstance();
            CFMLFactoryImpl factory=new CFMLFactoryImpl(this,queryCache);
            ConfigWebImpl config=ConfigWebFactory.newInstance(factory,configServer,configDir,isCustomSetting.toBooleanValue(),sg);
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
     * @param configServer 
     * @param countExistingContextes 
     * @return return path to directory
     */
    private Resource getConfigDirectory(ServletConfig sg, ConfigServerImpl configServer, int countExistingContextes, RefBoolean isCustomSetting) throws PageServletException {
    	isCustomSetting.setValue(true);
    	ServletContext sc=sg.getServletContext();
        String strConfig=sg.getInitParameter("configuration");
        if(strConfig==null)strConfig=sg.getInitParameter("railo-web-directory");
        if(strConfig==null) {
        	isCustomSetting.setValue(false);
        	strConfig="{web-root-directory}/WEB-INF/railo/";
        }
        // only for backward compatibility
        else if(strConfig.startsWith("/WEB-INF/railo/"))strConfig="{web-root-directory}"+strConfig;
        
        
        strConfig=Util.removeQuotes(strConfig,true);
        
        
        
        // static path is not allowed
        if(countExistingContextes>1 && strConfig!=null && strConfig.indexOf('{')==-1){
        	String text="static path ["+strConfig+"] for servlet init param [railo-web-directory] is not allowed, path must use a web-context specific placeholder.";
        	System.err.println(text);
        	throw new PageServletException(new ApplicationException(text));
        }
        strConfig=SystemUtil.parsePlaceHolder(strConfig,sc,configServer.getLabels());
        
        
        
        ResourceProvider frp = ResourcesImpl.getFileResourceProvider();
        Resource root = frp.getResource(ReqRspUtil.getRootPath(sc));
        Resource configDir=ResourceUtil.createResource(root.getRealResource(strConfig), FileUtil.LEVEL_PARENT_FILE,FileUtil.TYPE_DIR);
        
        if(configDir==null) {
            configDir=ResourceUtil.createResource(frp.getResource(strConfig), FileUtil.LEVEL_GRAND_PARENT_FILE,FileUtil.TYPE_DIR);
        }
        if(configDir==null) throw new PageServletException(new ApplicationException("path ["+strConfig+"] is invalid"));
        if(!configDir.exists()){
        	try {
				configDir.createDirectory(true);
			} 
        	catch (IOException e) {}
        }
        return configDir;
    }
    
    @Override
    
    public CFMLFactory getCFMLFactory(ServletContext srvContext, ServletConfig srvConfig,HttpServletRequest req) throws ServletException {
    	String real=ReqRspUtil.getRootPath(srvContext);
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
                factory=loadJSPFactory(cs,srvConfig,initContextes.size());
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
    
    @Override
    public void serviceCFML(HttpServlet servlet, HttpServletRequest req, HttpServletResponse rsp) throws ServletException, IOException {
    	
    	CFMLFactory factory=getCFMLFactory(servlet.getServletContext(), servlet.getServletConfig(), req);
    	
        PageContext pc = factory.getRailoPageContext(servlet,req,rsp,null,false,-1,false);
        ThreadQueue queue = factory.getConfig().getThreadQueue();
        queue.enter(pc);
        try {
        	/*print.out("INCLUDE");
        	print.out("servlet_path:"+req.getAttribute("javax.servlet.include.servlet_path"));
        	print.out("request_uri:"+req.getAttribute("javax.servlet.include.request_uri"));
        	print.out("context_path:"+req.getAttribute("javax.servlet.include.context_path"));
        	print.out("path_info:"+req.getAttribute("javax.servlet.include.path_info"));
        	print.out("query_string:"+req.getAttribute("javax.servlet.include.query_string"));
        	print.out("FORWARD");
        	print.out("servlet_path:"+req.getAttribute("javax.servlet.forward.servlet_path"));
        	print.out("request_uri:"+req.getAttribute("javax.servlet.forward.request_uri"));
        	print.out("context_path:"+req.getAttribute("javax.servlet.forward.context_path"));
        	print.out("path_info:"+req.getAttribute("javax.servlet.forward.path_info"));
        	print.out("query_string:"+req.getAttribute("javax.servlet.forward.query_string"));
        	print.out("---");
        	print.out(req.getServletPath());
        	print.out(pc.getHttpServletRequest().getServletPath());
        	*/
        	
        	pc.execute(pc.getHttpServletRequest().getServletPath(),false);
        } 
        catch (PageException pe) {
			throw new PageServletException(pe);
		}
        finally {
        	queue.exit(pc);
            factory.releaseRailoPageContext(pc);
            FDControllerFactory.notifyPageComplete();
        }
    }

	public void serviceFile(HttpServlet servlet, HttpServletRequest req, HttpServletResponse rsp) throws ServletException, IOException {
		req=new HTTPServletRequestWrap(req);
		CFMLFactory factory=getCFMLFactory(servlet.getServletContext(), servlet.getServletConfig(), req);
        ConfigWeb config = factory.getConfig();
        PageSource ps = config.getPageSourceExisting(null, null, req.getServletPath(), false, true, true, false);
        //Resource res = ((ConfigWebImpl)config).getPhysicalResourceExistingX(null, null, req.getServletPath(), false, true, true); 
        
		if(ps==null) {
    		rsp.sendError(404);
    	}
    	else {
    		Resource res = ps.getResource();
    		if(res==null) {
    			rsp.sendError(404);
    		}
    		else {
	    		ReqRspUtil.setContentLength(rsp,res.length());
	    		String mt = servlet.getServletContext().getMimeType(req.getServletPath());
	    		if(!StringUtil.isEmpty(mt))rsp.setContentType(mt);
	    		IOUtil.copy(res, rsp.getOutputStream(), true);
    		}
    	}
	}
	

	public void serviceRest(HttpServlet servlet, HttpServletRequest req, HttpServletResponse rsp) throws ServletException, IOException {
		req=new HTTPServletRequestWrap(req);
		CFMLFactory factory=getCFMLFactory(servlet.getServletContext(), servlet.getServletConfig(), req);
        
		PageContext pc = factory.getRailoPageContext(servlet,req,rsp,null,false,-1,false);
        ThreadQueue queue = factory.getConfig().getThreadQueue();
        queue.enter(pc);
        try {
        	pc.executeRest(pc.getHttpServletRequest().getServletPath(),false);
        } 
        catch (PageException pe) {
			throw new PageServletException(pe);
		}
        finally {
        	queue.exit(pc);
            factory.releaseRailoPageContext(pc);
            FDControllerFactory.notifyPageComplete();
        }
		
		
	}
    

    /*private String getContextList() {
        return List.arrayToList((String[])contextes.keySet().toArray(new String[contextes.size()]),", ");
    }*/

    @Override
    public String getVersion() {
        return Info.getVersionAsString();
    }

    @Override
    public String getUpdateType() {
        return getConfigServerImpl().getUpdateType();
    }

    @Override
    public URL getUpdateLocation() {
        return getConfigServerImpl().getUpdateLocation();
    }

    @Override
    public boolean can(int type, String password) {
        return getConfigServerImpl().passwordEqual(password);
    }

    public CFMLEngineFactory getCFMLEngineFactory() {
        return factory;
    }

    public void serviceAMF(HttpServlet servlet, HttpServletRequest req, HttpServletResponse rsp) throws ServletException, IOException {
    	req=new HTTPServletRequestWrap(req);
		amfEngine.service(servlet,req,rsp);
    }

    @Override
    public void reset() {
    	reset(null);
    }
    
    @Override
    public void reset(String configId) {
        
        CFMLFactoryImpl cfmlFactory;
        //ScopeContext scopeContext;
        try {
	        Iterator<String> it = contextes.keySet().iterator();
	        while(it.hasNext()) {
	        	try {
		            cfmlFactory=(CFMLFactoryImpl) contextes.get(it.next());
		            if(configId!=null && !configId.equals(cfmlFactory.getConfigWebImpl().getId())) continue;
		            	
		            // scopes
		            try{cfmlFactory.getScopeContext().clear();}catch(Throwable t){t.printStackTrace();}
		            
		            // PageContext
		            try{cfmlFactory.resetPageContext();}catch(Throwable t){t.printStackTrace();}
		            
		            // Query Cache
		            try{ cfmlFactory.getDefaultQueryCache().clear(null);}catch(Throwable t){t.printStackTrace();}
		            
		            // Gateway
		            try{ cfmlFactory.getConfigWebImpl().getGatewayEngine().reset();}catch(Throwable t){t.printStackTrace();}
		            
	        	}
	        	catch(Throwable t){
	        		t.printStackTrace();
	        	}
	        }
        }
    	finally {
            // Controller
            controlerState.setValue(false);
    	}
    }
    
    @Override
    public Cast getCastUtil() {
        return CastImpl.getInstance();
    }

    @Override
    public Operation getOperatonUtil() {
        return OperationImpl.getInstance();
    }

    @Override
    public Decision getDecisionUtil() {
        return DecisionImpl.getInstance();
    }

    @Override
    public Excepton getExceptionUtil() {
        return ExceptonImpl.getInstance();
    }

    @Override
    public Creation getCreationUtil() {
        return CreationImpl.getInstance(this);
    }

	@Override
	public Object getBlazeDSUtil() {
		return new BlazeDSImpl();
	}

	@Override
	public Object getFDController() {
		engine.allowRequestTimeout(false);
		
		return new FDControllerImpl(engine,engine.getConfigServerImpl().getSerialNumber());
	}

	public Map<String,CFMLFactory> getCFMLFactories() {
		return initContextes;
	}

	@Override
	public railo.runtime.util.ResourceUtil getResourceUtil() {
		return ResourceUtilImpl.getInstance();
	}

	@Override
	public railo.runtime.util.HTTPUtil getHTTPUtil() {
		return HTTPUtilImpl.getInstance();
	}

	@Override
	public PageContext getThreadPageContext() {
		return ThreadLocalPageContext.get();
	}

	@Override
	public void registerThreadPageContext(PageContext pc) {
		ThreadLocalPageContext.register(pc);
	}

	@Override
	public VideoUtil getVideoUtil() {
		return VideoUtilImpl.getInstance();
	}

	@Override
	public ZipUtil getZipUtil() {
		return ZipUtilImpl.getInstance();
	}

	@Override
	public String getState() {
		return Info.getStateAsString();
	}

	public void allowRequestTimeout(boolean allowRequestTimeout) {
		this.allowRequestTimeout=allowRequestTimeout;
	}

	public boolean allowRequestTimeout() {
		return allowRequestTimeout;
	}
	
	public boolean isRunning() {
		try{
			CFMLEngine other = CFMLEngineFactory.getInstance();
			// FUTURE patch, do better impl when changing loader
			if(other!=this && controlerState.toBooleanValue() &&  !(other instanceof CFMLEngineWrapper)) {
				SystemOut.printDate("CFMLEngine is still set to true but no longer valid, Railo disable this CFMLEngine.");
				controlerState.setValue(false);
				reset();
				return false;
			}
		}
		catch(Throwable t){}
		return controlerState.toBooleanValue();
	}

	@Override
	public void cli(Map<String, String> config, ServletConfig servletConfig) throws IOException,JspException,ServletException {
		ServletContext servletContext = servletConfig.getServletContext();
		HTTPServletImpl servlet=new HTTPServletImpl(servletConfig, servletContext, servletConfig.getServletName());

		// webroot
		String strWebroot=config.get("webroot");
		if(Util.isEmpty(strWebroot,true)) throw new IOException("missing webroot configuration");
		Resource root=ResourcesImpl.getFileResourceProvider().getResource(strWebroot);
		root.mkdirs();
		
		// serverName
		String serverName=config.get("server-name");
		if(Util.isEmpty(serverName,true))serverName="localhost";
		
		// uri
		String strUri=config.get("uri");
		if(Util.isEmpty(strUri,true)) throw new IOException("missing uri configuration");
		URI uri;
		try {
			uri = railo.commons.net.HTTPUtil.toURI(strUri);
		} catch (URISyntaxException e) {
			throw Caster.toPageException(e);
		}
		
		// cookie
		Cookie[] cookies;
		String strCookie=config.get("cookie");
		if(Util.isEmpty(strCookie,true)) cookies=new Cookie[0];
		else {
			Map<String,String> mapCookies=HTTPUtil.parseParameterList(strCookie,false,null);
			int index=0;
			cookies=new Cookie[mapCookies.size()];
			Entry<String, String> entry;
			Iterator<Entry<String, String>> it = mapCookies.entrySet().iterator();
			while(it.hasNext()){
				entry = it.next();
				cookies[index++]=new Cookie(entry.getKey(),entry.getValue());
			}
		}
		

		// header
		Pair[] headers=new Pair[0];
		
		// parameters
		Pair[] parameters=new Pair[0];
		
		// attributes
		StructImpl attributes = new StructImpl();
		ByteArrayOutputStream os=new ByteArrayOutputStream();
		
		
		
		
		HttpServletRequestDummy req=new HttpServletRequestDummy(
				root,serverName,uri.getPath(),uri.getQuery(),cookies,headers,parameters,attributes,null);
		req.setProtocol("CLI/1.0");
		HttpServletResponse rsp=new HttpServletResponseDummy(os);
		
		serviceCFML(servlet, req, rsp);
		String res = os.toString(ReqRspUtil.getCharacterEncoding(null,rsp));
		System.out.println(res);
	}
	
	public ServletConfig[] getServletConfigs(){
		return servletConfigs.toArray(new ServletConfig[servletConfigs.size()]);
	}

	// FUTURE add to interface
	public long uptime() {
		return uptime;
	}

}