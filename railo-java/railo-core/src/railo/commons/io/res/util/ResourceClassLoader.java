package railo.commons.io.res.util;

import java.io.Closeable;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

import railo.commons.io.res.Resource;
import railo.commons.io.res.type.file.FileResource;
import railo.runtime.exp.PageException;

/**
 * Classloader that load classes from resources
 */
public final class ResourceClassLoader extends URLClassLoader implements Closeable {

	private List<Resource> resources=new ArrayList<Resource>();

	/* *
	 * Constructor of the class
	 * @param resources
	 * @throws PageException
	 
	ResourceClassLoader(Resource[] resources) throws IOException {
		super(doURLs(resources));
	}*/
	
	/**
	 * Constructor of the class
	 * @param reses
	 * @param parent
	 * @throws PageException
	 */
	ResourceClassLoader(Resource[] resources, ClassLoader parent) throws IOException {
		super(doURLs(resources), parent);
		for(int i=0;i<resources.length;i++){
			this.resources.add(resources[i]);
		}
	}
	
	ResourceClassLoader(ClassLoader parent) {
		super(new URL[0], parent);
	}

	/**
	 * @return the resources
	 */
	public Resource[] getResources() {
		return resources.toArray(new Resource[resources.size()]);
	}

	/**
	 * translate resources to url Objects
	 * @param reses
	 * @return
	 * @throws PageException
	 */
	public static URL[] doURLs(Resource[] reses) throws IOException {
		List<URL> list=new ArrayList<URL>();
		for(int i=0;i<reses.length;i++) {
			if(reses[i].isDirectory() || "jar".equalsIgnoreCase(ResourceUtil.getExtension(reses[i])))
				list.add(doURL(reses[i]));
		}
		return list.toArray(new URL[list.size()]);
	
	}
	private static URL doURL(Resource res) throws IOException {
			if(!(res instanceof FileResource))
				throw new IOException("resource ["+res.getPath()+"] must be a local file");
			return ((FileResource)res).toURL();
	}
	
	/**
	 * @see java.io.Closeable#close()
	 */
	public void close(){}

	public synchronized void addResources(Resource[] reses) throws IOException {
		
		for(int i=0;i<reses.length;i++){
			if(!this.resources.contains(reses[i])){
				this.resources.add(reses[i]);
				addURL(doURL(reses[i]));
			}
		}
	}

}
