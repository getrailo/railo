package railo.runtime.osgi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.jar.Attributes;
import java.util.jar.Manifest;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.Version;
import org.osgi.framework.launch.FrameworkFactory;

import railo.commons.io.IOUtil;
import railo.commons.io.log.Log;
import railo.commons.io.res.Resource;
import railo.commons.lang.StringUtil;
import railo.loader.engine.CFMLEngineFactorySupport;
import railo.runtime.op.Caster;
import railo.runtime.type.util.ListUtil;

public class OSGiUtil {
	
	/**
	 * only installs a bundle, if the bundle does not already exist, if the bundle exists the existing bundle is unloaded first.
	 * @param factory
	 * @param context
	 * @param bundle
	 * @return
	 * @throws IOException
	 * @throws BundleException
	 */
	public static Bundle installBundle(Log log,BundleContext context,Resource bundle) throws IOException, BundleException {
		//if(bundle.toString().equals("/Users/mic/Projects/Railo/webroot/WEB-INF/railo/context/railo-context.ra"))
		//	print.ds("install-bundle:"+bundle);
		if(log!=null)log.info("OSGi", "add bundle:"+bundle);
		
		Manifest m=getManifest(bundle);
		if(m==null) throw new BundleException("no Manifest found in Bundle "+bundle);
		Attributes attrs = m.getMainAttributes();
		
		String symName = Caster.toString(attrs.getValue("Bundle-SymbolicName"),null);
		if(StringUtil.isEmpty(symName)) throw new BundleException("Manifest from bundle ["+bundle+"] does not contain a Bundle-SymbolicName");
		String version = Caster.toString(attrs.getValue("Bundle-Version"),null);
		if(StringUtil.isEmpty(version)) throw new BundleException("Manifest from bundle ["+bundle+"] does not contain a Bundle-Version");
		Version v = toVersion(version);
		
		
		Bundle existing = getBundle(context,symName,v,null);
		if(existing!=null) return existing;
		
		//context.getBundle(arg0);
		
    	InputStream is = bundle.getInputStream();
		try {
			Bundle b = context.installBundle(bundle.getAbsolutePath(),is);
			//if(start)start(factory,b);
        	return b;
        }
        finally {
        	CFMLEngineFactorySupport.closeEL(is);
        }
	}
	
	private static Bundle getBundle(BundleContext context, String symbolicName, Version version, Bundle defaultValue) { 
		Bundle[] bundles = context.getBundles();
		for(int i=0;i<bundles.length;i++){
			if(bundles[i].getSymbolicName().equals(symbolicName) && version.equals(bundles[i].getVersion()))
				return bundles[i];
		}
		
		return defaultValue;
	}
	
	
	public static Version toVersion(String version, Version defaultValue) {
		String[] arr = ListUtil.listToStringArray(version, '.');
		if(arr.length<3 || arr.length>4) return defaultValue;
		
		Integer major=Caster.toInteger(arr[0],null);
		if(major==null) return defaultValue;
		
		Integer minor=Caster.toInteger(arr[1],null);
		if(minor==null) return defaultValue;
		
		Integer micro=Caster.toInteger(arr[2],null);
		if(micro==null) return defaultValue;
		
		if(arr.length==3 || StringUtil.isEmpty(arr[3],true)) 
			return new Version(major,minor,micro);
		return new Version(major,minor,micro,arr[3].trim());
	}
	
	public static Version toVersion(String version) throws BundleException {
		Version v = toVersion(version,null);
		if(v!=null) return v;
		throw new BundleException("given version ["+version+"] is invalid, a valid version is following this pattern <major-number>.<minor-number>.<micro-number>[.<qualifier>]");
	}

	private static Manifest getManifest(Resource bundle) throws IOException {
		InputStream is=null;
		Manifest mf=null;
		try{
			is=bundle.getInputStream();
			ZipInputStream zis = new ZipInputStream(is);
			
		    ZipEntry entry;
		    
		    while ((entry = zis.getNextEntry()) != null && mf==null) {
		    	if("META-INF/MANIFEST.MF".equals(entry.getName())) {
		    		mf=new Manifest(zis);
		    	}
		    	zis.closeEntry();
		    }
		}
		finally {
			IOUtil.closeEL(is);
		}
		return mf;
	}

	public static FrameworkFactory getFrameworkFactory() throws Exception
    {
        java.net.URL url = OSGiUtil.class.getClassLoader().getResource(
            "META-INF/services/org.osgi.framework.launch.FrameworkFactory");
        if (url != null)
        {
            BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
            try
            {
                for (String s = br.readLine(); s != null; s = br.readLine())
                {
                    s = s.trim();
                    // Try to load first non-empty, non-commented line.
                    if ((s.length() > 0) && (s.charAt(0) != '#'))
                    {
                        return (FrameworkFactory) Class.forName(s).newInstance();
                    }
                }
            }
            finally
            {
                if (br != null) br.close();
            }
        }

        throw new Exception("Could not find framework factory.");
    }
	
	/*public Version toVersion(String version){
		String[] arr = ListUtil.listToStringArray(version, '.');
		if(arr.length!=4) throw new BundleException("version ["+version+"] has a invalid format");
		
		
		
	}*/
}
