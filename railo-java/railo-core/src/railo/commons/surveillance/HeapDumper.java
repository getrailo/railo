package railo.commons.surveillance;

import java.io.IOException;
import java.lang.management.ManagementFactory;

import javax.management.MBeanServer;

import railo.commons.io.SystemUtil;
import railo.commons.io.res.Resource;
import railo.commons.io.res.type.file.FileResource;

import com.sun.management.HotSpotDiagnosticMXBean;

public class HeapDumper {
	
	/**
	 * Dumps the heap to the outputFile file in the same format as the hprof heap dump.
	 * If this method is called remotely from another process, the heap dump output is written to a file named outputFile on the machine where the target VM is running. If outputFile is a relative path, it is relative to the working directory where the target VM was started.
	 * @param res Resource to write the .hprof file.
	 * @param live  if true dump only live objects i.e. objects that are reachable from others
	 * @throws IOException 
	 */
	public static void dumpTo(Resource res, boolean live) throws IOException {
		MBeanServer mbserver = ManagementFactory.getPlatformMBeanServer();
		HotSpotDiagnosticMXBean mxbean = ManagementFactory.newPlatformMXBeanProxy( mbserver, "com.sun.management:type=HotSpotDiagnostic", HotSpotDiagnosticMXBean.class );
		
		String path;
		Resource tmp=null;
		if(res instanceof FileResource) path=res.getAbsolutePath();
		else {
			tmp=SystemUtil.getTempFile("hprof",false);
			path=tmp.getAbsolutePath();
		}
		try{
			// it only 
			mxbean.dumpHeap(path, live);
		}
		finally{
			if(tmp!=null && tmp.exists()){
				tmp.moveTo(res);
			}
		}
		
   	}
}
