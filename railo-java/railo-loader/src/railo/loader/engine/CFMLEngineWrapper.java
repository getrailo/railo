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

import org.osgi.framework.Bundle;

import railo.Info;
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
    
    /**
     * @see railo.loader.engine.CFMLEngine#serviceFile(javax.servlet.http.HttpServlet, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    public void serviceFile(HttpServlet servlet, HttpServletRequest req,
            HttpServletResponse rsp) throws ServletException, IOException {
        engine.serviceFile(servlet,req,rsp);
    }
    

    public void serviceRest(HttpServlet servlet, HttpServletRequest req,
            HttpServletResponse rsp) throws ServletException, IOException {
        engine.serviceRest(servlet,req,rsp);
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
    
    @Override
    public IO getIOUtil() {
        return engine.getIOUtil();
    }

	/**
	 *
	 * @see railo.loader.engine.CFMLEngine#getCFMLFactory(javax.servlet.ServletContext, javax.servlet.ServletConfig, javax.servlet.http.HttpServletRequest)
	 */
	public CFMLFactory getCFMLFactory(ServletContext srvContext, ServletConfig srvConfig, HttpServletRequest req) throws ServletException {
		return engine.getCFMLFactory(srvContext, srvConfig, req);
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

	@Override
	public PageContext getThreadPageContext() {
		return engine.getThreadPageContext();
	}

	@Override
	public VideoUtil getVideoUtil() {
		return engine.getVideoUtil();
	}

	@Override
	public ZipUtil getZipUtil() {
		return engine.getZipUtil();
	}

	@Override
	public Strings getStringUtil() {
		return engine.getStringUtil();
	}

	@Override
	public String getState() {
		return engine.getState();
	}
	
	/**
	 * this interface is new to this class and not offically part of Railo 3.x, do not use outside the loader
	 * @param other
	 * @param checkReferenceEqualityOnly
	 * @return
	 */
	public boolean equalTo(CFMLEngine other, boolean checkReferenceEqualityOnly) {
		while(other instanceof CFMLEngineWrapper)
			other=((CFMLEngineWrapper)other).engine;
		if(checkReferenceEqualityOnly) return engine==other;
		return engine.equals(other);
	}

	@Override
	public void cli(Map<String, String> config, ServletConfig servletConfig) throws IOException, JspException, ServletException {
		engine.cli(config, servletConfig);
	}

	@Override
	public void registerThreadPageContext(PageContext pc) {
		engine.registerThreadPageContext(pc);
	}

	@Override
	public ConfigServer getConfigServer(String password) throws PageException {
		return engine.getConfigServer(password);
	}

	@Override
	public ConfigServer getConfigServer(String key, long timeNonce)throws PageException {
		return engine.getConfigServer(key, timeNonce);
	}

	@Override
	public long uptime() {
		return engine.uptime();
	}

	@Override
	public Info getInfo() {
		return engine.getInfo();
	}

	@Override
	public Bundle getCoreBundle() {
		return engine.getCoreBundle();
	}
}
