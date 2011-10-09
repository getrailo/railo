package railo.loader.servlet;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import railo.loader.engine.CFMLEngine;
import railo.loader.engine.CFMLEngineFactory;
import railo.loader.engine.EngineChangeListener;

/**
 */
public class AMFServlet extends RailoServlet {
    
	private static final long serialVersionUID = 2545934355390532318L;
	
	/**
     * @see javax.servlet.Servlet#init(javax.servlet.ServletConfig)
     */
    public void init(ServletConfig sg) throws ServletException {
        super.init(sg);
        // do not get engine here, because then it is possible that the engine is initilized with this values
    }
    /**
     * @see javax.servlet.http.HttpServlet#service(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    protected void service(HttpServletRequest req, HttpServletResponse rsp) throws ServletException, IOException {
    	if(engine==null)
    		engine=CFMLEngineFactory.getInstance(getServletConfig(),this);
        engine.serviceAMF(this,req,rsp);
    }
}
