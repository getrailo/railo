package railo.runtime;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspFactory;

import railo.loader.engine.CFMLEngine;
import railo.runtime.config.ConfigWeb;
import railo.runtime.query.QueryCache;

/**
 * implements a JSP Factory, this class procduce JSP Compatible PageContext Object
 * this object holds also the must interfaces to coldfusion specified functionlity
 */
public abstract class CFMLFactory extends JspFactory {
	
    /**
     * reset the PageContexes
     */
    public abstract void resetPageContext();
    
	
	/**
	 * similar to getPageContext Method but return the concret implementation of the railo PageCOntext
	 * and take the HTTP Version of the Servlet Objects
	 * @param servlet
	 * @param req
	 * @param rsp
	 * @param errorPageURL
	 * @param needsSession
	 * @param bufferSize
	 * @param autoflush
	 * @return return the page<context
	 */
	public abstract PageContext getRailoPageContext(
	HttpServlet servlet,
	HttpServletRequest req,
	HttpServletResponse rsp,
        String errorPageURL,
		boolean needsSession,
		int bufferSize,
		boolean autoflush);

	/**
	 * Similar to the releasePageContext Method, but take railo PageContext as entry
	 * @param pc
	 */
	public abstract void releaseRailoPageContext(PageContext pc);
    
    /**
	 * check timeout of all running threads, downgrade also priority from all thread run longer than 10 seconds
	 */
	public abstract void checkTimeout();
	
	/**
	 * @return returns the query cache
	 */
	public abstract QueryCache getDefaultQueryCache();

	/**
	 * @return returns count of pagecontext in use
	 */
	public abstract int getUsedPageContextLength();
	
    /**
     * @return Returns the config.
     */
    public abstract ConfigWeb getConfig();

    /**
     * @return label of the factory
     */
    public abstract Object getLabel();

    /**
     * @deprecated no replacement
     * @param label
     */
    public abstract void setLabel(String label);

	/**
	 * @return the servlet
	 */
	public abstract HttpServlet getServlet();


	public abstract CFMLEngine getEngine();


}