package railo.loader.engine;

import java.io.IOException;
import java.net.URL;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;

import railo.runtime.CFMLFactory;
import railo.runtime.PageContext;
import railo.runtime.config.ConfigServer;
import railo.runtime.exp.PageException;
import railo.runtime.util.Cast;
import railo.runtime.util.Creation;
import railo.runtime.util.Decision;
import railo.runtime.util.Excepton;
import railo.runtime.util.HTTPUtil;
import railo.runtime.util.IO;
import railo.runtime.util.Operation;
import railo.runtime.util.ResourceUtil;
import railo.runtime.util.Strings;
import railo.runtime.util.ZipUtil;
import railo.runtime.video.VideoUtil;

/**
 * The CFML Engine
 */
public interface CFMLEngine { 

    /**
     * Field <code>CAN_UPDATE</code>
     */
    public static int CAN_UPDATE=0;
    
    /**
     * Field <code>CAN_RESTART</code>
     */
    public static int CAN_RESTART=1; 
    public static int CAN_RESTART_ALL=CAN_RESTART; 
    public static int CAN_RESTART_CONTEXT=2; 

    public abstract  CFMLFactory getCFMLFactory(ServletContext srvContext, ServletConfig srvConfig,HttpServletRequest req) throws ServletException;
    
    /**
     * adds a servlet config 
     * @param config
     * @throws ServletException
     */
    public abstract void addServletConfig(ServletConfig config) throws ServletException;
    
    /**
     * method to invoke the engine for CFML
     * @param servlet
     * @param req
     * @param rsp
     * @throws ServletException
     * @throws IOException
     * @throws ServletException 
     */
    public void serviceCFML(HttpServlet servlet, HttpServletRequest req, HttpServletResponse rsp) throws IOException, ServletException;

    /**
     * method to invoke the engine for AMF
     * @param servlet
     * @param req
     * @param rsp
     * @throws ServletException
     * @throws IOException
     */
    public void serviceAMF(HttpServlet servlet, HttpServletRequest req, HttpServletResponse rsp) 
        throws ServletException, IOException;

    
    /* *
     * method to invoke the engine for AMF
     * @param serviceAdapter 
     * @param message
     * @return
     * /
    //public Object executeFlex(ServiceAdapter serviceAdapter, Message message);*/
    
    

    /**
     * method to invoke the engine for a simple file
     * @param servlet
     * @param req
     * @param rsp
     * @throws ServletException
     * @throws IOException
     */
    public void serviceFile(HttpServlet servlet, HttpServletRequest req, HttpServletResponse rsp) throws ServletException, IOException; 
    
    /**
     * method to invoke the engine for a Rest Requests
     * @param servlet
     * @param req
     * @param rsp
     * @throws ServletException
     * @throws IOException
     */
    public abstract void serviceRest(HttpServlet servlet, HttpServletRequest req, HttpServletResponse rsp) throws ServletException, IOException; 
    
    
    /**
     * @return returns the version of the engine in the format [x.x.x.xxx]
     */
    public String getVersion();
    
    /**
     * @return returns the stae of the version (alpha,beta,rc,final)
     */
    public String getState();
    
    /**
     * @return returns how this engine will be updated (auto, manuell)
     */
    public String getUpdateType();

    /**
     * @return return location URL to get updates for the engines
     */
    public URL getUpdateLocation();

    /**
     * checks if process has the right to do was given with type, the engine with given password
     * @param type restart type (CFMLEngine.CAN_UPDATE, CFMLEngine.CAN_RESTART)
     * @param password 
     * @return has right
     */
    public boolean can(int type, String password); 
    
    /**
     * @return returns the engine that has produced this engine
     */
    public CFMLEngineFactory getCFMLEngineFactory();

    /**
     * reset the engine
     */
    public void reset();
    
    /**
     * reset the engine
     */
    public void reset(String configId);
    
    /** 
     * return the cast util 
     * @return operaton util 
     */ 
    public Cast getCastUtil(); 
    
    /** 
     * return the operation util 
     * @return operaton util 
     */ 
    public Operation getOperatonUtil(); 

    /** 
     * returns the decision util 
     * @return decision util 
     */ 
    public Decision getDecisionUtil(); 
    
    /** 
     * returns the decision util 
     * @return decision util 
     */ 
    public Excepton getExceptionUtil();
    
    /** 
     * returns the decision util 
     * @return decision util 
     */ 
    public Creation getCreationUtil();
    

    /** 
     * returns the IO util 
     * @return decision util 
     */ 
    public IO getIOUtil();
    /** 
     * returns the IO util 
     * @return decision util 
     */ 
    public Strings getStringUtil();

	/**
	 * returns the FusionDebug Engine
	 * @return IFDController
	 */
	public Object getFDController();

	/*
	 * removed to avoid library conflicts, the blazeds implementation is no longer under developement an in a separate jar
	 */
	// public Object getBlazeDSUtil(); 

	/**
	 * returns the Resource Util 
	 * @return Blaze DS Util 
	 */
	public ResourceUtil getResourceUtil(); 
	
	/**
	 * returns the HTTP Util
	 * @return the HTTP Util
	 */
	public HTTPUtil getHTTPUtil(); 
	
	/**
	 * @return return PageContext for the current PageContext
	 */
	public PageContext getThreadPageContext();

	public VideoUtil getVideoUtil();

	public ZipUtil getZipUtil();

	public abstract void cli(Map<String, String> config, ServletConfig servletConfig) throws IOException, JspException, ServletException;

	public abstract void registerThreadPageContext(PageContext pc);

	
	public ConfigServer getConfigServer(String password) throws PageException;

    public ConfigServer getConfigServer(String key, long timeNonce) throws PageException;

    public long uptime();
}