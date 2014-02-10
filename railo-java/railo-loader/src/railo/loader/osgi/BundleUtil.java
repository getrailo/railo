package railo.loader.osgi;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;

import railo.commons.io.log.Log;
import railo.commons.io.res.Resource;
import railo.loader.engine.CFMLEngineFactory;

public class BundleUtil {
	public static Bundle addBundle(BundleContext context,File bundle, boolean start) throws IOException, BundleException {
    	return addBundle(context,bundle.getAbsolutePath(),bundle,start);
    }
	
	public static Bundle addBundle(BundleContext context,String id,File bundle, boolean start) throws IOException, BundleException {
		CFMLEngineFactory.log(Log.LEVEL_INFO,"add bundle:"+bundle);
    	InputStream is = new FileInputStream(bundle);
		try {
        	Bundle b = context.installBundle(id,is);
        	if(start){
        		CFMLEngineFactory.log(Log.LEVEL_INFO,"start bundle:"+bundle);
            	b.start();
        	}
        	return b;
        }
        finally {
        	CFMLEngineFactory.closeEL(is);
        }
	}

	public static Bundle addBundle(BundleContext context,Resource bundle, boolean start) throws IOException, BundleException {
    	return addBundle(context,bundle.getAbsolutePath(),bundle,start);
    }
    
	public static Bundle addBundle(BundleContext context,String id,Resource bundle, boolean start) throws IOException, BundleException {
		
		InputStream is = bundle.getInputStream();
		try {
        	Bundle b = context.installBundle(id,is);
        	if(start)b.start();
        	return b;
        }
        finally {
        	is.close();
        }
	}
	

	public static void start(List<Bundle> bundles) throws BundleException {
		if(bundles==null || bundles.isEmpty()) return;
		
		Iterator<Bundle> it = bundles.iterator();
		while(it.hasNext()){
			it.next().start();
		}
	}
	
	
	
	public static String bundleState(int state, String defaultValue) {
		switch(state){
		case Bundle.UNINSTALLED: return "UNINSTALLED";
		case Bundle.INSTALLED: return "INSTALLED";
		case Bundle.RESOLVED: return "RESOLVED";
		case Bundle.STARTING: return "STARTING";
		case Bundle.STOPPING: return "STOPPING";
		case Bundle.ACTIVE: return "ACTIVE";
		}
		
		return defaultValue;
	}
}
