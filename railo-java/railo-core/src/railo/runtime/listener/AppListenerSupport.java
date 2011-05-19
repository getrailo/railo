package railo.runtime.listener;

import railo.runtime.PageContext;

public abstract class AppListenerSupport implements ApplicationListener {
	 
	public boolean hasOnApplicationStart(PageContext pc){
		return false;
	}

	public boolean hasOnSessionStart(PageContext pc){
		return false;
	}
}
