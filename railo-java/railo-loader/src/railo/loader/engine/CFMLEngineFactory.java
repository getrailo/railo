package railo.loader.engine;

import java.io.BufferedOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

import railo.Version;
import railo.loader.TP;
import railo.loader.classloader.RailoClassLoader;
import railo.loader.util.ExtensionFilter;
import railo.loader.util.Util;

import com.intergral.fusiondebug.server.FDControllerFactory;

/**
 * Factory to load CFML Engine
 */
public class CFMLEngineFactory {
	
	 // set to false to disable patch loading, for example in major alpha releases
    private static final boolean PATCH_ENABLED = true;
    
    private static boolean loadRailoFromClassPath = false;
	private static CFMLEngineFactory factory;
    private static File railoServerRoot;
    private static CFMLEngineWrapper engineListener;
    private CFMLEngine engine;
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
        initParam=Util.parsePlaceHolder(Util.removeQuotes(initParam,true));
        
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
        // if this is true, the railo patch file should be renamed to a jar file and placed on the classpath in order for it to work.
        String loadRailoFromClasspathProperty = config.getInitParameter("railo-load-from-classpath");
        if ( !Util.isEmpty( loadRailoFromClasspathProperty ) ) {
        	loadRailoFromClassPath = loadRailoFromClasspathProperty.toLowerCase().equals("true");
        }
        
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
    	
        if ( loadRailoFromClassPath ) {
	        try {
				engine = getEngine( mainClassLoader );
			} catch (Exception e) {
				e.printStackTrace();
			}
        } else {
        	_initPatchEngine();
        }
    } 
    
    private void _initPatchEngine() throws ServletException {
        
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
        
        File[] patches=PATCH_ENABLED?patcheDir.listFiles(new ExtensionFilter(new String[]{"."+getCoreExtension()})):null;
        File railo=null;
        if(patches!=null) {
            for(int i=0;i<patches.length;i++) {
                if(patches[i].getName().startsWith("tmp.rc")) {
                    patches[i].delete();
                }
                else if(patches[i].lastModified()<coreCreated) {
                    patches[i].delete();
                }
                else if(railo==null || isNewerThan(Util.toInVersion(patches[i].getName()),Util.toInVersion(railo.getName()))) {
                    railo=patches[i];
                }
            }
        }
        if(railo!=null && isNewerThan(coreVersion,Util.toInVersion(railo.getName())))railo=null;
        
        // Load Railo
        //URL url=null;
        try {
            // Load core version when no patch available
            if(railo==null) {
            	tlog("Load Build in Core");
                // 
                String coreExt=getCoreExtension();
                engine=getCore(coreExt);
            	
                
                railo=new File(patcheDir,engine.getVersion()+"."+coreExt);
               if(PATCH_ENABLED) {
	                InputStream bis = new TP().getClass().getResourceAsStream("/core/core."+coreExt);
	                OutputStream bos=new BufferedOutputStream(new FileOutputStream(railo));
	                Util.copy(bis,bos);
	                Util.closeEL(bis,bos);
                }
            }
            else {
            	try {
            		engine=getEngine(new RailoClassLoader(railo,mainClassLoader));
            	}
            	catch(EOFException e) {
            		System.err.println("Railo patch file "+railo+" is invalid, please delete it");
            		engine=getCore(getCoreExtension());
            	}
            }
            version=Util.toInVersion(engine.getVersion());
            
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
    

    private String getCoreExtension() throws ServletException {
    	URL res = new TP().getClass().getResource("/core/core.rcs");
        if(res!=null) return "rcs";
        
        res = new TP().getClass().getResource("/core/core.rc");
        if(res!=null) return "rc";
        
        throw new ServletException("missing core file");
	}

	private CFMLEngine getCore(String ext) throws SecurityException, IllegalArgumentException, ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, IOException {
    	InputStream is = null;
    	try {
    		is = new TP().getClass().getResourceAsStream("/core/core."+ext);
    		RailoClassLoader classLoader=new RailoClassLoader(is,mainClassLoader,ext.equalsIgnoreCase("rcs"));
    		return getEngine(classLoader);
    	}
    	finally {
    		Util.closeEL(is);
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
    	
        URL hostUrl=getEngine().getUpdateLocation();
        if(hostUrl==null)hostUrl=new URL("http://www.getrailo.org");
        URL infoUrl=new URL(hostUrl,"/railo/remote/version/info.cfm?ext="+getCoreExtension()+"&version="+version);// FUTURE replace with Info.cfc or better move the functionality to core if possible. something like engine.getUpdater a class provided by the core and defined (interface) by the loader.
        
        tlog("Check for update at "+hostUrl);
        
        String availableVersion = Util.toString((InputStream)infoUrl.getContent()).trim();
        
        if(availableVersion.length()!=9) throw new IOException("can't get update info from ["+infoUrl+"]");
        if(!isNewerThan(Util.toInVersion(availableVersion),version)) {
            tlog("There is no newer Version available");
            return false;
        }
        
        tlog("Found a newer Version \n - current Version "+Util.toStringVersion(version)+"\n - available Version "+availableVersion);
        
        URL updateUrl=new URL(hostUrl,"/railo/remote/version/update.cfm?ext="+getCoreExtension()+"&version="+availableVersion);
        File patchDir=getPatchDirectory();
        File newRailo=new File(patchDir,availableVersion+("."+getCoreExtension()));//isSecure?".rcs":".rc"
        
        if(newRailo.createNewFile()) {
            Util.copy((InputStream)updateUrl.getContent(),new FileOutputStream(newRailo));  
        }
        else {
            tlog("File for new Version already exists, won't copy new one");
            return false;
        }
        try {
        engine.reset();
        }
        catch(Throwable t) {
        	t.printStackTrace();
        }
        
        // Test new railo version valid
        //FileClassLoader classLoader=new FileClassLoader(newRailo,mainClassLoader);
        RailoClassLoader classLoader=new RailoClassLoader(newRailo,mainClassLoader);
        //URLClassLoader classLoader=new URLClassLoader(new URL[]{newRailo.toURL()},mainClassLoader);
        String v="";
        try {
            CFMLEngine e = getEngine(classLoader);
            if(e==null)throw new IOException("can't load engine");
            v=e.getVersion();
            engine=e;
            version=Util.toInVersion(v);
            //e.reset();
            callListeners(e);
        }
        catch (Exception e) {
            classLoader=null;
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
        File[] patches=patchDir.listFiles(new ExtensionFilter(new String[]{"."+getCoreExtension()}));
        File patch=null;
        for(int i=0;i<patches.length;i++) {
        	 if(patch==null || isNewerThan(Util.toInVersion(patches[i].getName()),Util.toInVersion(patch.getName()))) {
                 patch=patches[i];
             }
        }
    	if(patch!=null && !patch.delete())patch.deleteOnExit();
        
        _restart();
        return true;
    }
    

	public String[] getInstalledPatches() throws ServletException, IOException {
		File patchDir=getPatchDirectory();
        File[] patches=patchDir.listFiles(new ExtensionFilter(new String[]{"."+getCoreExtension()}));
        
        List<String> list=new ArrayList<String>();
        String name;
        int extLen=getCoreExtension().length()+1;
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
     * @param classLoader
     * @return loaded CFML Engine
     * @throws ClassNotFoundException 
     * @throws NoSuchMethodException 
     * @throws SecurityException 
     * @throws InvocationTargetException 
     * @throws IllegalAccessException 
     * @throws IllegalArgumentException 
     */
    private CFMLEngine getEngine(ClassLoader classLoader) throws ClassNotFoundException, SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException {
        Class clazz=classLoader.loadClass("railo.runtime.engine.CFMLEngineImpl");
        Method m = clazz.getMethod("getInstance",new Class[]{CFMLEngineFactory.class});
        return (CFMLEngine) m.invoke(null,new Object[]{this});
        
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
