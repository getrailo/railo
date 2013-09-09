package railo.debug.loader.servlet;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public final class RESTServlet extends RailoDebugServlet  {

    @Override
    protected void service( HttpServletRequest req, HttpServletResponse rsp ) throws ServletException, IOException {

        engine.serviceRest( this, req, rsp );
    }
}