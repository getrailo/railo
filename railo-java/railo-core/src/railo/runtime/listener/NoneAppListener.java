package railo.runtime.listener;

import java.io.IOException;

import railo.runtime.CFMLFactory;
import railo.runtime.PageContext;
import railo.runtime.PageSource;
import railo.runtime.exp.PageException;
import railo.runtime.op.Caster;

public final class NoneAppListener  extends AppListenerSupport {

	private int mode;
	private String type;

	/**
	 *
	 * @see railo.runtime.listener.ApplicationListener#onRequest(railo.runtime.PageContext, railo.runtime.PageSource)
	 */
	public void onRequest(PageContext pc,PageSource requestedPage) throws PageException {
		pc.doInclude(requestedPage);
	}

	/**
	 * @see railo.runtime.listener.ApplicationListener#onApplicationStart(railo.runtime.PageContext)
	 */
	public boolean onApplicationStart(PageContext pc) throws PageException {
		// do nothing
		return true;
	}

	/**
	 *
	 * @see railo.runtime.listener.ApplicationListener#onSessionStart(railo.runtime.PageContext)
	 */
	public void onSessionStart(PageContext pc) throws PageException {
		// do nothing
	}

	/**
	 *
	 * @see railo.runtime.listener.ApplicationListener#onApplicationEnd(railo.runtime.CFMLFactory, java.lang.String)
	 */
	public void onApplicationEnd(CFMLFactory factory, String applicationName) throws PageException {
		// do nothing	
	}

	/**
	 *
	 * @see railo.runtime.listener.ApplicationListener#onSessionEnd(railo.runtime.CFMLFactory, java.lang.String, java.lang.String)
	 */
	public void onSessionEnd(CFMLFactory cfmlFactory, String applicationName, String cfid) throws PageException {
		// do nothing
	}

	/**
	 * @see railo.runtime.listener.ApplicationListener#onDebug(railo.runtime.PageContext)
	 */
	public void onDebug(PageContext pc) throws PageException {
		try {
			pc.getDebugger().writeOut(pc);
		} 
		catch (IOException e) {
			throw Caster.toPageException(e);
		}
	}

	/**
	 *
	 * @see railo.runtime.listener.ApplicationListener#onError(railo.runtime.PageContext, railo.runtime.exp.PageException)
	 */
	public void onError(PageContext pc,PageException pe) {
		pc.handlePageException(pe);
	}

	public void setMode(int mode) {
		this.mode=mode;
	}

	/**
	 *
	 * @see railo.runtime.listener.ApplicationListener#getMode()
	 */
	public int getMode() {
		return mode;
	}
	

	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}
}