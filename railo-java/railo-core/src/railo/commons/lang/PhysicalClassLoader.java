package railo.commons.lang;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Arrays;
import java.util.Map;

import org.apache.commons.collections.map.ReferenceMap;

import railo.commons.digest.MD5;
import railo.commons.io.IOUtil;
import railo.commons.io.SystemUtil;
import railo.commons.io.res.Resource;
import railo.commons.io.res.util.ResourceClassLoader;
import railo.commons.io.res.util.ResourceUtil;
import railo.runtime.config.Config;
import railo.runtime.engine.ThreadLocalPageContext;
import railo.runtime.type.Sizeable;
import railo.runtime.type.util.ArrayUtil;

/**
 * Directory ClassLoader
 */
public final class PhysicalClassLoader extends ExtendableClassLoader implements Sizeable  {
    
    private Resource directory;
    private ClassLoader parent;
	private int size=0;
	private int count;
	private Map<String,PhysicalClassLoader> customCLs; 
	
	/**
     * Constructor of the class
     * @param directory
     * @throws IOException
     */
    public PhysicalClassLoader(Resource directory) throws IOException {
        this(directory,getParentCL());
    }
    private static ClassLoader getParentCL() {
		Config config = ThreadLocalPageContext.getConfig();
		if(config!=null) return config.getClassLoader();
    	return new ClassLoaderHelper().getClass().getClassLoader();
	}

	/**
     * Constructor of the class
     * @param directory
     * @param parent
     * @throws IOException
     */
    public PhysicalClassLoader(Resource directory, ClassLoader parent) throws IOException {
        super(parent);
        this.parent=parent;
        if(!directory.isDirectory()) {
        	if(!directory.exists()) directory.mkdirs();
        	else throw new IOException("resource "+directory+" is not a directory");
        }
        if(!directory.canRead())
            throw new IOException("no access to "+directory+" directory");
        this.directory=directory;
    }
    
    /**
     * Loads the class with the specified name. This method searches for 
     * classes in the same manner as the {@link #loadClass(String, boolean)} 
     * method. It is called by the Java virtual machine to resolve class 
     * references. Calling this method is equivalent to calling 
     * <code>loadClass(name, false)</code>.
     *
     * @param     name the name of the class
     * @return    the resulting <code>Class</code> object
     * @exception ClassNotFoundException if the class was not found
     */
   public Class<?> loadClass(String name) throws ClassNotFoundException   {
       return loadClass(name, false);
   }//15075171

    /**
     * Loads the class with the specified name.  The default implementation of
     * this method searches for classes in the following order:<p>
     *
     * <ol>
     * <li> Call {@link #findLoadedClass(String)} to check if the class has
     *      already been loaded. <p>
     * <li> Call the <code>loadClass</code> method on the parent class
     *      loader.  If the parent is <code>null</code> the class loader
     *      built-in to the virtual machine is used, instead. <p>
     * <li> Call the {@link #findClass(String)} method to find the class. <p>
     * </ol>
     *
     * If the class was found using the above steps, and the
     * <code>resolve</code> flag is true, this method will then call the
     * {@link #resolveClass(Class)} method on the resulting class object.
     * <p>
     * From the Java 2 SDK, v1.2, subclasses of ClassLoader are 
     * encouraged to override
     * {@link #findClass(String)}, rather than this method.<p>
     *
     * @param     name the name of the class
     * @param     resolve if <code>true</code> then resolve the class
     * @return   the resulting <code>Class</code> object
     * @exception ClassNotFoundException if the class could not be found
     */
    protected synchronized Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
    	//if(!name.endsWith("$cf")) return super.loadClass(name, resolve); this break Webervices
    	// First, check if the class has already been loaded
        Class<?> c = findLoadedClass(name);
        //print.o("load:"+name+" -> "+c);
        if (c == null) {
            try {
            	c =parent.loadClass(name);
            } 
            catch (Throwable t) {
            	c = findClass(name);
            }
        }
        if (resolve) {
            resolveClass(c);
        }
        return c;
   }
    
    

    
    public static long lastModified(Resource res, long defaultValue)  {
    	InputStream in = null;
        try{
	        in=res.getInputStream();
	        byte[] buffer = new byte[10];
	    	in.read(buffer);
	    	if(!ClassUtil.hasCF33Prefix(buffer)) return defaultValue;
	    	
	    	 byte[] _buffer = new byte[]{
	    			 buffer[2],
	    			 buffer[3],
	    			 buffer[4],
	    			 buffer[5],
	    			 buffer[6],
	    			 buffer[7],
	    			 buffer[8],
	    			 buffer[9],
	    	 };
	    	
	    	
	    	return NumberUtil.byteArrayToLong(_buffer);
        }
        catch(IOException ioe){
        	return defaultValue;
        }
        finally {
        	IOUtil.closeEL(in);
        }
        
    }
    
    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
    	Resource res=directory.getRealResource(name.replace('.','/').concat(".class"));
    	ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            IOUtil.copy(res,baos,false);
        } 
        catch (IOException e) {//e.printStackTrace();
            throw new ClassNotFoundException("class "+name+" is invalid or doesn't exist");
        }
        
        byte[] barr=baos.toByteArray();
        size+=barr.length;
        count++;
        //print.o(name+":"+count+" -> "+(size/1024));
        IOUtil.closeEL(baos);
        return loadClass(name, barr);
        //return defineClass(name,barr,0,barr.length);
    }
    

    public Class<?> loadClass(String name, byte[] barr) {
    	int start=0;
    	if(ClassUtil.hasCF33Prefix(barr)) start=10;
    	size+=barr.length-start;
    	count++;
    	try {
    		return defineClass(name,barr,start,barr.length-start);
		} 
        catch (Throwable t) {
			SystemUtil.sleep(1);
			return defineClass(name,barr,start,barr.length-start);
		}
    	//return loadClass(name,false);
    }
    
    @Override
    public URL getResource(String name) {
        /*URL url=super.getResource(name);
        if(url!=null) return url;
        
        Resource f =_getResource(name);
        if(f!=null) {
            try {
                return f.toURL();
            } 
            catch (MalformedURLException e) {}
        }*/
        return null;
    }

    @Override
    public InputStream getResourceAsStream(String name) {
        InputStream is = super.getResourceAsStream(name);
        if(is!=null) return is;
        
        Resource f = _getResource(name);
        if(f!=null)  {
            try {
                return IOUtil.toBufferedInputStream(f.getInputStream());
            } 
            catch (IOException e) {}
        }
        return null;
    }

    /**
     * returns matching File Object or null if file not exust
     * @param name
     * @return matching file
     */
    public Resource _getResource(String name) {
        Resource f = directory.getRealResource(name);
        if(f!=null && f.exists() && f.isFile()) return f;
        return null;
    }

    public boolean hasClass(String className) {
        return hasResource(className.replace('.','/').concat(".class"));
    }
    
    public boolean isClassLoaded(String className) {
    	//print.o("isClassLoaded:"+className+"-"+(findLoadedClass(className)!=null));
        return findLoadedClass(className)!=null;
    }

    public boolean hasResource(String name) {
        return _getResource(name)!=null;
    }

	/**
	 * @return the directory
	 */
	public Resource getDirectory() {
		return directory;
	}

	@Override
	public long sizeOf() {
		return 0;
	}
	
	public int count() {
		return count;
	}
	
	// FUTURE add to interface
	public PhysicalClassLoader getCustomClassLoader(Resource[] resources, boolean reload) throws IOException{
		if(ArrayUtil.isEmpty(resources)) return this;
		String key = hash(resources);
		
		if(reload && customCLs!=null) customCLs.remove(key);
		
		
		PhysicalClassLoader pcl=customCLs==null?null:customCLs.get(key);
		if(pcl!=null) return pcl; 
		pcl=new PhysicalClassLoader(this.getDirectory(),new ResourceClassLoader(resources,getParent()));
		if(customCLs==null)customCLs=new ReferenceMap();
		customCLs.put(key, pcl);
		return pcl;
	}
	
	private String hash(Resource[] resources) {
		Arrays.sort(resources);
		StringBuilder sb=new StringBuilder();
		for(int i=0;i<resources.length;i++){
			sb.append(ResourceUtil.getCanonicalPathEL(resources[i]));
			sb.append(';');
		}
		return MD5.getDigestAsString(sb.toString(),null);
	}

}
