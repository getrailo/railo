package railo.runtime.engine;

import java.io.IOException;

import railo.commons.lang.types.RefBoolean;
import railo.runtime.config.ConfigServer;
import railo.runtime.config.ConfigServerImpl;
import railo.runtime.surveillance.Memory;

/**
 * own thread how check the main thread and his data 
 */
public final class Monitor extends Thread {

	
	private final RefBoolean run;
	private final ConfigServer configServer;

	/**
	 * @param contextes
	 * @param interval
	 * @param run 
	 */
	public Monitor(ConfigServer configServer,RefBoolean run) {		
        
        this.run=run;
        this.configServer=configServer;
        
	}
	
	/**
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		
		while(run.toBooleanValue()) {
            try {
				sleep(Memory.INTERVALL);
			} 
            catch (InterruptedException e) {
				e.printStackTrace();
			}
            
            Memory memory = ((ConfigServerImpl)configServer).getMemoryMonitor();
	        
    		try {
    			memory.log();
    		} 
    		catch (IOException e) {
    			e.printStackTrace();
    		}
            
	    }    
	}

}