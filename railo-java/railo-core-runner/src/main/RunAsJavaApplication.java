package main;

import java.util.List;

import org.mortbay.http.HttpContext;
import org.mortbay.http.HttpServer;
import org.mortbay.http.SocketListener;
import org.mortbay.http.handler.ResourceHandler;
import org.mortbay.jetty.servlet.ServletHandler;
import org.mortbay.jetty.servlet.ServletHolder;
import org.mortbay.util.MultiException;

/**
 * Runs Railo as a Java Application
 */
public class RunAsJavaApplication {
        
    public static void addContext(HttpServer server, String strContext,String host,String path, String home,String serverdir) {
        
    	if(home==null) home="./web";
        
        HttpContext context = new HttpContext();
        context.setContextPath(strContext);
        context.addWelcomeFile("index.cfm");
        context.addVirtualHost(host);
        //context.setClassPath(lib);
        server.addContext(context);
        
        // Create a servlet container
        ServletHandler servlets = new ServletHandler();
        
        context.addHandler(servlets);
        
        // Map a servlet onto the container
        ServletHolder servlet = servlets.addServlet(
        		"CFMLServlet",
        		"*.cfc/*,*.cfm/*,*.cfml/*,*.cfc,*.cfm,*.cfml",
        		//"*.cfc*,*.cfm,*.cfml*",
        		"main.servlet.CFMLServlet");
        
        servlet.setInitOrder(0);
        
        //servlet = servlets.addServlet("FileServlet","/","servlet.FileServlet");
        
        if(serverdir==null) serverdir=home;
        servlet.setInitParameter("railo-server-root",serverdir+"/context-server");
        servlet.setInitParameter("railo-web-directory",serverdir+"/context-web");
                
        //servlet = servlets.addServlet("openamf","/flashservices/gateway/*,/openamf/gateway/*","servlet.AMFServlet");
        servlet = servlets.addServlet("openamf","/openamf/gateway/*","main.servlet.AMFServlet");
        
        servlets.addServlet("AxisServlet","*.jws","org.apache.axis.transport.http.AxisServlet");
        //servlets.addServlet("AxisServlet","*.jws","AxisServlet");

        servlet = servlets.addServlet("MessageBrokerServlet","/flashservices/gateway/*,/messagebroker/*,/flex2gateway/*","flex.messaging.MessageBrokerServlet");
        servlet.setInitParameter("services.configuration.file", "/WEB-INF/flex/services-config.xml");
        
        home+=path;
           context.setResourceBase(home);
           context.addHandler(new ResourceHandler());
           
    }
    
  /**
 * @param args
 * @throws Exception 
 * @throws Exception
 */ 
public static void main (String[] args) throws Exception {
    try {
        _main(args);
    } 
    catch (MultiException e) {
        List list = e.getExceptions();
        int len=list.size();
        for(int i=0;i<len;i++) {
            ((Throwable)list.get(i)).printStackTrace();
        }
        
    }
    catch (Exception e) {
        throw e;
    }
}

public static void _main (String[] args)
    throws Exception { 
    // Create the server
    HttpServer server=new HttpServer();
      
    // Create a port listener
    SocketListener listener=new SocketListener();
    int port=8080; 
    
    listener.setPort(port);
    server.addListener(listener);
    
    // Create a context 
    addContext(server,"/","localhost","/",null,null);

    //addContext(server,"/susi/","localhost","/jm/",null,null);
    //addContext(server,"/sub1/","localhost","/subweb1/",null,null);
    //addContext(server,"/sub2/","localhost","/subweb2/",null,null);
    //addContext(server,"/","192.168.2.104","/",null,null);
    
    //addContext(server,"/","context.example.local","/",null);
    //addContext(server,"/","7of9","/",null);

    //for(int i=1;i<10;i++) 
    //    addContext(server,"/","context"+i+".example.local","/context"+i+"/",null,null);

    server.start ();
  }
}
