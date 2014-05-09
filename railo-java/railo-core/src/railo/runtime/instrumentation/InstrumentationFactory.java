/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.    
 */
package railo.runtime.instrumentation;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.instrument.Instrumentation;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.AccessController;
import java.security.PrivilegedAction;

import railo.commons.io.IOUtil;
import railo.commons.io.SystemUtil;
import railo.commons.io.log.Log;
import railo.commons.io.res.Resource;
import railo.commons.io.res.ResourcesImpl;
import railo.commons.io.res.type.file.FileResource;
import railo.commons.io.res.util.ResourceUtil;
import railo.commons.lang.ClassUtil;
import railo.loader.TP;
import railo.loader.engine.CFMLEngine;
import railo.loader.engine.CFMLEngineFactory;
import railo.runtime.config.Config;
import railo.runtime.config.ConfigWebUtil;
import railo.runtime.engine.InfoImpl;
import railo.runtime.exp.ApplicationException;
import railo.runtime.exp.PageRuntimeException;


/**
 * Factory for obtaining an {@link Instrumentation} instance.
 */
public class InstrumentationFactory {
    private static final String _name = InstrumentationFactory.class.getName();
	private static final String SEP = File.separator;
	private static final String TOOLS_VERSION = "7u25";
	private static final String AGENT_CLASS_NAME = "railo.runtime.instrumentation.ExternalAgent";
	private static Instrumentation _instr;
    

    /**
     * @param log OpenJPA log.
     * @return null if Instrumentation can not be obtained, or if any 
     * Exceptions are encountered.
     */
    public static synchronized Instrumentation getInstrumentation(final Config config) {
    	final Log log=config.getLog("application");
    	final CFMLEngine engine = ConfigWebUtil.getEngine(config);
    	Instrumentation instr=_getInstrumentation(log,config);
        
    	// agent already exist
    	if (instr!=null) return instr;
        
        

        AccessController.doPrivileged(new PrivilegedAction<Object>() {
            @Override
			public Object run() {
            	ClassLoader ccl = Thread.currentThread().getContextClassLoader();
            	Thread.currentThread().setContextClassLoader(ClassLoader.getSystemClassLoader());
                try{
	                
	                log.info("Instrumentation","looking for tools.jar");
	                JavaVendor vendor = JavaVendor.getCurrentVendor();
	                Resource toolsJar = null;
	                // When running on IBM, the attach api classes are packaged in vm.jar which is a part
	                // of the default vm classpath.
	                if (!vendor.isIBM()) {
	                    // If we can't find the tools.jar and we're not on IBM we can't load the agent. 
	                    toolsJar = findToolsJar(config,log);
	                    if (toolsJar == null) {
	                        return null;
	                    }
	                }
	                log.info("Instrumentation","tools.jar used:"+toolsJar);
	                
	                
	                Class<?> vmClass = loadVMClass(toolsJar, log, vendor);
	                log.info("Instrumentation","loaded VirtualMachine class:"+(vmClass==null?"null":vmClass.getName()));
	                if (vmClass == null) {
	                    return null;
	                }
	                String agentPath = createAgentJar(log,config).getAbsolutePath();
	                if (agentPath == null) {
	                    return null;
	                }
	                log.info("Instrumentation","load agent (path:"+agentPath+")");
	                loadAgent(log, agentPath, vmClass);
	                
                }
                catch(IOException ioe){
                	log.log(Log.LEVEL_ERROR,"Instrumentation", ioe);
                }
                finally{
                	Thread.currentThread().setContextClassLoader(ccl);
                }
                return null;
            }// end run()
        });
        // If the load(...) agent call was successful, this variable will no 
        // longer be null.
        instr=_getInstrumentation(log, config);
        if(instr==null) {
        	try{
        		Resource agentJar = createAgentJar(log,config);
        		throw new PageRuntimeException(new ApplicationException("Railo was not able to load a Agent dynamically! " +
        				"You need to load one manually by adding the following to your JVM arguments [-javaagent:"+agentJar+"]"));
        	}
        	catch(IOException ioe){ioe.printStackTrace();}
        }
        return instr;
    }

    private static Instrumentation _getInstrumentation(Log log, Config config) {
    	if(_instr!=null) return _instr;
    	
    	// try to get from different Classloaders
    	_instr=_getInstrumentation(ClassLoader.getSystemClassLoader(),log);
    	if(_instr!=null) return _instr;
    	
    	_instr=_getInstrumentation(CFMLEngineFactory.class.getClassLoader(),log);
    	if(_instr!=null) return _instr;
    	
    	_instr=_getInstrumentation(config.getClassLoader(),log);
    	return _instr;
	}

	private static Instrumentation _getInstrumentation(ClassLoader cl,Log log) {
		// get Class
		Class<?> clazz=ClassUtil.loadClass(cl,AGENT_CLASS_NAME, null);
		if(clazz!=null) {
			log.info("Instrumentation", "found [railo.runtime.instrumentation.ExternalAgent] in ClassLoader ["+clazz.getClassLoader()+"]");
		}
		else {
			log.error("Instrumentation", "not found [railo.runtime.instrumentation.ExternalAgent] in ClassLoader ["+cl+"]");
			return null;
		}
		
		

		try {
			Method m = clazz.getMethod("getInstrumentation", new Class[0]);
			_instr=(Instrumentation) m.invoke(null, new Object[0]);
			
			log.info("Instrumentation", "ExternalAgent does "+(_instr!=null?"":"not ")+"contain a Instrumentation instance");
			
			return _instr;
		}
		catch(Throwable t){
			log.log(Log.LEVEL_ERROR, "Instrumentation", t);
		}
		
		
		
		if(clazz!=null){
			
		}
		return null;
	}

	private static Resource createAgentJar(Log log,Config c) throws IOException {
    	Resource trg = getDeployDirectory(c).getRealResource("railo-external-agent.jar");
    	
		if(!trg.exists()) {
        	log.info("Instrumentation", "create "+trg);
			InputStream jar = InfoImpl.class.getResourceAsStream("/resource/lib/railo-external-agent.jar");
			IOUtil.copy(jar, trg,true);
			
		}
		return trg;
	}
    
    

	/* *
     *  The method that is called when a jar is added as an agent at runtime.
     *  All this method does is store the {@link Instrumentation} for
     *  later use.
     * /
    public static void agentmain(String agentArgs, Instrumentation inst) {
    	_inst=inst;
    }*/

    /* *
     * Create a new jar file for the sole purpose of specifying an Agent-Class
     * to load into the JVM.
     * 
     * @return absolute path to the new jar file.
     */
    /*private static String createAgentJar(Config config) throws IOException {
    	String canRetransform = Boolean.toString(JavaVendor.getCurrentVendor().isIBM() == false);
    	
    	// get Resource
    	String name="agent-"+HashUtil.create64BitHashAsString(Agent.class.getName()+":"+canRetransform, Character.MAX_RADIX)+".jar";
        Resource trg = getDeployDirectory(config).getRealResource(name);
    	if(trg instanceof FileResource)
    		((FileResource)trg).deleteOnExit();
    	
    	if(trg.length()>0) return trg.getAbsolutePath();
    	
    	
    	
    	ZipInputStream zis=null;
    	ZipOutputStream zos=null;
        try {
	        zis = new ZipInputStream( IOUtil.toBufferedInputStream(InfoImpl.class.getResourceAsStream("/resource/lib/railo-external-agent.jar")) );
	        zos=new ZipOutputStream(trg.getOutputStream());
	        ZipEntry entry;
	        // add all classes
	        while ( ( entry = zis.getNextEntry()) != null ) {
	        	if(entry.isDirectory()) {
	                //target.mkdirs();
	            }
	            else {
	            	if(entry.getName().endsWith(".class")) {
		            	zos.putNextEntry(entry);
		                print.e(entry.getName());
		                IOUtil.copy(zis,zos,false,false);
	            	}
	            }
	           zis.closeEntry() ;
	           zos.closeEntry();
	        }
	        
	        // add manifest
	        //ZipOutputStream zout = new ZipOutputStream(trg.getOutputStream());
	        zos.putNextEntry(new ZipEntry("META-INF/MANIFEST.MF"));

	        PrintWriter writer = new PrintWriter(new OutputStreamWriter(zos));
	        writer.println("Agent-Class: railo.runtime.instrumentation.ExternalAgent");
	        writer.println("Can-Redefine-Classes: true");
	        // IBM doesn't support retransform
	        writer.println("Can-Retransform-Classes: " + canRetransform);

	        writer.close();
	        //zos.closeEntry();
	        
    		
        }
        finally {
        	IOUtil.closeEL(zis);
        	IOUtil.closeEL(zos);
        }
    	
    	
    	// add MANIFEST.MF to resource
        

        return trg.getAbsolutePath();
    }*/
	
    private static Resource createToolsJar(Config config) throws IOException  {
    	Resource dir=getDeployDirectory(config);
    	
    	String os="bsd"; // used for Mac OS X
    	if(SystemUtil.isWindows()) {
    		os="windows";
    	}
    	else if(SystemUtil.isLinux()) { // not MacOSX
    		os="linux";
    	}
    	else if(SystemUtil.isSolaris()) {
    		os="solaris";
    	}
    	String name="tools-"+os+"-"+TOOLS_VERSION+".jar";
		Resource trg = dir.getRealResource(name);
		
		if(!trg.exists() || trg.length()==0) {
			
			InputStream jar = InfoImpl.class.getResourceAsStream("/resource/lib/"+name);
			IOUtil.copy(jar, trg,true);
		}
		return trg;
    }
    
    private static Resource getDeployDirectory(Config config) {
    	Resource dir=ConfigWebUtil.getConfigServerDirectory(config);
		if(dir==null || !dir.isWriteable() || !dir.isReadable()) 
			dir= ResourceUtil.toResource(CFMLEngineFactory.getClassLoaderRoot(TP.class.getClassLoader()));
		
		return dir;
	}


    /**
     * This private worker method attempts to find [java_home]/lib/tools.jar.
     * Note: The tools.jar is a part of the SDK, it is not present in the JRE.
     * 
     * @return If tools.jar can be found, a File representing tools.jar. <BR>
     *         If tools.jar cannot be found, null.
     */
    private static Resource findToolsJar(Config config,Log log) {
        String javaHome = System.getProperty("java.home");
        Resource javaHomeFile = ResourcesImpl.getFileResourceProvider().getResource(javaHome);
        
        Resource toolsJarFile = javaHomeFile.getRealResource("lib" + File.separator + "tools.jar");
        if (toolsJarFile.exists() == false) {
            log.trace("Instrumentation",_name + ".findToolsJar() -- couldn't find default " + toolsJarFile.getAbsolutePath());
            
            // If we're on an IBM SDK, then remove /jre off of java.home and try again.
            if (javaHomeFile.getAbsolutePath().endsWith(SEP + "jre") == true) {
                javaHomeFile = javaHomeFile.getParentResource();
                toolsJarFile = javaHomeFile.getRealResource( "lib" + SEP + "tools.jar");
                if (toolsJarFile.exists() == false) {
                    log.trace("Instrumentation",_name + ".findToolsJar() -- for IBM SDK couldn't find " +
                            toolsJarFile.getAbsolutePath());
                    
                }
            } else if (System.getProperty("os.name").toLowerCase().indexOf("mac") >= 0) {
                // If we're on a Mac, then change the search path to use ../Classes/classes.jar.
                if (javaHomeFile.getAbsolutePath().endsWith(SEP + "Home") == true) {
                    javaHomeFile = javaHomeFile.getParentResource();
                    toolsJarFile = javaHomeFile.getRealResource("Classes" + SEP + "classes.jar");
                    if (toolsJarFile.exists() == false) {
                        log.trace("Instrumentation",_name + ".findToolsJar() -- for Mac OS couldn't find " +
                                toolsJarFile.getAbsolutePath());
                        
                    }
                }
            }
        }
        
        // if Railo could not find the tools.jar it is using it's own version
        if (true || !toolsJarFile.exists()) {
        	try {
				toolsJarFile=createToolsJar(config);
			}
			catch (IOException e) {e.printStackTrace();}
        }
        
        if (!toolsJarFile.exists()) {
            return null;
        } 
        log.trace("Instrumentation",_name + ".findToolsJar() -- found " + toolsJarFile.getAbsolutePath());
        return toolsJarFile;
        
    }


    /**
     * Attach and load an agent class. 
     * 
     * @param log Log used if the agent cannot be loaded.
     * @param agentJar absolute path to the agent jar.
     * @param vmClass VirtualMachine.class from tools.jar.
     */
    private static void loadAgent(Log log, String agentJar, Class<?> vmClass) {
        try {
            // first obtain the PID of the currently-running process
            // ### this relies on the undocumented convention of the
            // RuntimeMXBean's
            // ### name starting with the PID, but there appears to be no other
            // ### way to obtain the current process' id, which we need for
            // ### the attach process
            RuntimeMXBean runtime = ManagementFactory.getRuntimeMXBean();
            String pid = runtime.getName();
            if (pid.indexOf("@") != -1)
                pid = pid.substring(0, pid.indexOf("@"));
            log.info("Instrumentation","pid:"+pid);
            // JDK1.6: now attach to the current VM so we can deploy a new agent
            // ### this is a Sun JVM specific feature; other JVMs may offer
            // ### this feature, but in an implementation-dependent way
            Object vm =
                vmClass.getMethod("attach", new Class<?>[] { String.class })
                    .invoke(null, new Object[] { pid });
            // now deploy the actual agent, which will wind up calling
            // agentmain()
            vmClass.getMethod("loadAgent", new Class[] { String.class })
                .invoke(vm, new Object[] { agentJar });
            vmClass.getMethod("detach", new Class[] {}).invoke(vm,
                new Object[] {});
        } catch (Throwable t) {
        		// Log the message from the exception. Don't log the entire
                // stack as this is expected when running on a JDK that doesn't
                // support the Attach API.
                log.log(Log.LEVEL_ERROR,"Instrumentation",t);
            
        }
    }

    /**
     * If <b>ibm</b> is false, this private method will create a new URLClassLoader and attempt to load the
     * com.sun.tools.attach.VirtualMachine class from the provided toolsJar file. 
     * 
     * <p>
     * If <b>ibm</b> is true, this private method will ignore the toolsJar parameter and load the 
     * com.ibm.tools.attach.VirtualMachine class. 
     * 
     * 
     * @return The AttachAPI VirtualMachine class <br>
     *         or null if something unexpected happened.
     */
    private static Class<?> loadVMClass(Resource toolsJar, Log log, JavaVendor vendor) {
        try {
            ClassLoader loader = ClassLoader.getSystemClassLoader();
            String cls = vendor.getVirtualMachineClassName();
            if (vendor.isIBM() == false) {
                loader = new URLClassLoader(new URL[] { ((FileResource)toolsJar).toURI().toURL() }, loader);
            }
            return loader.loadClass(cls);
        } catch (Exception e) {
            log.log(Log.LEVEL_ERROR,"Instrumentation",e);
            
        }
        return null;
    }

    /*private static boolean validateAgentJarManifestX(File agentJarFile, Log log,
        String agentClassName) {
        try {
            JarFile jar = new JarFile(agentJarFile);
            Manifest manifest = jar.getManifest();
            if (manifest == null) {
                return false;
            }
            Attributes attributes = manifest.getMainAttributes();
            String ac = attributes.getValue("Agent-Class");
            if (ac != null && ac.equals(agentClassName)) {
                return true;
            }
        } catch (Exception e) {
            log.log(Log.LEVEL_ERROR, "Instrumentation", e);
            
        }
        return false;
    }*/
    

    /*
     * This private worker method will return a fully qualified path to a jar
     * that has this class defined as an Agent-Class in it's
     * META-INF/manifest.mf file. Under normal circumstances the path should
     * point to the OpenJPA jar. If running in a development environment a
     * temporary jar file will be created.
     * 
     * @return absolute path to the agent jar or null if anything unexpected
     * happens.
     */
    /*private static String getAgentJar(Log log) {
        File agentJarFile = null;
        // Find the name of the File that this class was loaded from. That
        // jar *should* be the same location as our agent.
        CodeSource cs =
            InstrumentationFactory.class.getProtectionDomain().getCodeSource();
        if (cs != null) {   
            URL loc = cs.getLocation();
            if(loc!=null){
                agentJarFile = new File(loc.getFile());
            }
        }
        // Determine whether the File that this class was loaded from has this
        // class defined as the Agent-Class.
        boolean createJar = true;
        if (cs == null || agentJarFile == null
            || agentJarFile.isDirectory() == true) {
            createJar = true;
        }else if(validateAgentJarManifestX(agentJarFile, log, _name) == false){
            // We have an agentJarFile, but this class isn't the Agent-Class.
            createJar=true;           
        }
        
        String agentJar;
        if (createJar) {
            // This can happen when running in eclipse as an OpenJPA
            // developer or for some reason the CodeSource is null. We
            // should log a warning here because this will create a jar
            // in your temp directory that doesn't always get cleaned up.
            try {
                agentJar = createAgentJar();
                //log.info("Instrumentation",_loc.get("temp-file-creation", agentJar).toString());
                
            } catch (IOException ioe) {
                log.log(Log.LEVEL_TRACE, "Instrumentation",  ioe);
                agentJar = null;
            }
        } else {
            agentJar = agentJarFile.getAbsolutePath();
        }

        return agentJar;
    }*/
    

	/*private static void createToolsJar() throws IOException {
    	Resource toolsJarFile = ResourcesImpl.getFileResourceProvider().getResource("/Users/mic/Tmp3/tools.jar");
    	Resource trg = ResourcesImpl.getFileResourceProvider().getResource("/Users/mic/Tmp3/toolsxx.jar");
    	
    	ZipInputStream zis=null;
    	ZipOutputStream zos=null;
        try {
	        zis = new ZipInputStream( IOUtil.toBufferedInputStream(toolsJarFile.getInputStream()) );
	        zos=new ZipOutputStream(trg.getOutputStream());
	        ZipEntry entry;
	        while ( ( entry = zis.getNextEntry()) != null ) {
	        	if(entry.isDirectory()) {
	                //target.mkdirs();
	            }
	            else {
	            	if(!entry.getName().startsWith("META-INF") || entry.getName().endsWith("MANIFEST.MF") || entry.getName().endsWith("AttachProvider")) {
	            	zos.putNextEntry(entry);
	                print.e(entry.getName());
	                IOUtil.copy(zis,zos,false,false);
	            	}
	            }
	           zis.closeEntry() ;
	           zos.closeEntry();
	        }
    		
        }
        finally {
        	IOUtil.closeEL(zis);
        	IOUtil.closeEL(zos);
        }
    	
    }*/

}
