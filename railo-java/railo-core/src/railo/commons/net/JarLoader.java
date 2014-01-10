package railo.commons.net;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import railo.commons.io.IOUtil;
import railo.commons.io.res.Resource;
import railo.commons.io.res.util.ResourceClassLoader;
import railo.commons.io.res.util.ResourceUtil;
import railo.commons.lang.SystemOut;
import railo.loader.TP;
import railo.loader.engine.CFMLEngineFactory;
import railo.runtime.Info;
import railo.runtime.config.Config;
import railo.runtime.config.ConfigWeb;

public class JarLoader {

	public static final short WHEN_EXISTS_UPDATE=1;
	public static final short WHEN_EXISTS_RETURN_JAR=2;
	public static final short WHEN_EXISTS_THROW_EXP=4;
	public static final short WHEN_EXISTS_RETURN_NULL=8;
	
	


	/**
	 * this loads the given jars from update provider, copy it to lib directory (where railo.jar is located) and load them to a ClassLoader 
	 * @param pc
	 * @param jars jars names to Load
	 * @return Classloader with loaded jars for temporary use, after restart the engine this jars are loaded by the servlet engine
	 * @throws IOException
	 */
	public static ClassLoader loadJars(Config config, String[] jars,ClassLoader parent) throws IOException {
		return new ResourceClassLoader(download(config, jars),parent);
	}
	

	public static Resource[] download(Config config, String[] jars) throws IOException {
		List<Resource> list=new ArrayList<Resource>();
		Resource jar;
		lastCheck=-1;
		for(int i=0;i<jars.length;i++){
			jar=download(config, jars[i], WHEN_EXISTS_UPDATE);
			if(jar!=null) list.add(jar);
		}
		return list.toArray(new Resource[list.size()]);
	}
	
	
	private static Resource download(Config config,String jarName, short whenExists) throws IOException {
    	// some variables nned later
		PrintWriter out = config.getOutWriter();
		
		URL dataUrl=toURL(config,jarName);
        
		// destination file
		ClassLoader mainClassLoader = new TP().getClass().getClassLoader();
		
		Resource lib = config.getResource(CFMLEngineFactory.getClassLoaderRoot(mainClassLoader).getCanonicalPath());
			
		Resource jar=lib.getRealResource(jarName);
		SystemOut.printDate(out,"Check for jar at "+dataUrl);
        if(jar.exists()){
			if(whenExists==WHEN_EXISTS_RETURN_JAR) return jar;
			else if(whenExists==WHEN_EXISTS_RETURN_NULL) return null;
			else if(whenExists==WHEN_EXISTS_UPDATE) {
				// compare local and remote
				long localLen=jar.length();
				long remoteLengh=HTTPUtil.length(dataUrl);
				// only update when size change more than 10
				if(localLen==remoteLengh){
					SystemOut.printDate(out,"jar "+jar+" is up to date");
					return jar;
				}
				if(!jar.delete()) throw new IOException("cannot update jar ["+jar+"], jar is locked or write protected, stop the servlet engine and delete this jar manually."); 
			}
			else throw new IOException("jar ["+jar+"] exists already"); 
		}
		
		
        //long len=HTTPUtil.length();
        InputStream is = (InputStream)dataUrl.getContent();
        // copy input stream to lib directory
        IOUtil.copy(is, jar,true);
        
        SystemOut.printDate(out,"created/updated jar  "+jar);
        
        return jar;
    }

	private static URL toURL(Config config, String jarName) throws MalformedURLException {
		URL hostUrl=config.getUpdateLocation();
        if(hostUrl==null)hostUrl=new URL("http://www.getrailo.org");
        return new URL(hostUrl,"/railo/remote/jars/"+(Info.getMajorVersion()+"."+Info.getMinorVersion())+"/"+jarName);
	}


	public static boolean exists(ConfigWeb config,String[] jarNames) {
		for(int i=0;i<jarNames.length;i++){
			if(!exists(config, jarNames[i])) return false;
		}
		return true;
	}
	
	/**
	 * check if one of given jar has changed or not exist
	 * @param config
	 * @param jarNames
	 * @return
	 */
	private static boolean changed=false;
    private static long lastCheck=-1;
    public static boolean changed(ConfigWeb config,String[] jarNames) {
		if((lastCheck+300000)<System.currentTimeMillis()) {
			changed=false;
        	for(int i=0;i<jarNames.length;i++){
				if(changed(config, jarNames[i])) {
					changed=true;
					break;
				}
			}
			lastCheck=System.currentTimeMillis();
    	}
        return changed;
	}
	
	private static boolean exists(ConfigWeb config,String jarName) {
		Resource res = _toResource(config, jarName);
    	if(res==null) return false;
    	return res.exists();
    }
	
	private static boolean changed(ConfigWeb config,String jarName) {
    	Resource res = _toResource(config, jarName);
    	if(res==null) {
    		return true;
    	}
    	
    	try {
			URL dataUrl = toURL(config,jarName);
			boolean changed=res.length()!=HTTPUtil.length(dataUrl);
			
			return changed;
		} catch (IOException e) {
			return false;
		}
    }
	
	private static Resource _toResource(ConfigWeb config,String jarName) {
    	// destination file
		ClassLoader mainClassLoader = new TP().getClass().getClassLoader();
		try {
			Resource lib = ResourceUtil.toResourceNotExisting(config,CFMLEngineFactory.getClassLoaderRoot(mainClassLoader).getCanonicalPath());
			return lib.getRealResource(jarName);
		} catch (IOException e) {
			return null;
		}
    }
}
