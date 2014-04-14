package railo.loader.osgi;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.util.zip.ZipEntry;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.Constants;


import railo.commons.io.log.Log;
import railo.loader.engine.CFMLEngineFactory;
import railo.loader.util.Util;

public class BundleLoader {
	
	/**
	 * build (if necessary) a bundle and load it
	 * @param cfmlEngineFactory 
	 * @param bundleDir directory where bundles are located
	 * @param rc Railo Core File
	 * @throws BundleException 
	 * @throws IOException 
	 * @throws BundleBuilderFactoryException 
	 */
	public static Bundle loadBundles(CFMLEngineFactory engFac, File cacheRootDir, File jarDirectory, File rc, Bundle old) throws IOException, BundleException {
		JarFile jf = new JarFile(rc);// TODO this should work in any case, but we should still improve this code
		//new Throwable().printStackTrace();
	// Manifest
		Manifest mani =  jf.getManifest();
		if(mani==null) throw new IOException("railo core ["+rc+"] is invalid, there is no META-INF/MANIFEST.MF File");
		Attributes attrs = mani.getMainAttributes();
		
	// default properties
		Properties defProp = loadDefaultProperties(jf);
		
	// Get data from Manifest and default.properties
		
		// Railo Core Version
		//String rcv = unwrap(defProp.getProperty("railo.core.version"));
		//if(Util.isEmpty(rcv)) throw new IOException("railo core ["+rc+"] is invalid, no core version is defined in the {Railo-Core}/default.properties File");
		//int version = CFMLEngineFactory.toInVersion(rcv);
		
		// org.osgi.framework.storage.clean
		String storageClean = unwrap(defProp.getProperty("org.osgi.framework.storage.clean"));
		if(Util.isEmpty(storageClean)) storageClean=Constants.FRAMEWORK_STORAGE_CLEAN_ONFIRSTINIT;
		engFac.log(Log.LEVEL_INFO, "org.osgi.framework.storage.clean:"+storageClean);
		
		// org.osgi.framework.bootdelegation
		String bootDelegation = unwrap(defProp.getProperty("org.osgi.framework.bootdelegation"));
		if(Util.isEmpty(bootDelegation)) throw new IOException("[org.osgi.framework.bootdelegation] setting is necessary in file {Railo-Core}/default.properties");
		engFac.log(Log.LEVEL_INFO, "org.osgi.framework.bootdelegation:"+bootDelegation);
		
		// org.osgi.framework.bundle.parent
		String parentClassLoader = unwrap(defProp.getProperty("org.osgi.framework.bundle.parent"));
		if(Util.isEmpty(parentClassLoader)) parentClassLoader=Constants.FRAMEWORK_BUNDLE_PARENT_FRAMEWORK;
		else parentClassLoader=BundleUtil.toFrameworkBundleParent(parentClassLoader);
		engFac.log(Log.LEVEL_INFO, "org.osgi.framework.bundle.parent:"+parentClassLoader);

		// felix.log.level
		int logLevel=1; // 1 = error, 2 = warning, 3 = information, and 4 = debug
		String strLogLevel = unwrap(defProp.getProperty("felix.log.level"));
		if(!Util.isEmpty(strLogLevel)) {
			if("warn".equalsIgnoreCase(strLogLevel) || "warning".equalsIgnoreCase(strLogLevel)) 
				logLevel=2;
			else if("info".equalsIgnoreCase(strLogLevel) || "information".equalsIgnoreCase(strLogLevel)) 
				logLevel=3;
			else if("debug".equalsIgnoreCase(strLogLevel)) 
				logLevel=4;
		}
		engFac.log(Log.LEVEL_INFO, "felix.log.level (1 = error, 2 = warning, 3 = information, and 4 = debug):"+logLevel);
		
		// resuse existing BundleContext if possible
		BundleContext bc;
		if(old!=null) {
			bc=old.getBundleContext();
			removeBundles(bc);
		}
		else bc = engFac.getFelix(cacheRootDir,storageClean,bootDelegation,parentClassLoader,logLevel).getBundleContext();
		
		// get bundle needed for that core
		String rb = attrs.getValue("Require-Bundle");
		if(Util.isEmpty(rb)) {
			throw new IOException("railo core ["+rc+"] is invalid, no Require-Bundle defintion found in the META-INF/MANIFEST.MF File");
		}
		
		// get fragments needed for that core (Railo specific Key)
		String rbf = attrs.getValue("Require-Bundle-Fragment");
		
		
		// load Required/Available Bundles
		Map<String, String> requiredBundles=readRequireBundle(rb); // <bundle-name,bundle-version>
		Map<String, String> requiredBundleFragments=readRequireBundle(rbf); // <bundle-name,bundle-version>
		Map<String, File> availableBundles = loadAvailableBundles(jarDirectory);
		
		// Add Required Bundles
		Iterator<Entry<String, String>> it = requiredBundles.entrySet().iterator();
		Entry<String, String> e;
		File f;
		String id;
		List<Bundle> bundles=new ArrayList<Bundle>();
		while(it.hasNext()){
			e = it.next();
			id=e.getKey()+"|"+e.getValue();
			f = availableBundles.get(id);
			
			if(f==null) f=engFac.downloadBundle(e.getKey(), e.getValue());
			bundles.add(BundleUtil.addBundle(engFac,bc, f));
		}
		
		// Add Required Bundle Fragments
		it = requiredBundleFragments.entrySet().iterator();
		List<Bundle> fragments=new ArrayList<Bundle>();
		while(it.hasNext()){
			e = it.next();
			id=e.getKey()+"|"+e.getValue();
			f = availableBundles.get(id);
			
			if(f==null) f=engFac.downloadBundle(e.getKey(), e.getValue());
			fragments.add(BundleUtil.addBundle(engFac,bc, f));
		}
		
		/* list existing bundles
		Bundle[] _bundles = bc.getBundles();
		for(int i=0;i<_bundles.length;i++){
			System.err.println(">"+_bundles[i].getSymbolicName()+":"+_bundles[i].getVersion()+":"+BundleUtil.bundleState(_bundles[i].getState(),""));
		}*/
			
		// Add Railo core Bundle
		Bundle bundle;
		bundles.add(bundle=BundleUtil.addBundle(engFac,bc,rc));
		
		// Start the bundles
		BundleUtil.start(engFac,bundles);
		
		return bundle;
	}
	
	private static File getBundleFromRemote(String key, String value, File jarDirectory) {
		return null;
	}

	private static Map<String, File> loadAvailableBundles(File jarDirectory) throws IOException {
		Map<String,File> rtn=new HashMap<String, File>();
		File[] jars = jarDirectory.listFiles();
		JarFile jf;
		String symbolicName,version;
		Attributes attrs;
		for(int i=0;i<jars.length;i++){
			if(!jars[i].isFile() || !jars[i].getName().endsWith(".jar")) continue;
			try{
				jf=new JarFile(jars[i]);
				attrs = jf.getManifest().getMainAttributes();
				symbolicName=attrs.getValue("Bundle-SymbolicName");
				version=attrs.getValue("Bundle-Version");
				if(Util.isEmpty(symbolicName))
					throw new IOException("OSGi bundle ["+jars[i]+"] is invalid, {Railo-Core}META-INF/MANIFEST.MF does not contain a \"Bundle-SymbolicName\"");
				if(Util.isEmpty(version))
					throw new IOException("OSGi bundle ["+jars[i]+"] is invalid, {Railo-Core}META-INF/MANIFEST.MF does not contain a \"Bundle-Version\"");
				rtn.put(symbolicName+"|"+version, jars[i]);
			}
			catch(Throwable t){}
		}
		return rtn;
	}

	private static Map<String, String> readRequireBundle(String rb) throws IOException {
		HashMap<String, String> rtn=new HashMap<String, String>();
		StringTokenizer st=new StringTokenizer(rb,","),stl;
		String line,jarName,jarVersion=null,token;
		int index;
		while (st.hasMoreTokens()) {
			line=st.nextToken().trim();
			if(Util.isEmpty(line))continue;
			
			stl=new StringTokenizer(line,";");
			
			// first is the name
			jarName=stl.nextToken().trim();
			
			while(stl.hasMoreTokens()){
				token=stl.nextToken().trim();
				if(token.startsWith("bundle-version") && (index=token.indexOf('='))!=-1) {
					jarVersion=token.substring(index+1).trim();
				}
			}
			if(jarVersion==null) throw new IOException("missing \"bundle-version\" info in the following \"Require-Bundle\" record: \""+jarName+"\"");
			rtn.put(jarName, jarVersion);
		}
		return rtn;
	}

	private static String unwrap(String str) {
		return str==null?null: CFMLEngineFactory.removeQuotes(str, true);
	}

	private static Properties loadDefaultProperties(JarFile jf) throws IOException {
		ZipEntry ze = jf.getEntry("default.properties");
		if(ze==null) throw new IOException("the railo core has no default.properties file!");
		
		Properties prop = new Properties();
		InputStream is=null;
		try{
			is = jf.getInputStream(ze);
			prop.load(is);
		}
		finally {
			CFMLEngineFactory.closeEL(is);
		}
		return prop;
	}
	

	public static void removeBundles(BundleContext bc) throws BundleException {
		Bundle[] bundles = bc.getBundles();
		for(int i=0;i<bundles.length;i++){
			removeBundle(bundles[i]);
		}
	}
	
	public static void removeBundle(Bundle bundle) throws BundleException {
		if(bundle==null) return;
		
		log(Log.LEVEL_INFO,"remove bundle:"+bundle.getSymbolicName());
		
		// wait for starting
		int sleept=0;
		while(bundle.getState()==Bundle.STARTING) {
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				break;
			}
			sleept+=10;
			if(sleept>3000) break; // only wait for 3 seconds
		}
		
		// force stopping (even when still starting)
		if(bundle.getState()==Bundle.ACTIVE || bundle.getState()==Bundle.STARTING) bundle.stop();
		
		// wait for stopping
		sleept=0;
		while(bundle.getState()==Bundle.STOPPING) {
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				break;
			}
			sleept+=10;
			if(sleept>3000) break; // only wait for 3 seconds
		}

		if(bundle.getState()!=Bundle.UNINSTALLED) bundle.uninstall();
	}

	private static void log(int level, String msg) { 
		System.out.println(msg);
	}
}
