package railo.loader.osgi;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.felix.framework.Felix;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.Constants;

import railo.commons.io.IOUtil;
import railo.commons.io.res.Resource;
import railo.loader.engine.CFMLEngineFactory;
import railo.loader.util.Util;
import railo.runtime.util.Cast;

public class BundleLoader {
	
	/**
	 * build (if necessary) a bundle and load it
	 * @param bundleDir directory where bundles are located
	 * @param rc Railo Core File
	 * @param version version of the railo core file
	 * @throws BundleException 
	 * @throws IOException 
	 */
	public static Bundle buildAndLoad(BundleContext bc,File bundleDir, File rc, int version) throws IOException, BundleException {
		
		// get version from Manifest if necessary
		Manifest mani=null;
		if(version<=0) {
			mani = getManifest(rc);
			if(mani==null) throw new IOException("railo core ["+rc+"] is invalid, no core version is defined in the META-INF/MANIFEST.MF File");
			Attributes attrs = mani.getMainAttributes();
			String rcv = attrs.getValue("railo-core-version");
			if(Util.isEmpty(rcv)) throw new IOException("railo core ["+rc+"] is invalid, no core version is defined in the META-INF/MANIFEST.MF File");
			version=Util.toInVersion(rcv);
		}
		
		// is there already a bundle for this version?
		File bundle = new File(bundleDir,version+".bundle");
		if(bundle.isFile()) return load(bc,bundle);
		
		// get core meta data
		if(mani==null)mani = getManifest(rc);
		Attributes attrs = mani.getMainAttributes();
		
		// get jars needed for that core
		String cp = attrs.getValue("Class-Path");
		if(Util.isEmpty(cp)) throw new IOException("railo core ["+rc+"] is invalid, no Class-Path defintion found in the META-INF/MANIFEST.MF File");
		
		
		
		// first we have to extract meta information of the 
		return null;
	}

	private static Manifest getManifest(File rc) {
		return new JarFile(rc).getManifest();
	}
	
	private static Bundle load(BundleContext bc,File bundle) throws IOException, BundleException {
        return BundleUtil.addBundle(bc, bundle, true);
	}
}
