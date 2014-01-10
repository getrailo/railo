package railo.debug.loader.servlet;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public final class CFMLServlet extends RailoDebugServlet  {

    @Override
    protected void service( HttpServletRequest req, HttpServletResponse rsp ) throws ServletException, IOException {

    	engine.serviceCFML( this, req, rsp );
    }
}