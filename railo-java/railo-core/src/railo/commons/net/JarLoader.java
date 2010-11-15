package railo.commons.net;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import railo.commons.io.IOUtil;
import railo.commons.io.res.Resource;
import railo.commons.io.res.util.ResourceClassLoader;
import railo.commons.io.res.util.ResourceUtil;
import railo.commons.lang.SystemOut;
import railo.loader.TP;
import railo.loader.engine.CFMLEngine;
import railo.loader.engine.CFMLEngineFactory;
import railo.runtime.PageContext;
import railo.runtime.config.ConfigWeb;
import railo.runtime.config.ConfigWebImpl;

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
	public static ClassLoader loadJars(PageContext pc, String[] jars,ClassLoader parent) throws IOException {
		return new ResourceClassLoader(download(pc, jars),parent);
	}
	

	public static Resource[] download(PageContext pc, String[] jars) throws IOException {
		List<Resource> list=new ArrayList<Resource>();
		Resource jar;
		for(int i=0;i<jars.length;i++){
			jar=download(pc, jars[i], WHEN_EXISTS_RETURN_NULL);
			if(jar!=null) list.add(jar);
		}
		return list.toArray(new Resource[list.size()]);
	}
	
	
	private static Resource download(PageContext pc,String jarName, short whenExists) throws IOException {
    	// some variables nned later
		ConfigWebImpl config=(ConfigWebImpl) pc.getConfig();
    	CFMLEngine engine=config.getCFMLEngineImpl();
		PrintWriter out = pc.getConfig().getOutWriter();
        
		// destination file
		ClassLoader mainClassLoader = new TP().getClass().getClassLoader();
		Resource lib = ResourceUtil.toResourceNotExisting(pc,CFMLEngineFactory.getClassLoaderRoot(mainClassLoader).getCanonicalPath(),false);
		
		Resource jar=lib.getRealResource(jarName);
		if(jar.exists()){
			if(whenExists==WHEN_EXISTS_RETURN_JAR) return jar;
			else if(whenExists==WHEN_EXISTS_RETURN_NULL) return null;
			else if(whenExists==WHEN_EXISTS_UPDATE) {
				if(!jar.delete()) throw new IOException("cannot update jar ["+jar+"], jar is locked or write protected"); 
			}
			else throw new IOException("jar ["+jar+"] already exists"); 
		}
		
		URL hostUrl=engine.getUpdateLocation();
        if(hostUrl==null)hostUrl=new URL("http://www.getrailo.org");
        URL dataUrl=new URL(hostUrl,"/railo/remote/jars/"+jarName);
        
        SystemOut.printDate(out,"Check for jar at "+hostUrl);
        
        
        InputStream is = (InputStream)dataUrl.getContent();
        // copy input stream to lib directory
        IOUtil.copy(is, jar,true);
        
        SystemOut.printDate(out,"created jar  "+jar);
        
        return jar;
    }
	
	public static boolean exists(ConfigWeb config,String[] jarNames) {
		for(int i=0;i<jarNames.length;i++){
			if(!exists(config, jarNames[i])) return false;
		}
		return true;
		
	}
	
	private static boolean exists(ConfigWeb config,String jarName) {
    	// some variables nned later
		ConfigWebImpl configImpl=(ConfigWebImpl) config;
    	CFMLEngine engine=configImpl.getCFMLEngineImpl();
		PrintWriter out = config.getOutWriter();
        
		// destination file
		ClassLoader mainClassLoader = new TP().getClass().getClassLoader();
		try {
			Resource lib = ResourceUtil.toResourceNotExisting(config,CFMLEngineFactory.getClassLoaderRoot(mainClassLoader).getCanonicalPath());
			Resource jar=lib.getRealResource(jarName);
			return jar.exists();
		} catch (IOException e) {
			return false;
		}
		
		
    }
}
