package railo.loader.servlet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import railo.loader.engine.CFMLEngine;
import railo.loader.engine.CFMLEngineFactory;
import railo.loader.engine.EngineChangeListener;

public abstract class RailoServlet extends HttpServlet implements EngineChangeListener {

	private static final long serialVersionUID = 3911001884655921666L;
	
	protected CFMLEngine engine;
    
	/**
     * @see railo.loader.engine.EngineChangeListener#onUpdate(railo.loader.engine.CFMLEngine)
     */
    public void onUpdate(CFMLEngine newEngine) {
    	try {
    		engine=CFMLEngineFactory.getInstance(getServletConfig(),this);
    	} 
    	catch (ServletException e) {
    		engine=newEngine;
    	}
    }
}