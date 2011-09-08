package railo.runtime.engine;

import java.lang.reflect.Method;

import railo.commons.lang.types.RefBoolean;
import railo.runtime.config.ConfigServer;
import railo.runtime.config.ConfigServerImpl;

/**
 * own thread how check the main thread and his data 
 */
public final class Monitor extends Thread {

	
	private static final long INTERVALL = 5000;
	private static final Object[] EMPTY = new Object[0];
	private final RefBoolean run;
	private final ConfigServerImpl configServer;
	private Method log;

	/**
	 * @param contextes
	 * @param interval
	 * @param run 
	 */
	public Monitor(ConfigServer configServer,RefBoolean run) {		
        
        this.run=run;
        this.configServer=(ConfigServerImpl) configServer;
        
	}
	
	/**
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		short tries=0;
		while(run.toBooleanValue()) {
            try {
				sleep(INTERVALL);
			} 
            catch (InterruptedException e) {
				e.printStackTrace();
			}
           
            Object[] monitors = configServer.getMonitors();
            if(monitors==null) {
            	tries++;
            	if(tries>=10)return;
            	// MUST better impl
            }
            
            
            
            if(monitors!=null)for(int i=0;i<monitors.length;i++){
            	try {
            		if(log==null)
            			log=monitors[i].getClass().getMethod("log", new Class[0]);
        			log.invoke(monitors[i], EMPTY);// FUTURE add interface to loader and use interface
        		} 
        		catch (Throwable e) {
        			e.printStackTrace();
        		}
            }
	    }    
	}

}