package railo.runtime.listener;

import railo.runtime.PageContext;
import railo.runtime.exp.PageException;

public abstract class AppListenerSupport implements ApplicationListener {
	/*/ 
	public boolean hasOnApplicationStart(){
		return false;
	}*/
	// FUTURE add to interface
	public boolean hasOnSessionStart(PageContext pc){
		return false;
	}

	/* (non-Javadoc)
	 * @see railo.runtime.listener.ApplicationListener#onServerStart()
	 */
	@Override
	public void onServerStart() throws PageException {
	}

	/* (non-Javadoc)
	 * @see railo.runtime.listener.ApplicationListener#onServerEnd()
	 */
	@Override
	public void onServerEnd() throws PageException {
	}

	/* (non-Javadoc)
	 * @see railo.runtime.listener.ApplicationListener#onTimeout(railo.runtime.PageContext)
	 */
	@Override
	public void onTimeout(PageContext pc) {
	}
}
