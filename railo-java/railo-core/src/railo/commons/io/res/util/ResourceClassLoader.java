package railo.commons.io.res.util;

import java.io.Closeable;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.map.ReferenceMap;

import railo.commons.digest.MD5;
import railo.commons.io.res.Resource;
import railo.commons.io.res.type.file.FileResource;
import railo.runtime.exp.PageException;
import railo.runtime.type.util.ArrayUtil;

/**
 * Classloader that load classes from resources
 */
public final class ResourceClassLoader extends URLClassLoader implements Closeable {

	private List<Resource> resources=new ArrayList<Resource>();
	private Map<String,ResourceClassLoader> customCLs; 
	
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
	public ResourceClassLoader(Resource[] resources, ClassLoader parent) throws IOException {
		super(doURLs(resources), parent);
		for(int i=0;i<resources.length;i++){
			this.resources.add(resources[i]);
		}
	}
	
	public ResourceClassLoader(ClassLoader parent) {
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
			if(reses[i].isDirectory() || "jar".equalsIgnoreCase(ResourceUtil.getExtension(reses[i],null)))
				list.add(doURL(reses[i]));
		}
		return list.toArray(new URL[list.size()]);
	
	}
	private static URL doURL(Resource res) throws IOException {
			if(!(res instanceof FileResource))
				throw new IOException("resource ["+res.getPath()+"] must be a local file");
			return ((FileResource)res).toURL();
	}
	
	@Override
	public void close(){}

	public synchronized void addResourcesX(Resource[] reses) throws IOException {
		for(int i=0;i<reses.length;i++){
			if(!this.resources.contains(reses[i])){
				this.resources.add(reses[i]);
				addURL(doURL(reses[i]));
			}
		}
	}
	

	public ResourceClassLoader getCustomResourceClassLoader(Resource[] resources) throws IOException{
		if(ArrayUtil.isEmpty(resources)) return this;
		String key = hash(resources);
		ResourceClassLoader rcl=customCLs==null?null:customCLs.get(key);
		if(rcl!=null) return rcl; 
		
		resources=ResourceUtil.merge(this.getResources(), resources);
		rcl=new ResourceClassLoader(resources,getParent());
		if(customCLs==null)customCLs=new ReferenceMap();
		customCLs.put(key, rcl);
		return rcl;
	}

	public ResourceClassLoader getCustomResourceClassLoader2(Resource[] resources) throws IOException{
		if(ArrayUtil.isEmpty(resources)) return this;
		String key = hash(resources);
		ResourceClassLoader rcl=customCLs==null?null:customCLs.get(key);
		if(rcl!=null) return rcl; 
		
		rcl=new ResourceClassLoader(resources,this);
		if(customCLs==null)customCLs=new ReferenceMap();
		customCLs.put(key, rcl);
		return rcl;
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
