package main.servlet;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import railo.loader.engine.CFMLEngine;

/**
 */
public final class FileServlet extends HttpServlet {

    private CFMLEngine engine;
    
    /**
     * @see javax.servlet.Servlet#init(javax.servlet.ServletConfig)
     */
    public void init(ServletConfig sg) throws ServletException {
        super.init(sg);
        engine=CFMLEngineFactoryDummy.getInstance(sg);
    }
    /**
     * @see javax.servlet.http.HttpServlet#service(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    protected void service(HttpServletRequest req, HttpServletResponse rsp) throws ServletException, IOException {
        engine.serviceFile(this,req,rsp);
    }
}