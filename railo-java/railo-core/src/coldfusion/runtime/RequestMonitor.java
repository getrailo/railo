package coldfusion.runtime;

import railo.runtime.engine.ThreadLocalPageContext;

/**
 * this is just a wrapper class to simulate the ACF implementation
 */
public class RequestMonitor {
	//public void beginRequestMonitor(String str){/* ignored */ }
	//public void endRequestMonitor(){/* ignored */ }
	//public void checkSlowRequest(Object obj){/* ignored */ }
	//public boolean isRequestTimedOut()
	public long getRequestTimeout(){
		return ThreadLocalPageContext.get().getRequestTimeout();
	}
	
	
	public void overrideRequestTimeout(long timeout){
		ThreadLocalPageContext.get().setRequestTimeout(timeout);
	}
	
	
}
