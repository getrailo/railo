package railo.commons.io;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryPoolMXBean;
import java.lang.management.MemoryType;
import java.lang.management.MemoryUsage;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.NetworkInterface;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.ServletContext;

import railo.commons.digest.MD5;
import railo.commons.io.res.Resource;
import railo.commons.io.res.ResourceProvider;
import railo.commons.io.res.ResourcesImpl;
import railo.commons.io.res.util.ResourceUtil;
import railo.commons.lang.ClassUtil;
import railo.commons.lang.StringUtil;
import railo.loader.TP;
import railo.loader.engine.CFMLEngineFactory;
import railo.runtime.Info;
import railo.runtime.config.Config;
import railo.runtime.exp.ApplicationException;
import railo.runtime.exp.DatabaseException;
import railo.runtime.functions.other.CreateUniqueId;
import railo.runtime.net.http.ReqRspUtil;
import railo.runtime.op.Caster;
import railo.runtime.type.Array;
import railo.runtime.type.Collection;
import railo.runtime.type.KeyImpl;
import railo.runtime.type.Query;
import railo.runtime.type.QueryImpl;
import railo.runtime.type.Struct;
import railo.runtime.type.StructImpl;
import railo.runtime.type.util.KeyConstants;
import railo.runtime.type.util.ListUtil;

import com.jezhumble.javasysmon.CpuTimes;
import com.jezhumble.javasysmon.JavaSysMon;
import com.jezhumble.javasysmon.MemoryStats;

/**
 * 
 */
public final class SystemUtil {

	public static final int MEMORY_TYPE_ALL=0;
	public static final int MEMORY_TYPE_HEAP=1;
	public static final int MEMORY_TYPE_NON_HEAP=2;

	public static final int ARCH_UNKNOW=0;
	public static final int ARCH_32=32;
	public static final int ARCH_64=64;

	public static final char CHAR_DOLLAR=(char)36;
	public static final char CHAR_POUND=(char)163;
	public static final char CHAR_EURO=(char)8364;
	

	public static final int OUT = 0;
	public static final int ERR = 1;
	
	private static final PrintWriter PRINTWRITER_OUT = new PrintWriter(System.out);
	private static final PrintWriter PRINTWRITER_ERR = new PrintWriter(System.err);
	
	private static PrintWriter[] printWriter=new PrintWriter[2];
    
    
	private static final boolean isWindows=System.getProperty("os.name").toLowerCase().startsWith("windows");
    private static final boolean isUnix=!isWindows &&  File.separatorChar == '/';
	
    private static Resource tempFile;
    private static Resource homeFile;
    private static Resource[] classPathes;
    private static Charset charset;
    private static String lineSeparator=System.getProperty("line.separator","\n");
    private static MemoryPoolMXBean permGenSpaceBean;

	public static int osArch=-1;
	public static int jreArch=-1;
	
	static {
		String strCharset=System.getProperty("file.encoding");
		if(strCharset==null || strCharset.equalsIgnoreCase("MacRoman"))
			strCharset="cp1252";

		if(strCharset.equalsIgnoreCase("utf-8")) charset=CharsetUtil.UTF8;
		else if(strCharset.equalsIgnoreCase("iso-8859-1")) charset=CharsetUtil.ISO88591;
		else charset=CharsetUtil.toCharset(strCharset,null);
		
		// Perm Gen
		permGenSpaceBean=getPermGenSpaceBean();
		// make sure the JVM does not always a new bean
		MemoryPoolMXBean tmp = getPermGenSpaceBean();
		if(tmp!=permGenSpaceBean)permGenSpaceBean=null;
	}
	
	
	
	public static MemoryPoolMXBean getPermGenSpaceBean() {
		java.util.List<MemoryPoolMXBean> manager = ManagementFactory.getMemoryPoolMXBeans();
		MemoryPoolMXBean bean;
		// PERM GEN
		Iterator<MemoryPoolMXBean> it = manager.iterator();
		while(it.hasNext()){
			bean = it.next();
			if("Perm Gen".equalsIgnoreCase(bean.getName()) || "CMS Perm Gen".equalsIgnoreCase(bean.getName())) {
				return bean;
			}
		}
		it = manager.iterator();
		while(it.hasNext()){
			bean = it.next();
			if(StringUtil.indexOfIgnoreCase(bean.getName(),"Perm Gen")!=-1 || StringUtil.indexOfIgnoreCase(bean.getName(),"PermGen")!=-1) {
				return bean;
			}
		}
		// take none-heap when only one
		it = manager.iterator();
		LinkedList<MemoryPoolMXBean> beans=new LinkedList<MemoryPoolMXBean>();
		while(it.hasNext()){
			bean = it.next();
			if(bean.getType().equals(MemoryType.NON_HEAP)) {
				beans.add(bean);
				return bean;
			}
		}
		if(beans.size()==1) return beans.getFirst();
		
		// Class Memory/ClassBlock Memory?
		it = manager.iterator();
		while(it.hasNext()){
			bean = it.next();
			if(StringUtil.indexOfIgnoreCase(bean.getName(),"Class Memory")!=-1) {
				return bean;
			}
		}
		
		
		return null;
	}
	
    private static Boolean isFSCaseSensitive;
	private static JavaSysMon jsm;
	private static Boolean isCLI;
	private static double loaderVersion=0D;
	private static String macAddress; 

    /**
     * returns if the file system case sensitive or not
     * @return is the file system case sensitive or not
     */
    public static boolean isFSCaseSensitive() { 
        if(isFSCaseSensitive==null) { 
                try { 
                	_isFSCaseSensitive(File.createTempFile("abcx","txt"));
                } 
                catch (IOException e) { 
            		File f = new File("abcx.txt").getAbsoluteFile();
            		try {
						f.createNewFile();
	                    _isFSCaseSensitive(f);
						
					} catch (IOException e1) {
						throw new RuntimeException(e1.getMessage());
					}
                } 
        } 
        return isFSCaseSensitive.booleanValue(); 
    }
    private static void _isFSCaseSensitive(File f) { 
        File temp=new File(f.getPath().toUpperCase()); 
        isFSCaseSensitive=temp.exists()?Boolean.FALSE:Boolean.TRUE; 
        f.delete(); 
    }


	/**
	 * fixes a java canonical path to a Windows path
	 * e.g. /C:/Windows/System32 will be changed to C:\Windows\System32
	 *
	 * @param path
	 * @return
	 */
	public static String fixWindowsPath(String path) {
		if ( isWindows && path.length() > 3 && path.charAt(0) == '/' && path.charAt(2) == ':' ) {
			path = path.substring(1).replace( '/', '\\' );
		}
		return path;
	}


    /**
     * @return is local machine a Windows Machine
     */
    public static boolean isWindows() {
        return isWindows;
    }

    /**
     * @return is local machine a Unix Machine
     */
    public static boolean isUnix() {
        return isUnix;
    }

    /**
     * @return return System directory
     */
    public static Resource getSystemDirectory() {
        String pathes=System.getProperty("java.library.path");
        ResourceProvider fr = ResourcesImpl.getFileResourceProvider();
        if(pathes!=null) {
            String[] arr=ListUtil.toStringArrayEL(ListUtil.listToArray(pathes,File.pathSeparatorChar));
            for(int i=0;i<arr.length;i++) {    
                if(arr[i].toLowerCase().indexOf("windows\\system")!=-1) {
                    Resource file = fr.getResource(arr[i]);
                    if(file.exists() && file.isDirectory() && file.isWriteable()) return ResourceUtil.getCanonicalResourceEL(file);
                    
                }
            }
            for(int i=0;i<arr.length;i++) {    
                if(arr[i].toLowerCase().indexOf("windows")!=-1) {
                	Resource file = fr.getResource(arr[i]);
                    if(file.exists() && file.isDirectory() && file.isWriteable()) return ResourceUtil.getCanonicalResourceEL(file);
                    
                }
            }
            for(int i=0;i<arr.length;i++) {    
                if(arr[i].toLowerCase().indexOf("winnt")!=-1) {
                	Resource file = fr.getResource(arr[i]);
                    if(file.exists() && file.isDirectory() && file.isWriteable()) return ResourceUtil.getCanonicalResourceEL(file);
                    
                }
            }
            for(int i=0;i<arr.length;i++) {    
                if(arr[i].toLowerCase().indexOf("win")!=-1) {
                	Resource file = fr.getResource(arr[i]);
                    if(file.exists() && file.isDirectory() && file.isWriteable()) return ResourceUtil.getCanonicalResourceEL(file);
                    
                }
            }
            for(int i=0;i<arr.length;i++) {
            	Resource file = fr.getResource(arr[i]);
                if(file.exists() && file.isDirectory() && file.isWriteable()) return ResourceUtil.getCanonicalResourceEL(file);
            }
        }
        return null;
    }
    
    /**
     * @return return running context root
     */
    public static Resource getRuningContextRoot() {
    	ResourceProvider frp = ResourcesImpl.getFileResourceProvider();
        
        try {
            return frp.getResource(".").getCanonicalResource();
        } catch (IOException e) {}
        URL url=new Info().getClass().getClassLoader().getResource(".");
        try {
            return frp.getResource(FileUtil.URLToFile(url).getAbsolutePath());
        } catch (MalformedURLException e) {
            return null;
        }
    }
    
    /**
     * returns the Temp Directory of the System
     * @return temp directory
     */
    public static Resource getTempDirectory() {
        if(tempFile!=null) return tempFile;
        ResourceProvider fr = ResourcesImpl.getFileResourceProvider();
        String tmpStr = System.getProperty("java.io.tmpdir");
        if(tmpStr!=null) {
            tempFile=fr.getResource(tmpStr);
            if(tempFile.exists()) {
                tempFile=ResourceUtil.getCanonicalResourceEL(tempFile);
                return tempFile;
            }
        }
        File tmp =null;
        try {
        	tmp = File.createTempFile("a","a");
            tempFile=fr.getResource(tmp.getParent());
            tempFile=ResourceUtil.getCanonicalResourceEL(tempFile);   
        }
        catch(IOException ioe) {}
        finally {
        	if(tmp!=null)tmp.delete();
        }
        return tempFile;
    }
    

    /**
     * returns the a unique temp file (with no auto delete)
     * @param extension 
     * @return temp directory
     * @throws IOException 
     */
    public static Resource getTempFile(String extension, boolean touch) throws IOException {
    	String filename=CreateUniqueId.invoke();
    	if(!StringUtil.isEmpty(extension,true)){
    		if(extension.startsWith("."))filename+=extension;
    		else filename+="."+extension;
    	}
		Resource file = getTempDirectory().getRealResource(filename);
		if(touch)ResourceUtil.touch(file);
		return file;
	}
    
    /**
     * returns the Hoome Directory of the System
     * @return home directory
     */
    public static Resource getHomeDirectory() {
        if(homeFile!=null) return homeFile;
        
        ResourceProvider frp = ResourcesImpl.getFileResourceProvider();
        
        String homeStr = System.getProperty("user.home");
        if(homeStr!=null) {
            homeFile=frp.getResource(homeStr);
            homeFile=ResourceUtil.getCanonicalResourceEL(homeFile);
        }
        return homeFile;
    }
    
    public static Resource getClassLoadeDirectory(){
    	return ResourceUtil.toResource(CFMLEngineFactory.getClassLoaderRoot(TP.class.getClassLoader()));
    }

    /**
     * get class pathes from all url ClassLoaders
     * @param ucl URL Class Loader
     * @param pathes Hashmap with allpathes
     */
    private static void getClassPathesFromClassLoader(URLClassLoader ucl, ArrayList pathes) {
        ClassLoader pcl=ucl.getParent();
        // parent first
        if(pcl instanceof URLClassLoader)
            getClassPathesFromClassLoader((URLClassLoader) pcl, pathes);

        ResourceProvider frp = ResourcesImpl.getFileResourceProvider();
        // get all pathes
        URL[] urls=ucl.getURLs();
        for(int i=0;i<urls.length;i++) {
            Resource file=frp.getResource(urls[i].getPath());
            if(file.exists())
                pathes.add(ResourceUtil.getCanonicalResourceEL(file));
        }
        
    }
    
    /**
     * @return returns a string list of all pathes
     */
    public static Resource[] getClassPathes() {
        
        if(classPathes!=null) 
            return classPathes;
        
        ArrayList pathes=new ArrayList();
        String pathSeperator=System.getProperty("path.separator");
        if(pathSeperator==null)pathSeperator=";";
            
    // java.ext.dirs
        ResourceProvider frp = ResourcesImpl.getFileResourceProvider();
    	
        
    // pathes from system properties
        String strPathes=System.getProperty("java.class.path");
        if(strPathes!=null) {
            Array arr=ListUtil.listToArrayRemoveEmpty(strPathes,pathSeperator);
            int len=arr.size();
            for(int i=1;i<=len;i++) {
                Resource file=frp.getResource(Caster.toString(arr.get(i,""),"").trim());
                if(file.exists())
                    pathes.add(ResourceUtil.getCanonicalResourceEL(file));
            }
        }
        
        
    // pathes from url class Loader (dynamic loaded classes)
        ClassLoader cl = new Info().getClass().getClassLoader();
        if(cl instanceof URLClassLoader) 
            getClassPathesFromClassLoader((URLClassLoader) cl, pathes);
        
        return classPathes=(Resource[]) pathes.toArray(new Resource[pathes.size()]);
        
    }

    public static long getUsedMemory() {
        Runtime r = Runtime.getRuntime();
        return r.totalMemory()-r.freeMemory();
    }
    public static long getAvailableMemory() {
        Runtime r = Runtime.getRuntime();
        return r.freeMemory();
    }

    /**
     * replace path placeholder with the real path, placeholders are [{temp-directory},{system-directory},{home-directory}]
     * @param path
     * @return updated path
     */
    public static String parsePlaceHolder(String path) {
        if(path==null) return path;
        // Temp
        if(path.startsWith("{temp")) {
            if(path.startsWith("}",5)) path=getTempDirectory().getRealResource(path.substring(6)).toString();
            else if(path.startsWith("-dir}",5)) path=getTempDirectory().getRealResource(path.substring(10)).toString();
            else if(path.startsWith("-directory}",5)) path=getTempDirectory().getRealResource(path.substring(16)).toString();
        }
        // System
        else if(path.startsWith("{system")) {
            if(path.startsWith("}",7)) path=getSystemDirectory().getRealResource(path.substring(8)).toString();
            else if(path.startsWith("-dir}",7)) path=getSystemDirectory().getRealResource(path.substring(12)).toString();
            else if(path.startsWith("-directory}",7)) path=getSystemDirectory().getRealResource(path.substring(18)).toString();
        }
        // Home
        else if(path.startsWith("{home")) {
            if(path.startsWith("}",5)) path=getHomeDirectory().getRealResource(path.substring(6)).toString();
            else if(path.startsWith("-dir}",5)) path=getHomeDirectory().getRealResource(path.substring(10)).toString();
            else if(path.startsWith("-directory}",5)) path=getHomeDirectory().getRealResource(path.substring(16)).toString();
        }
        // ClassLoaderDir
        else if(path.startsWith("{classloader")) {
            if(path.startsWith("}",12)) path=getClassLoadeDirectory().getRealResource(path.substring(13)).toString();
            else if(path.startsWith("-dir}",12)) path=getClassLoadeDirectory().getRealResource(path.substring(17)).toString();
            else if(path.startsWith("-directory}",12)) path=getClassLoadeDirectory().getRealResource(path.substring(23)).toString();
        }
        return path;
    }
    
    public static String addPlaceHolder(Resource file, String defaultValue) {
     // Temp
        String path=addPlaceHolder(getTempDirectory(),file,"{temp-directory}");
        if(!StringUtil.isEmpty(path)) return path;
     // System
        path=addPlaceHolder(getSystemDirectory(),file,"{system-directory}");
        if(!StringUtil.isEmpty(path)) return path;
     // Home
        path=addPlaceHolder(getHomeDirectory(),file,"{home-directory}");
        if(!StringUtil.isEmpty(path)) return path;
        
      
        return defaultValue;
    }
    
    private static String addPlaceHolder(Resource dir, Resource file,String placeholder) {
    	if(ResourceUtil.isChildOf(file, dir)){
        	try {
				return StringUtil.replace(file.getCanonicalPath(), dir.getCanonicalPath(), placeholder, true);
			} 
        	catch (IOException e) {}
        }
    	return null;
	}
    

	public static String addPlaceHolder(Resource file,  Config config, String defaultValue) {
    	//ResourceProvider frp = ResourcesImpl.getFileResourceProvider();
    	
        // temp
        	Resource dir = config.getTempDirectory();
        	String path = addPlaceHolder(dir,file,"{temp-directory}");
        	if(!StringUtil.isEmpty(path)) return path;
            	
        // Config 
        	dir = config.getConfigDir();
        	path = addPlaceHolder(dir,file,"{railo-config-directory}");
        	if(!StringUtil.isEmpty(path)) return path;

        /* / Config WEB
        	dir = config.getConfigDir();
        	path = addPlaceHolder(dir,file,"{railo-server-directory}");
        	if(!StringUtil.isEmpty(path)) return path;
*/
        // Web root
        	dir = config.getRootDirectory();
        	path = addPlaceHolder(dir,file,"{web-root-directory}");
        	if(!StringUtil.isEmpty(path)) return path;

        return addPlaceHolder(file, defaultValue);
    }
	
	public static String parsePlaceHolder(String path, ServletContext sc, Map<String,String> labels) {
		if(path==null) return null;
        if(path.indexOf('{')!=-1){
        	if((path.indexOf("{web-context-label}"))!=-1){
        		String id=hash(sc);
        		
        		String label=labels.get(id);
        		if(StringUtil.isEmpty(label)) label=id;
        		
        		path=StringUtil.replace(path, "{web-context-label}", label, false);
        	}
        }
        return parsePlaceHolder(path, sc);
    }
    
	public static String parsePlaceHolder(String path, ServletContext sc) {
    	ResourceProvider frp = ResourcesImpl.getFileResourceProvider();
    	
    	
        if(path==null) return null;
        if(path.indexOf('{')!=-1){
        	if(StringUtil.startsWith(path,'{')){
	            
	            // Web Root
	            if(path.startsWith("{web-root")) {
	                if(path.startsWith("}",9)) 					path=frp.getResource(ReqRspUtil.getRootPath(sc)).getRealResource(path.substring(10)).toString();
	                else if(path.startsWith("-dir}",9)) 		path=frp.getResource(ReqRspUtil.getRootPath(sc)).getRealResource(path.substring(14)).toString();
	                else if(path.startsWith("-directory}",9)) 	path=frp.getResource(ReqRspUtil.getRootPath(sc)).getRealResource(path.substring(20)).toString();
	
	            }
	            else path=SystemUtil.parsePlaceHolder(path);
	        }
        	
        	if((path.indexOf("{web-context-hash}"))!=-1){
        		String id=hash(sc);
        		path=StringUtil.replace(path, "{web-context-hash}", id, false);
        	}
        }
        return path;
    }
	
	public static String hash(ServletContext sc) {
    	String id=null;
		try {
			id=MD5.getDigestAsString(ReqRspUtil.getRootPath(sc));
		} 
		catch (IOException e) {}
		return id;
    }

    public static Charset getCharset() {
    	return charset;
    }

	public static void setCharset(String charset) {
		SystemUtil.charset = CharsetUtil.toCharset(charset);
	}
	public static void setCharset(Charset charset) {
		SystemUtil.charset = charset;
	}

	public static String getOSSpecificLineSeparator() {
		return lineSeparator;
	}

	public static void sleep(int time) {
		try {
			Thread.sleep(time);
		} catch (InterruptedException e) {}
	}

	public static void sleep(long time) {
		try {
			Thread.sleep(time);
		} catch (InterruptedException e) {}
	}
	public static void join(Thread t) {
		try {
			t.join();
		} catch (InterruptedException e) {}
	}
	
	/**
	 * locks the object (synchronized) before calling wait
	 * @param lock
	 * @param timeout
	 * @throws InterruptedException
	 */
	public static void wait(Object lock, long timeout) {
		try {
			synchronized (lock) {lock.wait(timeout);}
		} catch (InterruptedException e) {}
	}

	/**
	 * locks the object (synchronized) before calling wait (no timeout)
	 * @param lock
	 * @throws InterruptedException
	 */
	public static void wait(Object lock) {
		try {
			synchronized (lock) {lock.wait();}
		} catch (InterruptedException e) {}
	}
	
	
	
	/**
	 * locks the object (synchronized) before calling notify
	 * @param lock
	 * @param timeout
	 * @throws InterruptedException
	 */
	public static void notify(Object lock) {
		synchronized (lock) {lock.notify();}
	}
	
	/**
	 * locks the object (synchronized) before calling notifyAll
	 * @param lock
	 * @param timeout
	 * @throws InterruptedException
	 */
	public static void notifyAll(Object lock) {
		synchronized (lock) {lock.notifyAll();}
	}

	/**
	 * return the operating system architecture
	 * @return one of the following SystemUtil.ARCH_UNKNOW, SystemUtil.ARCH_32, SystemUtil.ARCH_64
	 */
	public static int getOSArch(){
		if(osArch==-1) {
			osArch = toIntArch(System.getProperty("os.arch.data.model"));
			if(osArch==ARCH_UNKNOW)osArch = toIntArch(System.getProperty("os.arch"));
		}
		return osArch;
	}
	
	/**
	 * return the JRE (Java Runtime Engine) architecture, this can be different from the operating system architecture
	 * @return one of the following SystemUtil.ARCH_UNKNOW, SystemUtil.ARCH_32, SystemUtil.ARCH_64
	 */
	public static int getJREArch(){
		if(jreArch==-1) {
			jreArch = toIntArch(System.getProperty("sun.arch.data.model"));
			if(jreArch==ARCH_UNKNOW)jreArch = toIntArch(System.getProperty("com.ibm.vm.bitmode"));
			if(jreArch==ARCH_UNKNOW)jreArch = toIntArch(System.getProperty("java.vm.name"));
			if(jreArch==ARCH_UNKNOW) {
				int addrSize = getAddressSize();
				if(addrSize==4) return ARCH_32;
				if(addrSize==8) return ARCH_64;
			}
			
		}
		return jreArch;
	}
	
	private static int toIntArch(String strArch){
		if(!StringUtil.isEmpty(strArch)) {
			if(strArch.indexOf("64")!=-1) return ARCH_64;
			if(strArch.indexOf("32")!=-1) return ARCH_32;
			if(strArch.indexOf("i386")!=-1) return ARCH_32;
			if(strArch.indexOf("x86")!=-1) return ARCH_32;
		}
		return ARCH_UNKNOW;
	}
	

	
	public static int getAddressSize() {
		try {
			Class unsafe = ClassUtil.loadClass(null,"sun.misc.Unsafe",null);
			if(unsafe==null) return 0;
		
			Field unsafeField = unsafe.getDeclaredField("theUnsafe");
		    unsafeField.setAccessible(true);
		    Object obj = unsafeField.get(null);
		    Method addressSize = unsafe.getMethod("addressSize", new Class[0]);
		    
		    Object res = addressSize.invoke(obj, new Object[0]);
		    return Caster.toIntValue(res,0);
		}
		catch(Throwable t){
			return 0;
		}
	    
	}
	/*private static MemoryUsage getPermGenSpaceSize() {
		MemoryUsage mu = getPermGenSpaceSize(null);
		if(mu!=null) return mu;
		
		// create error message including info about available memory blocks
		StringBuilder sb=new StringBuilder();
		java.util.List<MemoryPoolMXBean> manager = ManagementFactory.getMemoryPoolMXBeans();
		Iterator<MemoryPoolMXBean> it = manager.iterator();
		MemoryPoolMXBean bean;
		while(it.hasNext()){
			bean = it.next();
			if(sb.length()>0)sb.append(", ");
			sb.append(bean.getName());
		}
		throw new RuntimeException("PermGen Space information not available, available Memory blocks are ["+sb+"]");
	}*/
	
	private static MemoryUsage getPermGenSpaceSize(MemoryUsage defaultValue) {
		if(permGenSpaceBean!=null) return permGenSpaceBean.getUsage();
		// create on the fly when the bean is not permanent
		MemoryPoolMXBean tmp = getPermGenSpaceBean();
		if(tmp!=null) return tmp.getUsage();
		
		return defaultValue;
	}
	

	public static long getFreePermGenSpaceSize() {
		MemoryUsage mu = getPermGenSpaceSize(null);
		if(mu==null) return -1;
		
		long max = mu.getMax();
		long used = mu.getUsed();
		if(max<0 || used<0) return -1;
		return max-used;
	}
	
	public static int getFreePermGenSpacePromille() {
		MemoryUsage mu = getPermGenSpaceSize(null);
		if(mu==null) return -1;
		
		long max = mu.getMax();
		long used = mu.getUsed();
		if(max<0 || used<0) return -1;
		return (int)(1000L-(1000L*used/max));
	}
	
	public static Query getMemoryUsageAsQuery(int type) throws DatabaseException {
		
		
		java.util.List<MemoryPoolMXBean> manager = ManagementFactory.getMemoryPoolMXBeans();
		Iterator<MemoryPoolMXBean> it = manager.iterator();
		Query qry=new QueryImpl(new Collection.Key[]{
				KeyConstants._name,
				KeyConstants._type,
				KeyConstants._used,
				KeyConstants._max,
				KeyConstants._init
		},0,"memory");
		
		int row=0;
		MemoryPoolMXBean bean;
		MemoryUsage usage;
		MemoryType _type;
		while(it.hasNext()){
			bean = it.next();
			usage = bean.getUsage();
			_type = bean.getType();
			if(type==MEMORY_TYPE_HEAP && _type!=MemoryType.HEAP)continue;
			if(type==MEMORY_TYPE_NON_HEAP && _type!=MemoryType.NON_HEAP)continue;
				
			row++;
			qry.addRow();
			qry.setAtEL(KeyConstants._name, row, bean.getName());
			qry.setAtEL(KeyConstants._type, row, _type.name());
			qry.setAtEL(KeyConstants._max, row, Caster.toDouble(usage.getMax()));
			qry.setAtEL(KeyConstants._used, row, Caster.toDouble(usage.getUsed()));
			qry.setAtEL(KeyConstants._init, row, Caster.toDouble(usage.getInit()));
			
		}
		return qry;
	}
	
	public static Struct getMemoryUsageAsStruct(int type) {
		java.util.List<MemoryPoolMXBean> manager = ManagementFactory.getMemoryPoolMXBeans();
		Iterator<MemoryPoolMXBean> it = manager.iterator();
		
		MemoryPoolMXBean bean;
		MemoryUsage usage;
		MemoryType _type;
		long used=0,max=0,init=0;
		while(it.hasNext()){
			bean = it.next();
			usage = bean.getUsage();
			_type = bean.getType();
			if((type==MEMORY_TYPE_HEAP && _type==MemoryType.HEAP) || (type==MEMORY_TYPE_NON_HEAP && _type==MemoryType.NON_HEAP)){
				used+=usage.getUsed();
				max+=usage.getMax();
				init+=usage.getInit();
			}
		}
		Struct sct=new StructImpl();
		sct.setEL(KeyConstants._used, Caster.toDouble(used));
		sct.setEL(KeyConstants._max, Caster.toDouble(max));
		sct.setEL(KeyConstants._init, Caster.toDouble(init));
		sct.setEL(KeyImpl.init("available"), Caster.toDouble(max-used));
		return sct;
	}
	

	public static Struct getMemoryUsageCompact(int type) {
		java.util.List<MemoryPoolMXBean> manager = ManagementFactory.getMemoryPoolMXBeans();
		Iterator<MemoryPoolMXBean> it = manager.iterator();
		
		MemoryPoolMXBean bean;
		MemoryUsage usage;
		MemoryType _type;
		Struct sct=new StructImpl();
		while(it.hasNext()){
			bean = it.next();
			usage = bean.getUsage();
			_type = bean.getType();
			if(type==MEMORY_TYPE_HEAP && _type!=MemoryType.HEAP)continue;
			if(type==MEMORY_TYPE_NON_HEAP && _type!=MemoryType.NON_HEAP)continue;
				
			double d=((int)(100D/usage.getMax()*usage.getUsed()))/100D;
			sct.setEL(KeyImpl.init(bean.getName()), Caster.toDouble(d));
		}
		return sct;
	}
	
	public static String getPropertyEL(String key) {
		try{
			String str = System.getProperty(key);
			if(!StringUtil.isEmpty(str,true)) return str;
			
			Iterator<Entry<Object, Object>> it = System.getProperties().entrySet().iterator();
			Entry<Object, Object> e;
			String n;
			while(it.hasNext()){
				e = it.next();
				n=(String) e.getKey();
				if(key.equalsIgnoreCase(n)) return (String) e.getValue();
			}
			
		}
		catch(Throwable t){}
		return null;
	}
	public static long microTime() {
		return System.nanoTime()/1000L;
	}
	
	public static TemplateLine getCurrentContext() {
		return _getCurrentContext(new Exception("Stack trace"));
	}
	private static TemplateLine _getCurrentContext(Throwable t) {
		
		//Throwable root = t.getRootCause();
		Throwable cause = t.getCause(); 
		if(cause!=null)_getCurrentContext(cause);
		StackTraceElement[] traces = t.getStackTrace();
		
		
        int line=0;
		String template;
		
		StackTraceElement trace=null;
		for(int i=0;i<traces.length;i++) {
			trace=traces[i];
			template=trace.getFileName();
			if(trace.getLineNumber()<=0 || template==null || ResourceUtil.getExtension(template,"").equals("java")) continue;
			line=trace.getLineNumber();
			return new TemplateLine(template,line);
		}
		return null;
	}
	
	public static class TemplateLine {

		public final String template;
		public final int line;

		public TemplateLine(String template, int line) {
			this.template=template;
			this.line=line;
		}
		public String toString(){
			return template+":"+line;
		}
	}

	public static long getFreeBytes() throws ApplicationException {
		return physical().getFreeBytes();
	}

	public static long getTotalBytes() throws ApplicationException {
		return physical().getTotalBytes();
	}
	
	public static double getCpuUsage(long time) throws ApplicationException {
		if(time<1) throw new ApplicationException("time has to be bigger than 0");
		if(jsm==null) jsm=new JavaSysMon();
		CpuTimes cput = jsm.cpuTimes();
		if(cput==null) throw new ApplicationException("CPU information are not available for this OS");
		CpuTimes previous = new CpuTimes(cput.getUserMillis(),cput.getSystemMillis(),cput.getIdleMillis());
        sleep(time);
        
        return jsm.cpuTimes().getCpuUsage(previous)*100D;
    }
	

	private synchronized static MemoryStats physical() throws ApplicationException {
		if(jsm==null) jsm=new JavaSysMon();
		MemoryStats p = jsm.physical();
		if(p==null) throw new ApplicationException("Memory information are not available for this OS");
		return p;
	}
	public static void setPrintWriter(int type,PrintWriter pw) {
		printWriter[type]=pw;
	}
	public static PrintWriter getPrintWriter(int type) {
		if(printWriter[type]==null) {
			if(type==OUT) printWriter[OUT]=PRINTWRITER_OUT;
			else printWriter[ERR]=PRINTWRITER_ERR;
		}
		return printWriter[type];
	}
	public static boolean isCLICall() {
    	if(isCLI==null){
    		isCLI=Caster.toBoolean(System.getProperty("railo.cli.call"),Boolean.FALSE);
    	}
    	return isCLI.booleanValue();
	}
	
	public static double getLoaderVersion() {
		// this is done via reflection to make it work in older version, where the class railo.loader.Version does not exist
		if(loaderVersion==0D) {
			loaderVersion=4D;
			Class cVersion = ClassUtil.loadClass(TP.class.getClassLoader(),"railo.loader.Version",null);
			if(cVersion!=null) {
				try {
					Field f = cVersion.getField("VERSION");
					loaderVersion=f.getDouble(null);
				} 
				catch (Throwable t) {t.printStackTrace();}
			}
		}
		return loaderVersion;
	}
	public static String getMacAddress() {
		if(macAddress==null) {
			try{
				InetAddress ip = InetAddress.getLocalHost();
				NetworkInterface network = NetworkInterface.getByInetAddress(ip);
				byte[] mac = network.getHardwareAddress();
		  
				StringBuilder sb = new StringBuilder();
				for (int i = 0; i < mac.length; i++) {
					sb.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? "-" : ""));		
				}
				macAddress= sb.toString();
			}
			catch(Throwable t){}
			
		}
		return macAddress;
	}
}