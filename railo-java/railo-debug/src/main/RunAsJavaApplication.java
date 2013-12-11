package main;

import org.mortbay.http.HttpContext;
import org.mortbay.http.HttpServer;
import org.mortbay.http.SocketListener;
import org.mortbay.http.handler.ResourceHandler;
import org.mortbay.jetty.servlet.ServletHandler;
import org.mortbay.jetty.servlet.ServletHolder;
import org.mortbay.jetty.servlet.WebApplicationContext;
import org.mortbay.util.MultiException;

import java.io.File;
import java.util.List;


/**
 * Runs Railo as a Java Application
 */
public class RunAsJavaApplication {

    private static String port = "8888";

    public static void addContext(HttpServer server, String strContext, String host, String path, String appDir, String webContextDir, String adminContextDir) {

        if (appDir == null) appDir = "./web";
        if (webContextDir == null) webContextDir = appDir + "/WEB-INF/railo";
        if (adminContextDir == null) adminContextDir = appDir + "/WEB-INF/lib/railo-server";
        System.out.println("appdir:" + appDir);
        System.out.println("webcontext:" + webContextDir);
        System.out.println("servercontext:" + adminContextDir);

        HttpContext context = new HttpContext();
        //context.setClassLoader(new ContextClassloader());
        context.setContextPath(strContext);
        context.addWelcomeFile("index.cfm");

	    if ( host != null && !host.isEmpty() )
	        context.addVirtualHost(host);

        //context.setClassPath(lib);
        server.addContext(context);

        // Create a servlet container
        ServletHandler servlets = new ServletHandler();
        context.addHandler(servlets);

        // Map a servlet onto the container
        ServletHolder servlet = servlets.addServlet("CFMLServlet", "*.cfc/*,*.cfm/*,*.cfml/*,*.cfc,*.cfm,*.cfml", "railo.debug.loader.servlet.CFMLServlet");
        servlet.setInitOrder(0);

        if (adminContextDir == null) webContextDir = appDir;
        servlet.setInitParameter("railo-server-directory", adminContextDir);
        servlet.setInitParameter("railo-web-directory", webContextDir);

        // Uncomment line below to debug Railo REST Servlet
        //servlet = servlets.addServlet("RESTServlet", "/rest/*", "railo.debug.loader.servlet.RESTServlet");

        //servlet = servlets.addServlet("FileServlet","/","servlet.FileServlet");

        /* Uncomment to add remote flash support; toggle block comment by adding/removing a '/' at the beginning of this line
        servlet = servlets.addServlet("openamf","/flashservices/gateway/*,/openamf/gateway/*","servlet.AMFServlet");
        servlet = servlets.addServlet("openamf","/openamf/gateway/*","railo.loader.servlet.AMFServlet");
        servlet = servlets.addServlet("MessageBrokerServlet","/flashservices/gateway/*,/messagebroker/*,/flex2gateway/*","flex.messaging.MessageBrokerServlet");
        servlet.setInitParameter("services.configuration.file", "/WEB-INF/flex/services-config.xml");
        //*/

        appDir += path;
        context.setResourceBase(appDir);
        context.addHandler(new ResourceHandler());
    }

    public static void addWebXmlContext(HttpServer server, String strContext, String host, String path, String appDir, String webContextDir, String adminContextDir) {

        if (appDir == null) appDir = "./web";
        if (webContextDir == null) webContextDir = appDir + "/WEB-INF/railo";
        if (adminContextDir == null) adminContextDir = appDir + "/WEB-INF/lib/railo-server";
        System.out.println("appdir:" + appDir);
        System.out.println("webcontext:" + webContextDir);
        System.out.println("servercontext:" + adminContextDir);
        WebApplicationContext context = new WebApplicationContext(appDir);
        context.setContextPath(strContext);

	    if ( host != null && !host.isEmpty() )
	        context.addVirtualHost(host);

        server.addContext(context);
        appDir += path;
        context.addHandler(new ResourceHandler());
    }

    /**
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        try {
            _main(args);
        } catch (MultiException e) {
            List list = e.getExceptions();
            int len = list.size();
            for (int i = 0; i < len; i++) {
                ((Throwable) list.get(i)).printStackTrace();
            }
        } catch (Exception e) {
            throw e;
        }
    }

    public static void _main(String[] args)
            throws Exception {
        // Create the server
        HttpServer server = new HttpServer();
        String portArg = port;
        String appDir = "./web";
        String webContextDir = appDir + "/WEB-INF/railo";
        String serverContextDir = appDir + "/WEB-INF/lib/railo";
        if (args.length > 0) {
            portArg = args[0];
        }
        if (args.length > 1) {
            appDir = args[1];
            webContextDir = appDir + "/WEB-INF/railo";
            serverContextDir = appDir + "/WEB-INF/lib/railo-server";
        }
        if (args.length > 2) {
            webContextDir = args[1];
        }
        if (args.length > 3) {
            serverContextDir = args[2];
        }

        // Create a port listener
        SocketListener listener = new SocketListener();
        int port = Integer.parseInt(portArg);

        listener.setPort(port);
        server.addListener(listener);

	    String host = null;

        // Create a context
        File webxml = new File(appDir + "/WEB-INF/web.xml");
        if (webxml.exists()) {
            addWebXmlContext(server, "/", host, "/", appDir, webContextDir, serverContextDir);
        } else {
            addContext(server, "/", host, "/", appDir, webContextDir, serverContextDir);
        }

        //addContext(server,"/susi/","localhost","/jm/",null,null);
        //addContext(server,"/sub1/","localhost","/subweb1/",null,null);
        //addContext(server,"/sub2/","localhost","/subweb2/",null,null);
        //addContext(server,"/","192.168.2.104","/",null,null);

        //addContext(server,"/","context.example.local","/",null);
        //addContext(server,"/","7of9","/",null);

        //for(int i=1;i<10;i++)
        //    addContext(server,"/","context"+i+".example.local","/context"+i+"/",null,null);


        server.start();

	    if ( host != null && !host.isEmpty() )
		    DesktopUtil.launchBrowser( host, port, false );
    }
}
