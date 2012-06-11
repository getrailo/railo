package railo.runtime.listener;

import railo.runtime.PageContext;
import railo.runtime.PageSource;
import railo.runtime.exp.PageException;

/**
 * this lstener is executed after the application.cfc/application.cfm was invoked, but before onApplicationStart, this class can change the PageSource executed
*/
public interface RequestListener {
	
	/**
	 * execute by the Application Listener
	 * @param pc page context of the current request
	 * @param requestedPage original requested pagesource
	 * @return pagesource that should be use by the ApplicationListener
	 * @throws PageException
	 */
	public PageSource execute(PageContext pc, PageSource requestedPage) throws PageException;

}
