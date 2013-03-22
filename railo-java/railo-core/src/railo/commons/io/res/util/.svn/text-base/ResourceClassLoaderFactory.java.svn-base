package railo.commons.io.res.util;

import java.io.IOException;

import railo.print;
import railo.commons.io.IOUtil;
import railo.commons.io.res.Resource;

public class ResourceClassLoaderFactory {

	private ClassLoader parent;
	private ResourceClassLoader rcl;
	
	
	public ResourceClassLoaderFactory(ClassLoader parent) {
		this.parent=parent;
	}

	/*public static ResourceClassLoader getResourceClassLoader(Resource[] resources) throws IOException{
		return new ResourceClassLoader(resources);, ClassLoader parent
	}*/
	

	public void addResources(Resource[] resources) throws IOException {
		if(rcl==null){
			rcl = new ResourceClassLoader(resources,parent);
			Thread.currentThread().setContextClassLoader(rcl);
		}
		else {
			rcl.addResources(resources);
		}
	}
	
	public ResourceClassLoader getResourceClassLoader(Resource[] resources) throws IOException{
		addResources(resources);
		return rcl;
	}
	
	public ResourceClassLoader getResourceClassLoader() {
		if(rcl==null){
			rcl = new ResourceClassLoader(parent);
			Thread.currentThread().setContextClassLoader(rcl);
		}
		return rcl;
	}

	public void reset() {
		IOUtil.closeEL(rcl);
	}

}
