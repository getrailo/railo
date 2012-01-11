package railo.commons.management;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryPoolMXBean;
import java.lang.management.MemoryType;
import java.util.HashMap;
import java.util.Map;

import javax.management.NotificationEmitter;

import railo.runtime.config.ConfigServer;


public class MemoryControler {
	private final static Map<String,MemoryType> types=new HashMap<String, MemoryType>();
	private static boolean init;
	public synchronized static void init(ConfigServer cs){
	      if(init) return;
			// set level
	      for (MemoryPoolMXBean pool : ManagementFactory.getMemoryPoolMXBeans()) {
	    	  types.put(pool.getName(), pool.getType());
	        // I don't know whether this approach is better, or whether
	        // we should rather check for the pool name "Tenured Gen"?
	    	  if (pool.getType() == MemoryType.HEAP && pool.isUsageThresholdSupported()) {
	    		  long maxMemory = pool.getUsage().getMax();
	    		  long warningThreshold = (long) (maxMemory * 0.9);
	    		  //long warningThreshold = maxMemory -(10*1024*1024);
	    		  pool.setUsageThreshold(warningThreshold);
	    	  }
	      }
	      
	      MemoryMXBean mbean = ManagementFactory.getMemoryMXBean();
	      NotificationEmitter emitter = (NotificationEmitter) mbean;
	      MemoryNotificationListener listener = new MemoryNotificationListener(types);
	      emitter.addNotificationListener(listener, null, cs);
	      init=true;
	}
}
