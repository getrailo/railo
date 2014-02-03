package railo.loader.osgi;

import java.io.File;
import java.io.IOException;
import java.util.StringTokenizer;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

import org.apache.felix.framework.Felix;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;

import railo.loader.osgi.factory.BundleBuilderFactory;
import railo.loader.osgi.factory.BundleBuilderFactoryException;
import railo.loader.util.Util;

public class BundleLoader {
	
	/**
	 * build (if necessary) a bundle and load it
	 * @param bundleDir directory where bundles are located
	 * @param rc Railo Core File
	 * @throws BundleException 
	 * @throws IOException 
	 * @throws BundleBuilderFactoryException 
	 */
	public static Bundle buildAndLoad(BundleContext bc,File bundleDir,File jarDirectory, File rc) throws IOException, BundleException, BundleBuilderFactoryException {
		
		// org.osgi.framework.bootdelegation
		// get version from Manifest if necessary
		//if(version<=0) { TODO can we cache this step
			JarFile jf = new JarFile(rc);// TODO this should work in any case, but we should still improve this code
			Manifest mani =  jf.getManifest();
			if(mani==null) throw new IOException("railo core ["+rc+"] is invalid, no core version is defined in the META-INF/MANIFEST.MF File");
			Attributes attrs = mani.getMainAttributes();
			String rcv = attrs.getValue("railo-core-version");
			if(Util.isEmpty(rcv)) throw new IOException("railo core ["+rc+"] is invalid, no core version is defined in the META-INF/MANIFEST.MF File");
			int version = Util.toInVersion(rcv);
		//}
		
		// is there already a bundle for this version?
		File bundle = new File(bundleDir,version+".bundle");
		if(bundle.isFile()) return load(bc,bundle);
		
		BundleBuilderFactory factory=new BundleBuilderFactory("Railo Core ", "railo.core");
		factory.setVersion(rcv);
		//factory.addImportPackage("railo.loader.engine");
		//factory.addExportPackage("railo.runtime.loader.*");
		//factory.addExportPackage("railo.*");
		factory.addClassPath(".");
		factory.setActivator("railo.runtime.engine.CFMLEngineActivator	");
		
		
		factory.addJar(rc);
		
		
		
		// get jars needed for that core
		String cp = attrs.getValue("Class-Path");
		if(Util.isEmpty(cp)) throw new IOException("railo core ["+rc+"] is invalid, no Class-Path defintion found in the META-INF/MANIFEST.MF File");
		
		// add jars to bundle
		StringTokenizer st=new StringTokenizer(cp,",");
		String line,jarName,jarVersion;
		int index;
		File jar;
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
			jar=new File(jarDirectory,jarName);
			if(!jar.isFile()) {
				throw new BundleException("Missing jar "+jar+" ("+jarVersion+")"); // MUST try to download jar
			}
			factory.addJar(jar);
	     }
		
		factory.build(bundle);
		return load(bc,bundle);
	}
	
	private static Bundle load(BundleContext bc,File bundle) throws IOException, BundleException {
        return BundleUtil.addBundle(bc, bundle, true);
	}
	
	public static void main(String[] args) throws BundleException, IOException, ClassNotFoundException {
		Felix felix = OSGiUtil.loadFelix();
		File file=new File("/Users/mic/Tmp/4020000.bundle");
    	Bundle b = load(felix.getBundleContext(),file);
    	System.out.println(b.loadClass("railo.runtime.engine.CFMLEngineImpl"));
    	
	}
}
