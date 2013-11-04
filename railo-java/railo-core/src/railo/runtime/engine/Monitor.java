package railo.runtime.engine;

import railo.commons.lang.types.RefBoolean;
import railo.runtime.config.ConfigServer;
import railo.runtime.config.ConfigServerImpl;

/**
 * own thread how check the main thread and his data 
 */
public final class Monitor extends Thread {

	
	private static final long INTERVALL = 5000;
	private final RefBoolean run;
	private final ConfigServerImpl configServer;
	
	/**
	 * @param contextes
	 * @param interval
	 * @param run 
	 */
	public Monitor(ConfigServer configServer,RefBoolean run) {
        
        this.run=run;
        this.configServer=(ConfigServerImpl) configServer;
        
	}
	
	@Override
	public void run() {
		short tries=0;
		while(run.toBooleanValue()) {
            try {
				sleep(INTERVALL);
			} 
            catch (InterruptedException e) {
				e.printStackTrace();
			}
            
            if(!configServer.isMonitoringEnabled()) return;
            railo.runtime.monitor.IntervallMonitor[] monitors = configServer.getIntervallMonitors();
            
            int logCount=0;
            if(monitors!=null)for(int i=0;i<monitors.length;i++){
            	if(monitors[i].isLogEnabled()) {
	            	logCount++;
            		try {
	            		monitors[i].log();
	        		} 
	        		catch (Throwable e) {
	        			e.printStackTrace();
	        		}
            	}
            }
            
            if(logCount==0) {
            	tries++;
            	if(tries>=10)return;
            }
	    }    
	}

}