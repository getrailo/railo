package railo.loader.servlet;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import railo.commons.io.log.Log;
import railo.loader.engine.CFMLEngineFactory;

/**
 */
public class CFMLServlet extends RailoServlet {

	private static final long serialVersionUID = -1878214660283329587L;
	
	/**
     * @see javax.servlet.Servlet#init(javax.servlet.ServletConfig)
     */
    public void init(ServletConfig sg) throws ServletException {
        super.init(sg);
        CFMLEngineFactory.log(Log.LEVEL_INFO, "init servlet");
        try{
        	engine=CFMLEngineFactory.getInstance(sg,this);
        }
        catch(ServletException se){
        	se.printStackTrace();// TEMP remove stacktrace
        	throw se;
        }
        catch(Throwable t){
        	t.printStackTrace();// TEMP remove stacktrace
        }
    }
    /**
     * @see javax.servlet.http.HttpServlet#service(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    protected void service(HttpServletRequest req, HttpServletResponse rsp) throws ServletException, IOException {
    	CFMLEngineFactory.log(Log.LEVEL_INFO, "service CFML");
		try{
    		engine.serviceCFML(this,req,rsp);
        }
        catch(ServletException se){
        	se.printStackTrace();// TEMP remove stacktrace
        	throw se;
        }
        catch(Throwable t){
        	t.printStackTrace();// TEMP remove stacktrace
        }
    	
    }
}
