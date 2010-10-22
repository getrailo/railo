package main.servlet;

import java.io.File;
import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

import railo.commons.io.SystemUtil;
import railo.commons.lang.StringUtil;
import railo.loader.engine.CFMLEngine;
import railo.loader.engine.CFMLEngineFactory;
import railo.runtime.engine.CFMLEngineImpl;

/**
 * 
 */
public final class CFMLEngineFactoryDummy extends CFMLEngineFactory {

    private static CFMLEngineFactoryDummy factory;
    private ServletConfig config;
    private CFMLEngine engine;

    
    
    
    private CFMLEngineFactoryDummy(ServletConfig config) {
    	this.config=config;
        engine = CFMLEngineImpl.getInstance(this);
        
    }
    
    /**
     * returns instance of this factory (singelton-> always the same instance)
     * @param config
     * @return Singelton Instance of the Factory
     * @throws ServletException 
     */
    public static CFMLEngine getInstance(ServletConfig config) throws ServletException {
    	
    	if(factory==null) {
    		factory=new CFMLEngineFactoryDummy(config);
    		CFMLEngineFactory.registerInstance(factory.engine);
    		
    	}
    	factory.engine.addServletConfig(config);
        
        return factory.engine;
    }


    /**
     * @see railo.loader.engine.CFMLEngineFactory#getResourceRoot()
     */
    public File getResourceRoot() throws IOException {
    	
    	String path=SystemUtil.parsePlaceHolder("{web-root}", config.getServletContext());
    	path=StringUtil.replace(path, "webroot", "work", true);
    	//print.err(path);
        return new File(path);
    }

    /**
     * @see railo.loader.engine.CFMLEngineFactory#restart(java.lang.String)
     */
    public boolean restart(String password) throws IOException, ServletException {
        engine.reset();
        return true;
    }

    /**
     * @see railo.loader.engine.CFMLEngineFactory#update(java.lang.String)
     */
    public boolean update(String password) throws IOException, ServletException {
        return true;
    }
    
    

}
