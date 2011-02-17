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
public class FileServlet extends RailoServlet {
    
    private static final long serialVersionUID = 1555107078656945805L;
	
	/**
     * @see javax.servlet.Servlet#init(javax.servlet.ServletConfig)
     */
    public void init(ServletConfig sg) throws ServletException {
        super.init(sg);
        engine=CFMLEngineFactory.getInstance(sg,this);
    }
    /**
     * @see javax.servlet.http.HttpServlet#service(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    protected void service(HttpServletRequest req, HttpServletResponse rsp) throws ServletException, IOException {
        engine.serviceFile(this,req,rsp);
    }
}
