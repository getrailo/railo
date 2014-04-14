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
import org.osgi.framework.Constants;

import railo.commons.io.log.Log;
import railo.loader.engine.CFMLEngineFactory;
import railo.loader.util.Util;

public class BundleUtil {
	/*public static Bundle addBundlex(BundleContext context,File bundle, boolean start) throws IOException, BundleException {
    	return addBundle(context,bundle.getAbsolutePath(),bundle,start);
    }*/

	public static Bundle addBundle(CFMLEngineFactory factory,BundleContext context,File bundle) throws IOException, BundleException {
		factory.log(Log.LEVEL_INFO,"add bundle:"+bundle);
    	InputStream is = new FileInputStream(bundle);
		try {
			Bundle b = context.installBundle(bundle.getAbsolutePath(),is);
			//if(start)start(factory,b);
        	return b;
        }
        finally {
        	CFMLEngineFactory.closeEL(is);
        }
	}
	
	
	
	/*
	 * at railo.loader.osgi.BundleLoader.loadBundles(BundleLoader.java:41)
	at railo.loader.engine.CFMLEngineFactory.getCore(CFMLEngineFactory.java:388)
	at railo.loader.engine.CFMLEngineFactory.initEngine(CFMLEngineFactory.java:254)
	at railo.loader.engine.CFMLEngineFactory.getEngine(CFMLEngineFactory.java:209)
	at railo.loader.engine.CFMLEngineFactory.getUpdateLocation(CFMLEngineFactory.java:561)
	at railo.loader.engine.CFMLEngineFactory.downloadBundle(CFMLEngineFactory.java:513)
	at railo.loader.osgi.BundleLoader.loadBundles(BundleLoader.java:119)
	at railo.loader.engine.CFMLEngineFactory.getCore(CFMLEngineFactory.java:388)
	at railo.loader.engine.CFMLEngineFactory.initEngine(CFMLEngineFactory.java:254)
	at railo.loader.engine.CFMLEngineFactory.getEngine(CFMLEngineFactory.java:209)
	at railo.loader.engine.CFMLEngineFactory.getUpdateLocation(CFMLEngineFactory.java:561)
	at railo.loader.engine.CFMLEngineFactory.downloadBundle(CFMLEngineFactory.java:513)
	at railo.loader.osgi.BundleLoader.loadBundles(BundleLoader.java:119)
	at railo.loader.engine.CFMLEngineFactory.getCore(CFMLEngineFactory.java:388)
	at railo.loader.engine.CFMLEngineFactory.initEngine(CFMLEngineFactory.java:254)
	at railo.loader.engine.CFMLEngineFactory.getEngine(CFMLEngineFactory.java:209)
	at railo.loader.engine.CFMLEngineFactory.getUpdateLocation(CFMLEngineFactory.java:561)
	at railo.loader.engine.CFMLEngineFactory.downloadBundle(CFMLEngineFactory.java:513)
	at railo.loader.osgi.BundleLoader.loadBundles(BundleLoader.java:119)
	at railo.loader.engine.CFMLEngineFactory.getCore(CFMLEngineFactory.java:388)
	at railo.loader.engine.CFMLEngineFactory.initEngine(CFMLEngineFactory.java:254)
	at railo.loader.engine.CFMLEngineFactory.getEngine(CFMLEngineFactory.java:209)
	at railo.loader.engine.CFMLEngineFactory.getUpdateLocation(CFMLEngineFactory.java:561)
	at railo.loader.engine.CFMLEngineFactory.downloadBundle(CFMLEngineFactory.java:513)
	at railo.loader.osgi.BundleLoader.loadBundles(BundleLoader.java:119)
	at railo.loader.engine.CFMLEngineFactory.getCore(CFMLEngineFactory.java:388)
	at railo.loader.engine.CFMLEngineFactory.initEngine(CFMLEngineFactory.java:254)
	at railo.loader.engine.CFMLEngineFactory.getEngine(CFMLEngineFactory.java:209)
	at railo.loader.engine.CFMLEngineFactory.getUpdateLocation(CFMLEngineFactory.java:561)
	at railo.loader.engine.CFMLEngineFactory.downloadBundle(CFMLEngineFactory.java:513)
	at railo.loader.osgi.BundleLoader.loadBundles(BundleLoader.java:119)

	 */

	public static void start(CFMLEngineFactory factory,List<Bundle> bundles) throws BundleException {
		if(bundles==null || bundles.isEmpty()) return;
		
		Iterator<Bundle> it = bundles.iterator();
		while(it.hasNext()){
			start(factory,it.next());
		}
	}
	

	private static void start(CFMLEngineFactory factory, Bundle bundle) throws BundleException {
		String fh=bundle.getHeaders().get("Fragment-Host");
		if(!Util.isEmpty(fh)) {
			factory.log(Log.LEVEL_INFO,"do not start ["+bundle.getSymbolicName()+"], because this is a fragment bundle for ["+fh+"]");
			return;
		}
		
		factory.log(Log.LEVEL_INFO,"start bundle:"+bundle.getSymbolicName()+":"+bundle.getVersion().toString());
		bundle.start();
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
	


	public static String toFrameworkBundleParent(String str) throws IOException {
		if(str!=null) {
			str=str.trim();
			if(Constants.FRAMEWORK_BUNDLE_PARENT_FRAMEWORK.equalsIgnoreCase(str)) return Constants.FRAMEWORK_BUNDLE_PARENT_FRAMEWORK;
			if(Constants.FRAMEWORK_BUNDLE_PARENT_APP.equalsIgnoreCase(str)) return Constants.FRAMEWORK_BUNDLE_PARENT_APP;
			if(Constants.FRAMEWORK_BUNDLE_PARENT_BOOT.equalsIgnoreCase(str)) return Constants.FRAMEWORK_BUNDLE_PARENT_BOOT;
			if(Constants.FRAMEWORK_BUNDLE_PARENT_EXT.equalsIgnoreCase(str)) return Constants.FRAMEWORK_BUNDLE_PARENT_EXT;
		}
		throw new IOException("value ["+str+"] for ["+Constants.FRAMEWORK_BUNDLE_PARENT+"] defintion is invalid, " +
				"valid values are ["+Constants.FRAMEWORK_BUNDLE_PARENT_APP+", "+Constants.FRAMEWORK_BUNDLE_PARENT_BOOT+", "+Constants.FRAMEWORK_BUNDLE_PARENT_EXT+", "+Constants.FRAMEWORK_BUNDLE_PARENT_FRAMEWORK+"]");
	}
}
