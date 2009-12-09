package railo.runtime.schedule;

import java.util.Map;

import railo.commons.io.SystemUtil;
import railo.commons.lang.types.RefBoolean;
import railo.runtime.CFMLFactoryImpl;
import railo.runtime.config.ConfigWeb;
 
public class OldScheduleThread extends Thread {
	
	private RefBoolean run;
	private Map contextes;

	public OldScheduleThread(RefBoolean run, Map contextes) {
		this.run=run;
		this.contextes=contextes;
	}

	public void run() {
	    while(run.toBooleanValue()) {
	    	SystemUtil.sleep(5*1000);
	    	Object[] arr = contextes.keySet().toArray();
	        Object key=null;
	        for(int i=0;i<arr.length;i++) {
	            key=arr[i];
	            run(((CFMLFactoryImpl)contextes.get(key)).getConfig());
	        }
	    }    
	}

	private void run(ConfigWeb config) {
		try {
			config.getScheduler().execute();	
		}
		catch(Throwable t){
			t.printStackTrace();
		}
	}
}
