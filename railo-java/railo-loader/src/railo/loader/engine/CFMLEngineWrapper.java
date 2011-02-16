package railo.loader.engine;

import java.io.IOException;
import java.net.URL;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import railo.runtime.CFMLFactory;
import railo.runtime.PageContext;
import railo.runtime.util.Cast;
import railo.runtime.util.Creation;
import railo.runtime.util.Decision;
import railo.runtime.util.Excepton;
import railo.runtime.util.HTTPUtil;
import railo.runtime.util.Operation;
import railo.runtime.util.ResourceUtil;
import railo.runtime.util.ZipUtil;
import railo.runtime.video.VideoUtil;

/**
 * wrapper for a CFMlEngine
 */
public class CFMLEngineWrapper implements CFMLEngine, EngineChangeListener {
    
    private CFMLEngine engine;

    /**
     * constructor of the class
     * @param engine
     */
    public CFMLEngineWrapper(CFMLEngine engine) {
        this.engine=engine;
    }
    
    /**
     * @see railo.loader.engine.CFMLEngine#addServletConfig(javax.servlet.ServletConfig)
     */
    public void addServletConfig(ServletConfig config) throws ServletException {
        engine.addServletConfig(config);
    }

    /**
     * @see railo.loader.engine.CFMLEngine#serviceCFML(javax.servlet.http.HttpServlet, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    public void serviceCFML(HttpServlet servlet, HttpServletRequest req,
            HttpServletResponse rsp) throws ServletException, IOException {
        engine.serviceCFML(servlet,req,rsp);
    }

    /**
     * @see railo.loader.engine.CFMLEngine#serviceAMF(javax.servlet.http.HttpServlet, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    public void serviceAMF(HttpServlet servlet, HttpServletRequest req,
            HttpServletResponse rsp) throws ServletException, IOException {
        engine.serviceAMF(servlet,req,rsp);
    }
    
    public void serviceFile(HttpServlet servlet, HttpServletRequest req,
            HttpServletResponse rsp) throws ServletException, IOException {
        engine.serviceFile(servlet,req,rsp);
    }

    /**
     * @see railo.loader.engine.CFMLEngine#getVersion()
     */
    public String getVersion() {
        return engine.getVersion();
    }

    /**
     * @see railo.loader.engine.CFMLEngine#getUpdateType()
     */
    public String getUpdateType() {
        return engine.getUpdateType();
    }

    /**
     * @see railo.loader.engine.CFMLEngine#getUpdateLocation()
     */
    public URL getUpdateLocation() {
        return engine.getUpdateLocation();
    }

    /**
     * @see railo.loader.engine.CFMLEngine#can(int, java.lang.String)
     */
    public boolean can(int type, String password) {
        return engine.can(type,password);
    }

    /**
     * @see railo.loader.engine.CFMLEngine#getCFMLEngineFactory()
     */
    public CFMLEngineFactory getCFMLEngineFactory() {
        return engine.getCFMLEngineFactory();
    }

    /**
     * @see railo.loader.engine.CFMLEngine#reset()
     */
    public void reset() {
        engine.reset();
    }

    /**
     * @see railo.loader.engine.CFMLEngine#reset(String)
     */
    public void reset(String configId) {
        engine.reset(configId);
    }

    /**
     * @see railo.loader.engine.EngineChangeListener#onUpdate(railo.loader.engine.CFMLEngine)
     */
    public void onUpdate(CFMLEngine newEngine) {
        this.engine=newEngine;
    }

    /**
     * @see railo.loader.engine.CFMLEngine#getCastUtil()
     */
    public Cast getCastUtil() {
        return engine.getCastUtil();
    }

    /**
     * @see railo.loader.engine.CFMLEngine#getOperatonUtil()
     */
    public Operation getOperatonUtil() {
        return engine.getOperatonUtil();
    }

    /**
     * @see railo.loader.engine.CFMLEngine#getDecisionUtil()
     */
    public Decision getDecisionUtil() {
        return engine.getDecisionUtil();
    }

    /**
     * @see railo.loader.engine.CFMLEngine#getExceptionUtil()
     */
    public Excepton getExceptionUtil() {
        return engine.getExceptionUtil();
    }

    /**
     * @see railo.loader.engine.CFMLEngine#getCreationUtil()
     */
    public Creation getCreationUtil() {
        return engine.getCreationUtil();
    }

	/**
	 *
	 * @see railo.loader.engine.CFMLEngine#getCFMLFactory(javax.servlet.ServletContext, javax.servlet.ServletConfig, javax.servlet.http.HttpServletRequest)
	 */
	public CFMLFactory getCFMLFactory(ServletContext srvContext, ServletConfig srvConfig, HttpServletRequest req) throws ServletException {
		System.out.println(engine);
		return engine.getCFMLFactory(srvContext, srvConfig, req);
	}

	/**
	 * @see railo.loader.engine.CFMLEngine#getBlazeDSUtil()
	 */
	public Object getBlazeDSUtil() {
		return engine.getBlazeDSUtil();
	}

	/**
	 * @see railo.loader.engine.CFMLEngine#getFDController()
	 */
	public Object getFDController() {
		return engine.getFDController();
	}

	/**
	 * @see railo.loader.engine.CFMLEngine#getHTTPUtil()
	 */
	public HTTPUtil getHTTPUtil() {
		return engine.getHTTPUtil();
	}

	/**
	 * @see railo.loader.engine.CFMLEngine#getResourceUtil()
	 */
	public ResourceUtil getResourceUtil() {
		return engine.getResourceUtil();
	}

	/**
	 * @see railo.loader.engine.CFMLEngine#getThreadPageContext()
	 */
	public PageContext getThreadPageContext() {
		return engine.getThreadPageContext();
	}

	/**
	 * @see railo.loader.engine.CFMLEngine#getVideoUtil()
	 */
	public VideoUtil getVideoUtil() {
		return engine.getVideoUtil();
	}

	/**
	 * @see railo.loader.engine.CFMLEngine#getZipUtil()
	 */
	public ZipUtil getZipUtil() {
		return engine.getZipUtil();
	}

	/**
	 * @see railo.loader.engine.CFMLEngine#getState()
	 */
	public String getState() {
		return engine.getState();
	}
	
	/*FUTURE  something is wrong with usage of this class, it is not reset when updating railo
	
	public CFMLEngine getEngine() {
		return engine;
	}*/
}
