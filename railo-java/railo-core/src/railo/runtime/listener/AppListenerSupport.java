package railo.runtime.listener;

import railo.runtime.PageContext;
import railo.runtime.exp.PageException;

public abstract class AppListenerSupport implements ApplicationListener {
	
	@Override
	public boolean hasOnApplicationStart(){
		return false;
	}
	
	@Override
	public boolean hasOnSessionStart(PageContext pc){
		return false;
	}

	@Override
	public void onServerStart() throws PageException {
	}

	@Override
	public void onServerEnd() throws PageException {
	}

	@Override
	public void onTimeout(PageContext pc) {
	}
	
	@Override
	public final void setType(String type) {
		// no longer used
	}
}
