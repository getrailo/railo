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
	public static Bundle loadBundles(CFMLEngineFactory engFac, File cacheRootDir, File jarDirectory, File rc) throws IOException, BundleException {
		
		JarFile jf = new JarFile(rc);// TODO this should work in any case, but we should still improve this code
		
	// Manifest
		Manifest mani =  jf.getManifest();
		if(mani==null) throw new IOException("railo core ["+rc+"] is invalid, there is no META-INF/MANIFEST.MF File");
		Attributes attrs = mani.getMainAttributes();
		
		/*Iterator<Object> itt = attrs.keySet().iterator();
		System.err.println("---------------->");
		while(itt.hasNext()){
			System.err.println("->"+itt.next());
		}*/
		
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
		CFMLEngineFactory.log(Log.LEVEL_INFO, "org.osgi.framework.storage.clean:"+storageClean);
		
		// org.osgi.framework.bootdelegation
		String bootDelegation = unwrap(defProp.getProperty("org.osgi.framework.bootdelegation"));
		if(Util.isEmpty(bootDelegation)) throw new IOException("[org.osgi.framework.bootdelegation] setting is necessary in file {Railo-Core}/default.properties");
		CFMLEngineFactory.log(Log.LEVEL_INFO, "org.osgi.framework.bootdelegation:"+bootDelegation);
		
		// org.osgi.framework.bundle.parent
		String parentClassLoader = unwrap(defProp.getProperty("org.osgi.framework.bundle.parent"));
		if(Util.isEmpty(parentClassLoader)) parentClassLoader=Constants.FRAMEWORK_BUNDLE_PARENT_FRAMEWORK;
		CFMLEngineFactory.log(Log.LEVEL_INFO, "org.osgi.framework.bundle.parent:"+parentClassLoader);
		
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
		CFMLEngineFactory.log(Log.LEVEL_INFO, "felix.log.level (1 = error, 2 = warning, 3 = information, and 4 = debug):"+logLevel);
		
		
		BundleContext bc = engFac.getFelix(cacheRootDir,storageClean,bootDelegation,parentClassLoader,logLevel).getBundleContext();
		
		// get jars needed for that core
		String rb = attrs.getValue("Require-Bundle");
		if(Util.isEmpty(rb)) {
			throw new IOException("railo core ["+rc+"] is invalid, no Require-Bundle defintion found in the META-INF/MANIFEST.MF File");
		}
		
		// load Required/Available Bundles
		Map<String, String> requiredBundles=readRequireBundle(rb); // <bundle-name,bundle-version>
		Map<String, File> availableBundles = loadAvailableBundles(jarDirectory);
		
		// Add Required Bundles
		Iterator<Entry<String, String>> it = requiredBundles.entrySet().iterator();
		Entry<String, String> e;
		File f;
		List<Bundle> bundles=new ArrayList<Bundle>();
		while(it.hasNext()){
			e = it.next();
			f = availableBundles.get(e.getKey()+"|"+e.getValue());
			if(f==null) throw new IOException("there is no bundle ["+e.getKey()+";bundle-version="+e.getValue()+"] available"); // MUST load bundle from somewhere
			bundles.add(BundleUtil.addBundle(bc, f, false));
		}
		
		// Add Railo core Bundle
		Bundle bundle;
		bundles.add(bundle=BundleUtil.addBundle(bc, rc, false));
		
		// Start the bundles
		BundleUtil.start(bundles);
		
		return bundle;
	}
	
	private static Map<String, File> loadAvailableBundles(File jarDirectory) throws IOException {
		Map<String,File> rtn=new HashMap<String, File>();
		File[] jars = jarDirectory.listFiles();
		JarFile jf;
		String symbolicName,version;
		Attributes attrs;
		for(int i=0;i<jars.length;i++){
			if(!jars[i].isFile() || !jars[i].getName().endsWith(".jar")) continue;
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
}
