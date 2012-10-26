package railo.runtime.listener;

import railo.runtime.PageContext;
import railo.runtime.exp.PageException;

public abstract class AppListenerSupport implements ApplicationListener {
	
	/**
	 * @see railo.runtime.listener.ApplicationListener#hasOnApplicationStart()
	 */
	public boolean hasOnApplicationStart(){
		return false;
	}
	
	/**
	 * @see railo.runtime.listener.ApplicationListener#hasOnSessionStart(railo.runtime.PageContext)
	 */
	public boolean hasOnSessionStart(PageContext pc){
		return false;
	}

	/**
	 * @see railo.runtime.listener.ApplicationListener#onServerStart()
	 */
	@Override
	public void onServerStart() throws PageException {
	}

	/**
	 * @see railo.runtime.listener.ApplicationListener#onServerEnd()
	 */
	@Override
	public void onServerEnd() throws PageException {
	}

	/**
	 * @see railo.runtime.listener.ApplicationListener#onTimeout(railo.runtime.PageContext)
	 */
	@Override
	public void onTimeout(PageContext pc) {
	}
	
	public final void setType(String type) {
		// no longer used
	}
}
