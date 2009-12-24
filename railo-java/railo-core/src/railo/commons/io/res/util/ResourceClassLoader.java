package railo.commons.io.res.util;

import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

import railo.print;
import railo.commons.io.res.Resource;
import railo.commons.io.res.type.file.FileResource;
import railo.runtime.exp.PageException;

/**
 * Classloader that load classes from resources
 */
public final class ResourceClassLoader extends URLClassLoader {

	private Resource[] resources;

	/**
	 * Constructor of the class
	 * @param resources
	 * @throws PageException
	 */
	public ResourceClassLoader(Resource[] resources) throws IOException {
		super(doURLs(resources));
	}
	
	/**
	 * Constructor of the class
	 * @param reses
	 * @param parent
	 * @throws PageException
	 */
	public ResourceClassLoader(Resource[] resources, ClassLoader parent) throws IOException {
		super(doURLs(resources), parent);
		this.resources=resources;
	}

	/**
	 * @return the resources
	 */
	public Resource[] getResources() {
		return resources;
	}

	/**
	 * translate resources to url Objects
	 * @param reses
	 * @return
	 * @throws PageException
	 */
	private static URL[] doURLs(Resource[] reses) throws IOException {
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
	
	public void close(){}

	public void addResources(Resource[] reses) throws IOException {
		for(int i=0;i<reses.length;i++){
			addURL(doURL(reses[i]));
		}
	}

}
