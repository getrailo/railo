package railo.loader.engine;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

import org.apache.felix.framework.Felix;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleException;
import org.osgi.framework.Constants;

import railo.Version;
import railo.commons.io.log.Log;
import railo.loader.TP;
import railo.loader.osgi.BundleLoader;
import railo.loader.osgi.BundleUtil;
import railo.loader.util.ExtensionFilter;
import railo.loader.util.Util;

import com.intergral.fusiondebug.server.FDControllerFactory;

/**
 * Factory to load CFML Engine
 */
public class CFMLEngineFactory extends CFMLEngineFactorySupport {
	
	 // set to false to disable patch loading, for example in major alpha releases
    private static final boolean PATCH_ENABLED = true;
    
    private Felix felix;
	private Bundle bundle;
    private CFMLEngine engine;
    
    private static CFMLEngineFactory factory;
    private static File railoServerRoot;
    private static CFMLEngineWrapper engineListener;
    private ClassLoader mainClassLoader=new TP().getClass().getClassLoader();
    private int version;
    private List<EngineChangeListener> listeners=new ArrayList<EngineChangeListener>();
    private File resourceRoot;

	private PrintWriter out;

    
    
    /**
     * Constructor of the class
     */
    protected CFMLEngineFactory(){
    }

    /**
     * returns instance of this factory (singelton-> always the same instance)
     * do auto update when changes occur
     * @param config 
     * @return Singelton Instance of the Factory
     * @throws ServletException 
     */
    public static CFMLEngine getInstance(ServletConfig config) throws ServletException {
        
        if(engineListener!=null) {
        	if(factory==null) factory=engineListener.getCFMLEngineFactory();
        	return engineListener;
        }
        
        if(factory==null) factory=new CFMLEngineFactory();
        
        
        // read init param from config
        factory.setInitParam(config);
        
        CFMLEngine engine = factory.getEngine();
        engine.addServletConfig(config);
        engineListener = new CFMLEngineWrapper(engine);
        
        // add listener for update
        factory.addListener(engineListener);
        return engineListener;
    }

    /**
     * returns instance of this factory (singelton-> always the same instance)
     * do auto update when changes occur
     * @return Singelton Instance of the Factory
     * @throws RuntimeException 
     */
    public static CFMLEngine getInstance() throws RuntimeException {
        if(engineListener!=null) return engineListener;
        throw new RuntimeException("engine is not initalized, you must first call getInstance(ServletConfig)");
    }

    /**
     * used only for internal usage
     * @param engine
     * @throws RuntimeException
     */
    public static void registerInstance(CFMLEngine engine) throws RuntimeException {
    	if(factory==null) factory=engine.getCFMLEngineFactory();
    	
    	// first update existing listener
    	if(engineListener!=null) {
    		if(engineListener.equalTo(engine, true)) return;
    		engineListener.onUpdate(engine);// perhaps this is still refrenced in the code, because of that we update it
    		factory.removeListener(engineListener);
    	}
    	
    	// now register this
    	if(engine instanceof CFMLEngineWrapper) 
    		engineListener=(CFMLEngineWrapper) engine;
    	else 
    		engineListener = new CFMLEngineWrapper(engine);
    	
    	factory.addListener(engineListener);	
    }
    
    
    /**
     * returns instance of this factory (singelton-> always the same instance)
     * @param config
     * @param listener 
     * @return Singelton Instance of the Factory
     * @throws ServletException 
     */
    public static CFMLEngine getInstance(ServletConfig config, EngineChangeListener listener) throws ServletException {
        getInstance(config);
        
        // add listener for update
        factory.addListener(listener);
        
        // read init param from config
        factory.setInitParam(config);
        
        CFMLEngine e = factory.getEngine();
        e.addServletConfig(config);
        
        // make the FDController visible for the FDClient
        FDControllerFactory.makeVisible();
        
        return e;
    }
    
    void setInitParam(ServletConfig config) {
        if(railoServerRoot!=null) return;
        
        String initParam=config.getInitParameter("railo-server-directory");
        if(Util.isEmpty(initParam))initParam=config.getInitParameter("railo-server-root");
        if(Util.isEmpty(initParam))initParam=config.getInitParameter("railo-server-dir");
        if(Util.isEmpty(initParam))initParam=config.getInitParameter("railo-server");
        initParam=parsePlaceHolder(removeQuotes(initParam,true));
        
        try {
            if(!Util.isEmpty(initParam)) {
                File root=new File(initParam);
                if(!root.exists()) {
                    if(root.mkdirs()) {
                        railoServerRoot=root.getCanonicalFile();
                        return;
                    }
                }
                else if(root.canWrite()) {
                    railoServerRoot=root.getCanonicalFile();
                    return;
                }
            }
        }
        catch(IOException ioe){}
    }
    

	/**
     * adds a listener to the factory that will be informed when a new engine will be loaded.
     * @param listener
     */
    private void addListener(EngineChangeListener listener) {
       if(!listeners.contains(listener)) {
           listeners.add(listener);
       }
    }
    
    private void removeListener(EngineChangeListener listener) {
    	listeners.remove(listener);
     }

    /**
     * @return CFML Engine
     * @throws ServletException
     */
    private CFMLEngine getEngine() throws ServletException {
        if(engine==null)initEngine();
        return engine;
    }

    private void initEngine() throws ServletException {
        
        int coreVersion=Version.getIntVersion();
        long coreCreated=Version.getCreateTime();
        
        
        // get newest railo version as file
        File patcheDir=null;
        try {
            patcheDir = getPatchDirectory();
            log("railo-server-root:"+patcheDir.getParent());
        } 
        catch (IOException e) {
           throw new ServletException(e);
        }
        
        File[] patches=PATCH_ENABLED?patcheDir.listFiles(new ExtensionFilter(new String[]{".rc"})):null;
        File railo=null;
        if(patches!=null) {
            for(int i=0;i<patches.length;i++) {
                if(patches[i].getName().startsWith("tmp.rc")) {
                    patches[i].delete();
                }
                else if(patches[i].lastModified()<coreCreated) {
                    patches[i].delete();
                }
                else if(railo==null || isNewerThan(toInVersion(patches[i].getName(),-1),toInVersion(railo.getName(),-1))) {
                    railo=patches[i];
                }
            }
        }
        if(railo!=null && isNewerThan(coreVersion,toInVersion(railo.getName(),-1)))railo=null;
        
        // Load Railo
        //URL url=null;
        try {
            // Load core version when no patch available
            if(railo==null) {
            	tlog("Load Build in Core");
                // 
                String coreExt="rc";
                engine=getCore();
            	
                
               railo=new File(patcheDir,engine.getVersion()+"."+coreExt);
               if(PATCH_ENABLED) {
	                InputStream bis = new TP().getClass().getResourceAsStream("/core/core."+coreExt);
	                OutputStream bos=new BufferedOutputStream(new FileOutputStream(railo));
	                copy(bis,bos);
	                closeEL(bis);
	                closeEL(bos);
                }
            }
            else {

            	bundle = BundleLoader.loadBundles(this,getFelixCacheDirectory(),getJarDirectory(),railo,bundle);
            	//bundle=loadBundle(railo);
            	log("loaded bundle2:"+bundle.getSymbolicName());
            	engine=getEngine(bundle);
            	log("loaded engine2:"+engine);
            	/*try {
            	}
            	catch(EOFException e) {
            		System.err.println("Railo patch file "+railo+" is invalid, please delete it");
            		engine=getCore(getCoreExtension());
            	}*/
            }
            version=toInVersion(engine.getVersion(),-1);
            
            tlog("Loaded Railo Version "+engine.getVersion());
        }
        catch(InvocationTargetException e) {
            e.getTargetException().printStackTrace();
            throw new ServletException(e.getTargetException());
        }
        catch(Exception e) {
            e.printStackTrace();
            throw new ServletException(e);
        }
        
        //check updates
        String updateType=engine.getUpdateType();
        if(updateType==null || updateType.length()==0)updateType="manuell";
        
        if(updateType.equalsIgnoreCase("auto")) {
            new UpdateChecker(this).start();
        }
        
    }
    
    

	public Felix getFelix(File cacheRootDir, String storageClean,String bootDelegation, String parentClassLoader, int logLevel) throws BundleException {
		/* this is done before this call if(felix!=null){
			log(Log.LEVEL_INFO,"remove existing bundle");
			removeBundle(bundle);
			return felix;
		}*/
		
		log(Log.LEVEL_INFO,"load felix");
    	
		
		Map<String,Object> config = new HashMap<String,Object>();
		
		// storage clean
		if(!Util.isEmpty(storageClean))
			config.put(Constants.FRAMEWORK_STORAGE_CLEAN, storageClean);
		else
			config.put(Constants.FRAMEWORK_STORAGE_CLEAN, Constants.FRAMEWORK_STORAGE_CLEAN_ONFIRSTINIT);

		// boot delegation
		if(!Util.isEmpty(bootDelegation))
			config.put(Constants.FRAMEWORK_BOOTDELEGATION, bootDelegation);
		System.err.println(">>>"+bootDelegation);
		
		// parent classLoader
		if(!Util.isEmpty(parentClassLoader))
			config.put(Constants.FRAMEWORK_BUNDLE_PARENT,parentClassLoader);
		else
			config.put(Constants.FRAMEWORK_BUNDLE_PARENT, Constants.FRAMEWORK_BUNDLE_PARENT_FRAMEWORK);
		System.err.println(">>>>"+parentClassLoader);
		
		// felix.cache.rootdir
		if(!cacheRootDir.exists()) {
			cacheRootDir.mkdirs();
		}
		if(cacheRootDir.isDirectory()) {
			config.put("felix.cache.rootdir", cacheRootDir.getAbsolutePath());
		}
		
		//
		// felix.log.level
		config.put("felix.log.level", ""+logLevel);
		
		// TODO felix.log.logger 
		
		
		/*
		 FrameworkFactory frameworkFactory = ServiceLoader.load(
                FrameworkFactory.class).iterator().next();
Map<String, String> config = new HashMap<String, String>();
//TODO: add some config properties
Framework framework = frameworkFactory.newFramework(config);
framework.start();
		 */
		
		felix = new Felix(config);
        felix.start();
        
		return felix;
	}

	

	public static void log(int level, String msg) {
		//System.out.println(msg);
		System.err.println(msg);
	}


	private CFMLEngine getCore() throws IOException, BundleException, ClassNotFoundException, SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException {
		File rc = new File(getTempDirectory(),"tmp_"+System.currentTimeMillis()+".rc");
		try {
			InputStream is = null;
			OutputStream os = null;
	    	try {
	    		is = new TP().getClass().getResourceAsStream("/core/core.rc");
	    		os=new FileOutputStream(rc);
	    		copy(is, os);
	    		
	    	}
	    	finally {
	    		closeEL(is);
	    		closeEL(os);
	    	}
	    	bundle = BundleLoader.loadBundles(this,getFelixCacheDirectory(),getJarDirectory(),rc,bundle);
        	log("loaded bundle3:"+bundle.getSymbolicName());
	    	engine = getEngine(bundle);
        	log("loaded engine3:"+engine);
        	return engine;
    	}
    	finally {
    		rc.delete();
    	}
	}

	/**
     * method to initalize a update of the CFML Engine.
     * checks if there is a new Version and update it whwn a new version is available
     * @param password
     * @return has updated
     * @throws IOException
     * @throws ServletException 
     */
    public boolean update(String password) throws IOException, ServletException {
        if(!engine.can(CFMLEngine.CAN_UPDATE,password))
            throw new IOException("access denied to update CFMLEngine");
        //new RunUpdate(this).start();
        return update();
    }

    /**
     * restart the cfml engine
     * @param password
     * @return has updated
     * @throws IOException 
     * @throws ServletException 
     */
    public boolean restart(String password) throws IOException, ServletException {
        if(!engine.can(CFMLEngine.CAN_RESTART_ALL,password))
            throw new IOException("access denied to restart CFMLEngine");
        
        return _restart();
    }

    /**
     * restart the cfml engine
     * @param password
     * @return has updated
     * @throws IOException 
     * @throws ServletException 
     */
    public boolean restart(String configId, String password) throws IOException, ServletException {
        if(!engine.can(CFMLEngine.CAN_RESTART_CONTEXT,password))// TODO restart single context
            throw new IOException("access denied to restart CFML Context (configId:"+configId+")");
        
        return _restart();
    }
    
    /**
     * restart the cfml engine
     * @param password
     * @return has updated
     * @throws IOException 
     * @throws ServletException 
     */
    private synchronized boolean _restart() throws ServletException {
        engine.reset();
        initEngine();
        registerInstance(engine);
        callListeners(engine);
        System.gc(); 
        System.gc();
        return true;
    }

	/**
     * updates the engine when a update is available
     * @return has updated
     * @throws IOException
     * @throws ServletException
     */
    private boolean update() throws IOException, ServletException {
    	File newRailo = downloadCore();
    	if(newRailo==null) return false;
        
        try {
        	engine.reset();
        }
        catch(Throwable t) {
        	t.printStackTrace();
        }
        

        
        String v="";
        try {
        	
        	bundle = BundleLoader.loadBundles(this,getFelixCacheDirectory(),getJarDirectory(),newRailo,bundle);
        	log("loaded bundle1:"+bundle.getSymbolicName());
            CFMLEngine e = getEngine(bundle);
        	log("loaded engine1:"+e);
            if(e==null)throw new IOException("can't load engine");
            v=e.getVersion();
            engine=e;
            version=toInVersion(v,-1);
            //e.reset();
            callListeners(e);
        }
        catch (Exception e) {
            System.gc();
            try {
                newRailo.delete();
            }
            catch(Exception ee){}
            tlog("There was a Problem with the new Version, can't install ("+e+":"+e.getMessage()+")");
            e.printStackTrace();
            return false;
        }
        
        tlog("Version ("+v+")installed");
        return true;
    }
    

    public File downloadBundle(String symbolicName,String symbolicVersion) throws IOException {
    	File jarDir=getJarDirectory();
	    File jar=new File(jarDir,symbolicName+"-"+symbolicVersion.replace('.', '-')+(".jar"));
        
    	
    	URL updateProvider = getUpdateLocation();
    	URL updateUrl=new URL(updateProvider,"/rest/update/provider/download/"+symbolicName+"/"+symbolicVersion+"/?ioid={ioid}");
        System.out.println("update-loc:"+updateUrl);
        if(jar.createNewFile()) {
            copy((InputStream)updateUrl.getContent(),new FileOutputStream(jar));
            return jar;
        }
        else {
        	throw new IOException("File ["+jar.getName()+"] already exists, won't copy new one");
        }
    }
    
    private File downloadCore() throws IOException, ServletException {
    	URL updateProvider = getUpdateLocation();
    	
    	// MUST get IOID
        URL infoUrl=new URL(updateProvider,"/rest/update/provider/update-for/"+version+"?ioid={ioid}");
        
        tlog("Check for update at "+updateProvider);
        
        String strAvailableVersion = toString((InputStream)infoUrl.getContent()).trim();
        strAvailableVersion=CFMLEngineFactory.removeQuotes(strAvailableVersion,true);
        CFMLEngineFactory.removeQuotes(strAvailableVersion,true); // not necessary but does not hurt
        
        if(strAvailableVersion.length()==0 || !isNewerThan(toInVersion(strAvailableVersion,-1),version)) {
            tlog("There is no newer Version available");
            return null;
        }

        tlog("Found a newer Version \n - current Version "+toStringVersion(version)+"\n - available Version "+strAvailableVersion);

        URL updateUrl=new URL(updateProvider,"/rest/update/provider/download/"+version+"?ioid={ioid}");
        File patchDir=getPatchDirectory();
        File newRailo=new File(patchDir,strAvailableVersion+(".rc"));
        
        if(newRailo.createNewFile()) {
            copy((InputStream)updateUrl.getContent(),new FileOutputStream(newRailo));  
        }
        else {
            tlog("File for new Version already exists, won't copy new one");
            return null;
        }
        return newRailo;
	}

	private URL getUpdateLocation() throws MalformedURLException {
		URL updateProvider=null;
		try {
			updateProvider = getEngine().getUpdateLocation();
		} 
		catch (ServletException e) {}
		
        if(updateProvider==null)updateProvider=new URL("http://www.getrailo.org");
        return updateProvider;
	}

	/**
     * method to initalize a update of the CFML Engine.
     * checks if there is a new Version and update it whwn a new version is available
     * @param password
     * @return has updated
     * @throws IOException
     * @throws ServletException 
     */
    public boolean removeUpdate(String password) throws IOException, ServletException {
        if(!engine.can(CFMLEngine.CAN_UPDATE,password))
            throw new IOException("access denied to update CFMLEngine");
        return removeUpdate();
    }
    

    /**
     * method to initalize a update of the CFML Engine.
     * checks if there is a new Version and update it whwn a new version is available
     * @param password
     * @return has updated
     * @throws IOException
     * @throws ServletException 
     */
    public boolean removeLatestUpdate(String password) throws IOException, ServletException {
        if(!engine.can(CFMLEngine.CAN_UPDATE,password))
            throw new IOException("access denied to update CFMLEngine");
        return removeLatestUpdate();
    }
    
    
    
    /**
     * updates the engine when a update is available
     * @return has updated
     * @throws IOException
     * @throws ServletException
     */
    private boolean removeUpdate() throws IOException, ServletException {
        File patchDir=getPatchDirectory();
        File[] patches=patchDir.listFiles(new ExtensionFilter(new String[]{"railo","rc","rcs"}));
        
        for(int i=0;i<patches.length;i++) {
        	if(!patches[i].delete())patches[i].deleteOnExit();
        }
        _restart();
        return true;
    }
    

    private boolean removeLatestUpdate() throws IOException, ServletException {
        File patchDir=getPatchDirectory();
        File[] patches=patchDir.listFiles(new ExtensionFilter(new String[]{".rc"}));
        File patch=null;
        for(int i=0;i<patches.length;i++) {
        	 if(patch==null || isNewerThan(toInVersion(patches[i].getName(),-1),toInVersion(patch.getName(),-1))) {
                 patch=patches[i];
             }
        }
    	if(patch!=null && !patch.delete())patch.deleteOnExit();
        
        _restart();
        return true;
    }
    

	public String[] getInstalledPatches() throws ServletException, IOException {
		File patchDir=getPatchDirectory();
        File[] patches=patchDir.listFiles(new ExtensionFilter(new String[]{".rc"}));
        
        List<String> list=new ArrayList<String>();
        String name;
        int extLen="rc".length()+1;
        for(int i=0;i<patches.length;i++) {
        	name=patches[i].getName();
        	name=name.substring(0, name.length()-extLen);
        	 list.add(name);
        }
        String[] arr = list.toArray(new String[list.size()]);
    	Arrays.sort(arr);
        return arr;
	}
    

    /**
     * call all registred listener for update of the engine
     * @param engine
     */
    private void callListeners(CFMLEngine engine) {
        Iterator<EngineChangeListener> it = listeners.iterator();
        while(it.hasNext()) {
            it.next().onUpdate(engine);
        }
    }
    

    private File getPatchDirectory() throws IOException {
        File pd = new File(getResourceRoot(),"patches");
        if(!pd.exists())pd.mkdirs();
        return pd;
    }
    
    private File getJarDirectory() throws IOException {
        File bd = new File(getResourceRoot(),"jars");
        if(!bd.exists())bd.mkdirs();
        return bd;
    }
    
    private File getFelixCacheDirectory() throws IOException {
    	return getResourceRoot();
        //File bd = new File(getResourceRoot(),"felix-cache");
        //if(!bd.exists())bd.mkdirs();
        //return bd;
    }

    /**
     * return directory to railo resource root
     * @return railo root directory
     * @throws IOException
     */
    public File getResourceRoot() throws IOException {
        if(resourceRoot==null) {
            resourceRoot=new File(getRuningContextRoot(),"railo-server");
            if(!resourceRoot.exists()) resourceRoot.mkdirs();
        }
        return resourceRoot;
    }
    
    /**
     * @return return running context root
     * @throws IOException 
     * @throws IOException 
     */
    private File getRuningContextRoot() throws IOException {
        
        if(railoServerRoot!=null) {
            return railoServerRoot;
        }
        File dir=getClassLoaderRoot(mainClassLoader);
        dir.mkdirs();
        if(dir.exists() && dir.isDirectory()) return dir;
        
            
           
        throw new IOException("can't create/write to directory ["+dir+"], set \"init-param\" \"railo-server-directory\" with path to writable directory");
    }
    /**
     * returns the path where the classloader is located
     * @param cl ClassLoader
     * @return file of the classloader root
     */
    public static File getClassLoaderRoot(ClassLoader cl) {
        String path="railo/loader/engine/CFMLEngine.class";
        URL res = cl.getResource(path);
       
        // get file and remove all after !
        String strFile=null;
		try {
			strFile = URLDecoder.decode(res.getFile().trim(),"iso-8859-1");
		} catch (UnsupportedEncodingException e) {
			
		}
        int index=strFile.indexOf('!');
        if(index!=-1)strFile=strFile.substring(0,index);
        
        // remove path at the end
        index=strFile.lastIndexOf(path);
        if(index!=-1)strFile=strFile.substring(0,index);
        
        // remove "file:" at start and railo.jar at the end
        if(strFile.startsWith("file:"))strFile=strFile.substring(5);
        if(strFile.endsWith("railo.jar")) strFile=strFile.substring(0,strFile.length()-9);
		
        File file=new File(strFile);
        if(file.isFile())file=file.getParentFile();
        
        return file;
    }

    /**
     * check left value against right value
     * @param left
     * @param right
     * @return returns if right is newer than left
     */
    private boolean isNewerThan(int left, int right) {
        return left>right;
    }
  
    /**
     * Load CFMl Engine Implementation (railo.runtime.engine.CFMLEngineImpl) from a Classloader
     * @param bundle
     * @return
     * @throws ClassNotFoundException
     * @throws SecurityException
     * @throws NoSuchMethodException
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    private CFMLEngine getEngine(Bundle bundle) throws ClassNotFoundException, SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException {
        log("state:"+BundleUtil.bundleState(bundle.getState(),""));
    	//bundle.getBundleContext().getServiceReference(CFMLEngine.class.getName());
        log(Constants.FRAMEWORK_BOOTDELEGATION+":"+bundle.getBundleContext().getProperty(Constants.FRAMEWORK_BOOTDELEGATION));
        log("felix.cache.rootdir:"+bundle.getBundleContext().getProperty("felix.cache.rootdir"));
    	
        
        log(bundle.loadClass(TP.class.getName()).getClassLoader().toString());
    	Class<?> clazz = bundle.loadClass("railo.runtime.engine.CFMLEngineImpl");
        log("class:"+clazz.getName());
        Method m = clazz.getMethod("getInstance",new Class[]{CFMLEngineFactory.class,Bundle.class});
        return (CFMLEngine) m.invoke(null,new Object[]{this,bundle});
        
    }

    /**
     * log info to output
     * @param obj Object to output
     */
    public void tlog(Object obj) {
    	log(new Date()+ " "+obj);
    }
    
    /**
     * log info to output
     * @param obj Object to output
     */
    public void log(Object obj) {
    	if(out==null){
    		boolean isCLI=false;
    		String str=System.getProperty("railo.cli.call");
    		if(!Util.isEmpty(str, true)) {
    			str=str.trim();
    			isCLI="true".equalsIgnoreCase(str) || "yes".equalsIgnoreCase(str);
    			
    		}
    		
    		if(isCLI) {
    			try{
    				File dir = new File(getResourceRoot(),"logs");
    				dir.mkdirs();
    				File file = new File(dir,"out");
        			
    			file.createNewFile();
    			out=new PrintWriter(file);
    			}
    			catch(Throwable t){t.printStackTrace();}
    		}
    		if(out==null)out=new PrintWriter(System.out);
    	}
    	out.write(""+obj+"\n");   
    	out.flush();
    }
    
    private class UpdateChecker extends Thread {
        private CFMLEngineFactory factory;

        private UpdateChecker(CFMLEngineFactory factory) {
            this.factory=factory;
        }
        
        public void run() {
            long time=10000;
            while(true) {
                try {
                    sleep(time);
                    time=1000*60*60*24;
                    factory.update();
                    
                } catch (Exception e) {
                    
                }
            }
        }
    }

}
