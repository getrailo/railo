/**
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either 
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public 
 * License along with this library.  If not, see <http://www.gnu.org/licenses/>.
 * 
 **/
package railo.loader.classloader;


import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Hashtable;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import railo.loader.util.Util;


/**
 *
 * This class implements a simple class loader 
 * that can be used to load at runtime 
 * classes contained in a JAR file.
 */
public final class RailoClassLoader extends ClassLoader {
    private Hashtable classes = new Hashtable();
    private Hashtable resources = new Hashtable();
    private ClassLoader pcl;
 
  /**
   * Creates a new JarClassLoader that will allow the loading
   * of classes stored in a jar file.
   *
   * @param jarFile   the name of the jar file
   * @param parent parent class loader
 * @throws IOException 
   * @exception IOException   an error happened while reading
   * the contents of the jar file
   */
    public RailoClassLoader(File jarFile, ClassLoader parent) throws IOException {  
    	this(new FileInputStream(jarFile),parent,isSecure(jarFile));
    }
    private static boolean isSecure(File jarFile) {
		if(jarFile.getName().toLowerCase().endsWith(".rc")) return false;
    	return true;
	}
	public RailoClassLoader(InputStream jar, ClassLoader parent, boolean secured) throws IOException {  
        super(parent);
        
        if(secured)
        	throw new IOException("secured core files are not supported");
        
        
        this.pcl=parent;
        ZipInputStream zis = new ZipInputStream(new BufferedInputStream(jar));
  
        try {
            byte[] buffer = new byte[0xffff];
            int bytes_read;
          
            ZipEntry ze;
            byte[] barr;
            while((ze = zis.getNextEntry()) != null) {
                if (!ze.isDirectory()) {
                    ByteArrayOutputStream baos=new ByteArrayOutputStream();
                    while((bytes_read = zis.read(buffer)) != -1)
                        baos.write(buffer, 0, bytes_read);
                    String name = ze.getName().replace('\\', '/');
                    barr=baos.toByteArray();
                    if(name.endsWith(".class")) {
                        String className=name.substring(0,name.length()-6);
                        className=className.replace('/','.');
                        classes.put(className,barr);
                    }
                    resources.put(name, barr);
                    zis.closeEntry();
                    baos.close();
                }
            }
        }
        finally {
            Util.closeEL(zis);
        }   
    }

	/**
     * Looks among the contents of the jar file (cached in memory)
     * and tries to find and define a class, given its name.
     *
     * @param className   the name of the class
     * @return   a Class object representing our class
     * @exception ClassNotFoundException   the jar file did not contain
     * a class named <code>className</code>
     */
    public Class findClass(String className) throws ClassNotFoundException {
        byte[] classBytes = (byte[])classes.get(className);
        if (classBytes == null) throw new ClassNotFoundException("class ["+className+"] not found");
        return defineClass(className, classBytes, 0, classBytes.length);
    }
    
    private Class findClassEL(String className) {
        byte[] classBytes = (byte[])classes.get(className);
        if (classBytes == null) return null;
        return defineClass(className, classBytes, 0, classBytes.length);
    }
     
     
     
     /**
      * Loads the class with the specified name. This method searches for 
      * classes in the same manner as the "loadClass(String, boolean)" 
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
                 c = pcl.loadClass(name);
             }
         }
         if (resolve) {
             resolveClass(c);
         }
         return c;
    }
     
    /**
     * @see java.lang.ClassLoader#getResourceAsStream(java.lang.String)
     */
   public InputStream getResourceAsStream(String name) {
       name = name.replace('\\', '/');
       
       byte[] bytes = (byte[])resources.get(name);
       if (bytes == null) return super.getResourceAsStream(name);
       return new ByteArrayInputStream(bytes);
   }
}

