package railo.loader.osgi;

import java.io.File;
import java.io.IOException;
import java.util.StringTokenizer;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.Constants;


import railo.commons.io.log.Log;
import railo.loader.engine.CFMLEngineFactory;
import railo.loader.osgi.factory.BundleBuilderFactory;
import railo.loader.osgi.factory.BundleBuilderFactoryException;
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
	public static Bundle buildAndLoad(CFMLEngineFactory engFac, File cacheRootDir, File bundleDir,File jarDirectory, File rc) throws IOException, BundleException, BundleBuilderFactoryException {
		
		
		JarFile jf = new JarFile(rc);// TODO this should work in any case, but we should still improve this code
		
		// Manifest
		Manifest mani =  jf.getManifest();
		if(mani==null) throw new IOException("railo core ["+rc+"] is invalid, no core version is defined in the META-INF/MANIFEST.MF File");
		Attributes attrs = mani.getMainAttributes();
		
		// Railo Core Version
		String rcv = attrs.getValue("railo-core-version");
		if(Util.isEmpty(rcv)) throw new IOException("railo core ["+rc+"] is invalid, no core version is defined in the META-INF/MANIFEST.MF File");
		int version = CFMLEngineFactory.toInVersion(rcv);
		
		// org.osgi.framework.storage.clean
		String storageClean = attrs.getValue("org-osgi-framework-storage-clean");
		if(Util.isEmpty(storageClean)) storageClean=Constants.FRAMEWORK_STORAGE_CLEAN_ONFIRSTINIT;
		CFMLEngineFactory.log(Log.LEVEL_INFO, "org-osgi-framework-storage-clean:"+storageClean);
		
		// org-osgi-framework-bootdelegation
		String bootDelegation = attrs.getValue("org-osgi-framework-bootdelegation");
		if(Util.isEmpty(bootDelegation)) throw new IOException("[org.osgi.framework.bootdelegation] setting is necessary in file {Railo-Core}/META-INF/MANIFEST.MF");
		CFMLEngineFactory.log(Log.LEVEL_INFO, "org-osgi-framework-bootdelegation:"+bootDelegation);
		
		// org-osgi-framework-bundle-parent
		String parentClassLoader = attrs.getValue("org-osgi-framework-bundle-parent");
		if(Util.isEmpty(parentClassLoader)) parentClassLoader=Constants.FRAMEWORK_BUNDLE_PARENT_FRAMEWORK;
		CFMLEngineFactory.log(Log.LEVEL_INFO, "org-osgi-framework-bundle-parent:"+parentClassLoader);
		
		// felix.log.level org.apache.felix.framework.Logger
		int logLevel=1; // 1 = error, 2 = warning, 3 = information, and 4 = debug
		String strLogLevel = attrs.getValue("felix-log-level");
		if(!Util.isEmpty(strLogLevel)) {
			if("warn".equalsIgnoreCase(strLogLevel) || "warning".equalsIgnoreCase(strLogLevel)) 
				logLevel=2;
			else if("info".equalsIgnoreCase(strLogLevel) || "information".equalsIgnoreCase(strLogLevel)) 
				logLevel=3;
			else if("debug".equalsIgnoreCase(strLogLevel)) 
				logLevel=4;
		}
		CFMLEngineFactory.log(Log.LEVEL_INFO, "felix-log-level (1 = error, 2 = warning, 3 = information, and 4 = debug):"+logLevel);
		
		
		BundleContext bc = engFac.getFelix(cacheRootDir,storageClean,bootDelegation,parentClassLoader,logLevel).getBundleContext();
		
		
		// is there already a bundle for this version?
		File bundle = new File(bundleDir,version+".bundle");
		if(bundle.isFile()) return load(bc,bundle);
		
		// Label
		String str = attrs.getValue("Label");
		if(!Util.isEmpty(str)) str="Railo Core ("+rcv+")";
		
		
		BundleBuilderFactory factory=new BundleBuilderFactory(str, "railo.core");

		// Activator
		str = attrs.getValue("Bundle-Activator");
		if(!Util.isEmpty(str)) factory.setActivator(str);
		
		// Description
		str = attrs.getValue("Bundle-Description");
		if(!Util.isEmpty(str)) factory.setDescription(str);
		
		// Export-Package
		str = attrs.getValue("Export-Package");
		if(!Util.isEmpty(str)) factory.addExportPackage(str);

		// Import-Package
		str = attrs.getValue("Import-Package");
		if(!Util.isEmpty(str)) factory.addImportPackage(str);
		
		// DynamicImport-Package
		str = attrs.getValue("DynamicImport-Package");
		if(!Util.isEmpty(str)) factory.addDynamicImportPackage(str);
		
		
		factory.setBundleVersion(rcv);
		
		
		factory.addJar(rc);
		
		
		
		// get jars needed for that core
		String cp = attrs.getValue("Class-Path");
		if(Util.isEmpty(cp)) throw new IOException("railo core ["+rc+"] is invalid, no Class-Path defintion found in the META-INF/MANIFEST.MF File");
		
		// add jars to bundle
		StringTokenizer st=new StringTokenizer(cp,",");
		String line,jarName,jarVersion,jarNameNoExt;
		int index;
		File jar,jarBundle;
		while (st.hasMoreTokens()) {
			line=st.nextToken().trim();
			if(Util.isEmpty(line))continue;
			index=line.lastIndexOf(';');
			if(index==-1) {
				jarName=line.trim();
				jarVersion="0";
			}
			else {
				jarName=line.substring(0,index).trim();
				jarVersion=line.substring(index+1).trim();
			}
			jarNameNoExt=jarName.substring(0,jarName.length()-4); // remove .jar
			
			jar=new File(jarDirectory,jarName);
			jarBundle=new File("/Users/mic/Tmp2/",jarNameNoExt+"-"+toVarname(jarVersion)+".bundle"); 
			if(!jar.isFile()) {
				throw new BundleException("Missing jar "+jar+" ("+jarVersion+")"); // MUST try to download jar
			}
			factory.addJar(jar);
			
			BundleBuilderFactory bbf=new BundleBuilderFactory(jarNameNoExt, null);
			bbf.setBundleVersion(jarVersion);
			bbf.addJar(jar);
			bbf.addExportPackage("*");
			bbf.build(jarBundle);
			
	     }
		
		factory.build(bundle);
		return load(bc,bundle);
	}
	
	private static String toVarname(String str) {
		str=str.replace('.', '-');
		return str;
	}

	private static Bundle load(BundleContext bc,File bundle) throws IOException, BundleException {
        return BundleUtil.addBundle(bc, bundle, true);
	}
	
	/*public static void main(String[] args) throws BundleException, IOException, ClassNotFoundException {
		Felix felix = OSGiUtil.loadFelix();
		File file=new File("/Users/mic/Tmp/4020000.bundle");
    	Bundle b = load(felix.getBundleContext(),file);
    	System.out.println(b.loadClass("railo.runtime.engine.CFMLEngineImpl"));
    	
	}*/
}
