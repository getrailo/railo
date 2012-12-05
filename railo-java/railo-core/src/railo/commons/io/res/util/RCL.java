package railo.commons.io.res.util;

import java.io.ByteArrayInputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import railo.commons.io.IOUtil;
import railo.commons.io.res.Resource;
import railo.commons.lang.SizeOf;
import railo.runtime.type.Sizeable;

public final class RCL extends ClassLoader implements Sizeable,Closeable {
    
    private final ClassLoader pcl;
	private ZipFile[] zips;
    
    /**
     * constructor of the class
     * @param file
     * @param parent
     * @throws IOException
     */
    public RCL(Resource[] files, ClassLoader parent) throws IOException {
        super(parent);
        this.pcl=parent;
        
        ArrayList list=new ArrayList();
        ZipFile zip;
        for(int i=0;i<files.length;i++){
        	zip=toZip(files[i]);
        	if(zip!=null)
        		list.add(zip);
        }
        zips=(ZipFile[]) list.toArray(new ZipFile[list.size()]);
    }
    
    private ZipFile toZip(Resource file) {
    	if(!file.exists())
            return null;
        if(!file.isFile())
            return null;
        if(!file.isReadable())
            return null;
        try {
			return new ZipFile(FileWrapper.toFile(file));
		} catch (Exception e) {
			return null;
		}
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
   public Class loadClass(String name) throws ClassNotFoundException   {
       return loadClass(name, false);
   }

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
    protected synchronized Class loadClass(String name, boolean resolve) throws ClassNotFoundException {
        // First, check if the class has already been loaded
        Class c = findLoadedClass(name);
        if (c == null) {
            c=findClassEL(name);
            if(c==null) {
                c =pcl.loadClass(name);
            }
        }
        if (resolve) {
            resolveClass(c);
        }
        return c;
   }
    
    @Override
    protected Class findClass(String name) throws ClassNotFoundException {
        Class clazz=findClassEL(name);
        if(clazz!=null) return clazz;
        throw new ClassNotFoundException("class "+name+" not found");
    }
    
    private Class findClassEL(String name) {
        byte[] barr = getBytes(name.replace('.','/').concat(".class"));
        if(barr!=null) {
            try {
                return defineClass(name,barr,0,barr.length);
            }
            catch(Throwable t) {}
        }
        return null;
    }

    @Override
    public InputStream getResourceAsStream(String name) {
        InputStream is = super.getResourceAsStream(name);
        if(is!=null) return is;
        
        byte[] barr = getBytes(name);
        if(barr!=null) return new ByteArrayInputStream(barr);
        
        return null;
    }
    
    @Override 
    public URL getResource(String name) { 
    	return null; 
    } 

    private byte[] getBytes(String name) {
    	ZipEntry entry=null;
    	for(int i=0;i<zips.length;i++){
    		entry= zips[i].getEntry(name);
    		if(entry!=null) {
    			return getBytes(zips[i], entry);
    		}
    	}
    	return null;
    }

    private byte[] getBytes(ZipFile zip,ZipEntry entry) {
    	
        if (entry == null)return null;
        
        int size= (int) entry.getSize();
        InputStream is=null;
        try {
            is= zip.getInputStream(entry);
            byte[] data= new byte[size];
            int pos= 0;
            while (pos < size) {
                int n= is.read(data, pos, data.length - pos);
                pos += n;
            }
            return data;
        }
        catch(IOException ioe) {}
        finally {
            IOUtil.closeEL(is);
        }
        return null;
    }

	@Override
	public long sizeOf() {
		return SizeOf.size(zips);
	}

	public void close() throws IOException {
		for(int i=0;i<zips.length;i++){
			closeEL(zips[i]);
		}
	}

	private static void closeEL(ZipFile zipFile) {
		try {
			if(zipFile!=null)zipFile.close();
		} catch (IOException e) {}
	} 
}
